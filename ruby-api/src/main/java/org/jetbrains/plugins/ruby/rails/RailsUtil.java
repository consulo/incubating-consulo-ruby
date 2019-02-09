/*
 * Copyright 2000-2008 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.ruby.rails;

import com.intellij.execution.RunManagerEx;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.Function;
import com.intellij.util.ThrowableRunnable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.gems.GemUtil;
import org.jetbrains.plugins.ruby.addins.gems.GemsRunner;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.StandardRailsPaths;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeId;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.RailsNode;
import org.jetbrains.plugins.ruby.rails.nameConventions.NamingConventions;
import org.jetbrains.plugins.ruby.rails.run.configuration.RailsRunConfigurationType;
import org.jetbrains.plugins.ruby.rails.run.filters.GeneratorsLinksFilter;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.run.*;
import org.jetbrains.plugins.ruby.ruby.run.filters.RFileLinksFilter;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.support.utils.IdeaInternalUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import org.jetbrains.yaml.YAMLFileType;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: 16.09.2006
 */
public class RailsUtil
{
	private static final Logger LOG = Logger.getInstance(RailsUtil.class.getName());

	/**
	 * Generates new rails app in the location
	 *
	 * @param module                Current modul, may be uncommited
	 * @param sdk                   Sdk because module may be uncommited
	 * @param location              Path, to generate rail application in
	 * @param overwrite             overwrite exisiting files or not
	 * @param preconfigureForDBName If not null rails will be preconfigured for DB with this name
	 * @param descriptorFactory     User Factory for creating non default run content descriptors
	 * @param onDone                Activity after application will be generated
	 */
	public static void generateRailsApp(final Module module, @Nonnull final Sdk sdk, @Nonnull final String location, final boolean overwrite, @Nullable final String preconfigureForDBName, final RunContentDescriptorFactory descriptorFactory, @Nullable final Runnable onDone)
	{
		final Project project = module.getProject();
		final String moduleName = module.getName();
		final String progressTitle = RBundle.message("module.rails.generateapp.progress.title", moduleName);
		final String errorTitle = RBundle.message("execution.error.title.generate.rails.app", moduleName);

		final LinkedList<String> generatorArgsList = new LinkedList<String>();
		generatorArgsList.add(location);
		generatorArgsList.add(overwrite ? RailsConstants.PARAM_FORCE_OVERWRITE : RailsConstants.PARAM_SKIP);
		if(preconfigureForDBName != null)
		{
			generatorArgsList.add(RailsConstants.PARAM_DATABASE + "=" + preconfigureForDBName);
		}

		final RubyScriptRunnerArgumentsProvider provider = new RubyScriptRunnerArgumentsProvider(generatorArgsList.toArray(new String[generatorArgsList.size()]), null, null);
		final Filter[] filters = {
				new RFileLinksFilter(module, null),
				new GeneratorsLinksFilter(module)
		};

		try
		{
			// run in with progress
			GemsRunner.runGemScriptInConsoleAndRefreshModule(module, sdk, progressTitle, null, true, RailsConstants.RAILS_GEM_EXECUTABLE, null, provider, descriptorFactory, filters, onDone);

		}
		catch(Exception exp)
		{
			RubyScriptRunner.showErrorMessage(project, errorTitle, exp);
		}
	}

	/**
	 * Checks for rails app at the location
	 *
	 * @param railsAppHomePath Path, to check
	 * @return true if all rails application core files is already presents in location
	 */
	public static boolean containsRailsApp(@Nullable final String railsAppHomePath)
	{
		if(railsAppHomePath == null)
		{
			return false;
		}
		File src = new File(railsAppHomePath);
		if(!src.exists())
		{
			return false;
		}
		for(String coreFile : RailsConstants.RAILS_APP_CORE_FILES)
		{
			File file = new File(railsAppHomePath + File.separatorChar + coreFile);
			if(!file.exists())
			{
				return false;
			}
		}
		return true;
	}


	public static boolean hasRailsSupportInSDK(@Nullable final Sdk sdk)
	{
		return RubySdkUtil.isKindOfRubySDK(sdk) && GemUtil.isGemExecutableRubyScriptExists(sdk, RailsConstants.RAILS_GEM_EXECUTABLE);
	}

	/**
	 * Creates Rails Server Run Configuration
	 *
	 * @param module Ruby or Java module
	 */
	public static void createRailsRunConfiguration(final Module module)
	{
		final Project project = module.getProject();

		final String title = RBundle.message("rails.facet.builder.run.configuration.server.creating");
		final Task task = new Task.Backgroundable(project, title, true)
		{
			@Override
			public void run(ProgressIndicator indicator)
			{
				if(indicator != null)
				{
					indicator.setText(RBundle.message("progress.backgnd.indicator.title.please.wait", getTitle()));
				}

				final RunManagerEx runManagerEx = RunManagerEx.getInstanceEx(project);

				IdeaInternalUtil.runInsideReadAction(new ThrowableRunnable<Exception>()
				{
					@Override
					public void run() throws Exception
					{
						// requires read action
						final RunnerAndConfigurationSettings settings = RailsRunConfigurationType.getInstance().getWEBrickFactory().createRunConfigurationSettings(module);

						runManagerEx.addConfiguration(settings, false);
						runManagerEx.setActiveConfiguration(settings);
					}
				});
			}
		};

		final Runnable taskRunner = new Runnable()
		{
			@Override
			public void run()
			{
				ProgressManager.getInstance().run(task);
			}
		};
		if(!project.isInitialized())
		{
			StartupManager.getInstance(project).registerPostStartupActivity(taskRunner);
		}
		else
		{
			taskRunner.run();
		}
	}

	/**
	 * Gets array of rails moduls in project
	 *
	 * @param project project
	 * @return array of moduls
	 */
	@Nonnull
	public static Module[] getAllModulesWithRailsSupport(final @Nonnull Project project)
	{
		final List<Module> runableModules = new LinkedList<Module>();
		Module[] allModules = ModuleManager.getInstance(project).getModules();
		for(Module module : allModules)
		{
			if(RailsFacetUtil.hasRailsSupport(module))
			{
				runableModules.add(module);
			}
		}
		return runableModules.toArray(new Module[runableModules.size()]);
	}

	/**
	 * Uses BaseRails Facet configuration
	 *
	 * @param fileUrl url for file
	 * @param module  rails module
	 * @return relative path in the module or null
	 */
	@Nullable
	public static String getPathRelativeToTailsApplicationRoot(@Nonnull final String fileUrl, @Nonnull final Module module)
	{

		final String appHomeUrl = RailsFacetUtil.getRailsAppHomeDirPathUrl(module);
		assert appHomeUrl != null; //can't be null for module with rails support!

		return VirtualFileUtil.getRelativePath(fileUrl, appHomeUrl);
	}

	public static boolean isValidRailsFSPath(@Nonnull final String string)
	{
		final StringTokenizer st = new StringTokenizer(string, String.valueOf(VirtualFileUtil.VFS_PATH_SEPARATOR));
		boolean isValid = string.length() > 0;
		while(st.hasMoreTokens() && isValid)
		{
			final String token = st.nextToken();
			isValid = NamingConventions.isInMixedCase(token) || NamingConventions.isInUnderscoredCase(token);
		}
		return isValid;
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getModelRoot(Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getModelRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getHelpersRoot(Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getHelpersRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getTestsRoot(Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getTestsStdUnitRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}


	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getComponentsRoot(@Nonnull Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getComponentsRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getApisRoot(@Nonnull Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getApisRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getConfigRoot(@Nonnull Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getConfigRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getAppRoot(@Nonnull Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getAppRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getTestMockEnviromentRoot(@Nonnull Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getTestMockEnviromentRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getVendorRoot(@Nonnull Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getVendorRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getPluginsRoot(@Nonnull Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getPluginsRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getEdgeRailsRoot(@Nonnull Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getEdgeRailsRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getLibsRoot(@Nonnull Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getLibsRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getServicesRoot(@Nonnull Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getServicesRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getScriptsRoot(@Nonnull Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getScriptsRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getControllersRoot(@Nonnull Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getControllerRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	/**
	 * @param module Module
	 * @return null for modules without Rails Support
	 */
	@Nullable
	public static VirtualFile getViewsRoot(@Nonnull Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String url = settings.getViewsRootURL();
		return VirtualFileManager.getInstance().findFileByUrl(url);
	}

	public static boolean isYMLFile(final String fileName)
	{
		final FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName(fileName);
		return fileType instanceof YAMLFileType;
	}

	/**
	 * If RailsNode is associated with Directory method returns corresponding
	 * PsiDirectory. If node is associated with File metod returns PsiDirectory
	 * for file parent directory.
	 *
	 * @param node    RailsNode
	 * @param project Project
	 * @return PsiDirectory or null
	 */
	@Nullable
	public static PsiDirectory getPsiDirByRailsNode(@Nullable final RailsNode node, @Nonnull final Project project)
	{
		if(node == null)
		{
			return null;
		}

		final VirtualFile file = node.getVirtualFile();
		if(file != null)
		{
			if(file.isDirectory())
			{
				return PsiManager.getInstance(project).findDirectory(file);
			}
			final VirtualFile parent = file.getParent();
			if(parent != null)
			{
				return PsiManager.getInstance(project).findDirectory(parent);
			}
		}
		return null;
	}

	/**
	 * @param node    Node
	 * @param project Project
	 * @return PsiFile or null
	 */
	@Nullable
	public static PsiFile getPsiFileByRailsNode(@Nullable final RailsNode node, @Nonnull final Project project)
	{
		if(node == null)
		{
			return null;
		}

		final VirtualFile file = node.getVirtualFile();
		if(file != null && file.isValid())
		{
			return PsiManager.getInstance(project).findFile(file);
		}
		return null;
	}

	@Nullable
	public static PsiElement getPsiElementByRailsNode(@Nullable final RailsNode node, @Nonnull final Project project)
	{
		if(node == null)
		{
			return null;
		}
		final VirtualFile file = node.getVirtualFile();
		return getPsiElementByNodeId(node.getElement(), file, project);
	}

	@Nullable
	public static PsiElement getPsiElementByNodeId(@Nonnull final NodeId nodeId, @Nullable final VirtualFile file, @Nonnull final Project project)
	{
		if(file != null && file.isValid())
		{
			if(file.isDirectory())
			{
				return PsiManager.getInstance(project).findDirectory(file);
			}
			final RVirtualContainer rContainer = nodeId.getRContainer();
			if(rContainer == null)
			{
				return file.isValid() ? PsiManager.getInstance(project).findFile(file) : null;
			}
			return RVirtualPsiUtil.findInPsi(project, rContainer);
		}
		return null;
	}

	/**
	 * Converts virtual file system path to modules path according rails
	 * naming conventions.
	 * For example:
	 * server1/admin/login_module -> Server1::Admin::LoginModule
	 *
	 * @param fsPath path in virtual file system
	 * @return modules path. If fsPath is null method returns "".
	 */
	@Nonnull
	public static String toModulesPath(@Nullable final String fsPath)
	{
		if(TextUtil.isEmpty(fsPath))
		{
			return TextUtil.EMPTY_STRING;
		}
		final StringTokenizer sTokenizer = new StringTokenizer(fsPath, String.valueOf(VirtualFileUtil.VFS_PATH_SEPARATOR));
		final StringBuilder buff = new StringBuilder();
		while(sTokenizer.hasMoreTokens())
		{
			if(buff.length() != 0)
			{
				buff.append(RubyUtil.MODULES_PATH_SEPARATOR);
			}
			buff.append(NamingConventions.toMixedCase(sTokenizer.nextToken()));
		}
		return buff.toString();
	}

	public static String getGemExecutablePath(Sdk sdk)
	{
		return RubySdkUtil.getBinPath(sdk) + File.separator + RailsConstants.GEM_EXECUTABLE;
	}

	@Nullable
	public static String getRailsVersion(@Nonnull final Sdk sdk, final boolean runWithModalProgress, @Nullable final Function<Object, Boolean> shouldCancelFun)
	{
		final Output output;
		try
		{
			final String progressTitle = RBundle.message("execution.get.rails.gem.version");
			final Runner.ExecutionMode mode;
			if(runWithModalProgress)
			{
				mode = new Runner.ModalProgressMode(progressTitle);
			}
			else
			{
				mode = new Runner.SameThreadMode(progressTitle);
			}
			if(shouldCancelFun != null)
			{
				mode.setShouldCancelFun(shouldCancelFun);
			}
			output = GemsRunner.runGemsExecutableScript(sdk, null, RailsConstants.RAILS_GEM_EXECUTABLE, null, mode, false, null, RailsConstants.PARAM_VERSION);
		}
		catch(Exception e)
		{
			LOG.error(e);
			return null;
		}
		if(output == null)
		{
			return null;
		}
		final String stdout = output.getStdout();
		if(TextUtil.isEmpty(stdout) || !TextUtil.isEmpty(output.getStderr()))
		{
			return null;
		}
		return stdout;
	}


}
