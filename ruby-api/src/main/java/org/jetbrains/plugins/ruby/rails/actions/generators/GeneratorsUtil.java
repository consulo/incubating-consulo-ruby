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

package org.jetbrains.plugins.ruby.rails.actions.generators;

import static org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorOptions.Option;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.JCheckBox;

import org.jetbrains.annotations.NonNls;

import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.actions.generators.lexer.OutputLexer;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfigurationImpl;
import org.jetbrains.plugins.ruby.rails.run.RailsScriptRunner;
import org.jetbrains.plugins.ruby.rails.run.filters.GeneratorsLinksFilter;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.ExecutionHelper;
import org.jetbrains.plugins.ruby.ruby.run.Output;
import org.jetbrains.plugins.ruby.ruby.run.RubyScriptRunner;
import org.jetbrains.plugins.ruby.ruby.run.RubyScriptRunnerArgumentsProvider;
import org.jetbrains.plugins.ruby.ruby.run.RunContentDescriptorFactory;
import org.jetbrains.plugins.ruby.ruby.run.Runner;
import org.jetbrains.plugins.ruby.ruby.run.filters.RFileLinksFilter;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.settings.RProjectUtil;
import org.jetbrains.plugins.ruby.support.utils.IdeaInternalUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 29.11.2006
 */
public class GeneratorsUtil
{
	@NonNls
	public final static String GENERATORS_DIR = "generators";
	@NonNls
	public final static String GENERATE_SCRIPT = "script/generate";
	@NonNls
	public static final String BACKTRACE_CMD_OPTION = "-t";
	@NonNls
	public static final String FORCE_CMD_OPTION = "-f";
	@NonNls
	public static final String PRETEND_CMD_OPTION = "-p";
	@NonNls
	public static final String SKIP_CMD_OPTION = "-s";
	@NonNls
	public static final String SVN_CMD_OPTION = "-c";

	protected final static Logger LOG = Logger.getInstance(GeneratorsUtil.class.getName());

	private final static String[] EMPTY_STRING_ARRAY = new String[0];

	/**
	 * List of installed generators names.
	 * For example:
	 * <p/>
	 * >ruby script/generate
	 * ...
	 * Installed Generators
	 * User: goldberg
	 * Rubygems: site
	 * Builtin: controller, integration_test
	 * ...
	 * <p/>
	 * For this method will return {"goldberg", "site", "controller", "integration_test"}
	 *
	 * @param project                Project
	 * @param sdk                    SDK with rails support
	 * @param moduleName             Module name
	 * @param railsApplicHomeDirPath Rails Application Home Directory
	 * @return array of installed generators
	 */
	@Nonnull
	public static String[] getInstalledGenerators(@Nullable final Project project, @Nullable final Sdk sdk, @Nonnull final String moduleName, @Nonnull final String railsApplicHomeDirPath)
	{
		final List<Exception> exceptions = new LinkedList<Exception>();
		String[] generators = EMPTY_STRING_ARRAY;

		final String generateScriptPath = railsApplicHomeDirPath + VirtualFileUtil.VFS_PATH_SEPARATOR + GENERATE_SCRIPT;

		final Output output = getGenerateScriptOutput(project, sdk, railsApplicHomeDirPath, exceptions, false);
		if(output != null)
		{
			final String buff = output.getStdout();
			try
			{
				if(!TextUtil.isEmpty(output.getStderr()))
				{
					final String msg = RBundle.message("execution.ruby.script.get.available.generators", output.getStderr());

					//noinspection ThrowableInstanceNeverThrown
					exceptions.add(new Exception(msg));
					LOG.warn(msg);
				}
				final OutputLexer ouputLexer = new OutputLexer((Reader) null);
				ouputLexer.reset(buff, 0, buff.length(), 0);
				String token = ouputLexer.advance();
				final LinkedList<String> tokens = new LinkedList<String>();
				while(token != null)
				{
					tokens.add(token);
					token = ouputLexer.advance();
				}
				Collections.sort(tokens);
				generators = tokens.toArray(new String[tokens.size()]);
			}
			catch(IOException e)
			{
				exceptions.add(e);
				LOG.warn(e);
			}
			catch(Error err)
			{
				final String msg = "Parsing error. Unknown data format.\n" + "stdout:\n" + buff + "stderr:\n" + output.getStderr() + "Error message:\n" + err;
				//noinspection ThrowableInstanceNeverThrown
				exceptions.add(new Exception(msg));
				LOG.warn(msg);
			}
		}
		//show errors
		if(!exceptions.isEmpty())
		{
			final VirtualFile url = VirtualFileUtil.findFileByLocalPath(generateScriptPath);
			ExecutionHelper.showErrors(project, exceptions, RBundle.message("execution.error.title.generators.list", moduleName), url);
		}
		return generators;
	}

	@Nullable
	public static Output getGenerateScriptOutput(@Nullable final Project project, @Nullable final Sdk sdk, final String workingDir, List<Exception> exceptions, final boolean showStdErrErrors)
	{
		try
		{
			return RubyScriptRunner.runRubyScript(sdk, project, GENERATE_SCRIPT, workingDir, new Runner.SameThreadMode(), showStdErrErrors, null);
		}
		catch(Exception exp)
		{
			exceptions.add(exp);
			return null;
		}
	}

	/**
	 * Loads available generators list to rails module settings from cache. If file with list of generators
	 * doesn't exist method creates list and saves it in file system.
	 *
	 * @param forceRegenerate         if true rake tasks list must be regenerated.
	 * @param project                 Project
	 * @param sdk                     SDK with rails
	 * @param moduleName              Module name
	 * @param railsFacetConfiguration Settings
	 */
	public static void loadGeneratorsList(final boolean forceRegenerate, @Nullable final Project project, @Nullable final Sdk sdk, @Nonnull final String moduleName, @Nonnull final BaseRailsFacetConfigurationImpl railsFacetConfiguration)
	{
		final String title = RBundle.message("module.rails.create.rake.generators.title");

		final String railsApplicHomeDirPath = railsFacetConfiguration.getRailsApplicationRootPath();

		final Task task = new Task.Backgroundable(project, title, true)
		{
			@Override
			public void run(final ProgressIndicator indicator)
			{
				final GeneratorsExternalizer generatorsExt = new GeneratorsExternalizer();
				String[] generators = generatorsExt.loadGeneratorList(railsApplicHomeDirPath);
				if(generators == null || forceRegenerate)
				{
					if(!RubySdkUtil.isSDKValid(sdk))
					{
						railsFacetConfiguration.setGenerators(null);
						return;
					}
					generators = getInstalledGenerators(project, sdk, moduleName, railsApplicHomeDirPath);
					generatorsExt.saveGeneratorList(generators, railsApplicHomeDirPath);
				}
				railsFacetConfiguration.setGenerators(generators);
			}

			@Override
			public boolean shouldStartInBackground()
			{
				return true;
			}
		};
		IdeaInternalUtil.runInEventDispatchThread(new Runnable()
		{
			@Override
			public void run()
			{
				// Must be executed in EDT
				ProgressManager.getInstance().run(task);
			}
		}, ModalityState.defaultModalityState());
	}

	public static boolean checkIfGenerateScriptExists(@Nonnull final Module module)
	{
		final String railsAppHomePath = RailsFacetUtil.getRailsAppHomeDirPath(module);
		if(railsAppHomePath == null)
		{
			return false;
		}

		final String generateScriptPath = railsAppHomePath + VirtualFileUtil.VFS_PATH_SEPARATOR + GENERATE_SCRIPT;
		final Ref<VirtualFile> file = new Ref<VirtualFile>();

		IdeaInternalUtil.runInsideWriteAction(new ThrowableRunnable<Exception>()
		{
			@Override
			public void run() throws Exception
			{
				file.set(LocalFileSystem.getInstance().refreshAndFindFileByPath(generateScriptPath));
			}
		});
		return file.get() != null;
	}

	/**
	 * @param file File or Directory
	 * @param ts   Time stamp
	 * @return Checks if exists file newer than given timestamp in subdirectories of file.
	 */
	public static boolean existsNewerThanTimeStamp(@Nullable final VirtualFile file, final long ts)
	{
		if(file == null)
		{
			return false;
		}
		if(file.getTimeStamp() > ts)
		{
			return true;
		}
		if(!file.isDirectory())
		{
			return false;
		}
		// If file is directory
		for(VirtualFile child : file.getChildren())
		{
			if(existsNewerThanTimeStamp(child, ts))
			{
				return true;
			}
		}
		return false;
	}

	public static SerializableGenerator findGenerator(final String name, final boolean isGroup, final SerializableGenerator parent)
	{
		for(SerializableGenerator child : parent.getChildren())
		{
			if(child.getName().equals(name) && child.isGroup() == isGroup)
			{
				return child;
			}
		}
		return null;
	}

	public static void initOptionsCheckBoxes(@Nonnull final JCheckBox pretendCheckBox, @Nonnull final JCheckBox forceCheckBox, @Nonnull final JCheckBox skipCheckBox, @Nonnull final JCheckBox backtraceCheckBox, @Nonnull final JCheckBox svnCheckBox, @Nonnull final GeneratorOptions options)
	{
		pretendCheckBox.setSelected(options.containsValue(Option.PRETEND));
		forceCheckBox.setSelected(options.containsValue(Option.FORCE));
		skipCheckBox.setSelected(options.containsValue(Option.SKIP));
		backtraceCheckBox.setSelected(options.containsValue(Option.BACK_TRACE));

		svnCheckBox.setEnabled(options.containsValue(Option.SVN_SHOW_CONFIRMATION));
		svnCheckBox.setSelected(options.containsValue(Option.SVN));
	}

	public static void saveSettings(@Nonnull final JCheckBox pretendCheckBox, @Nonnull final JCheckBox forceCheckBox, @Nonnull final JCheckBox skipCheckBox, @Nonnull final JCheckBox backtraceCheckBox, @Nonnull final JCheckBox svnCheckBox, @Nonnull final GeneratorOptions options, @Nonnull final Project project)
	{
		options.setOption(Option.PRETEND, pretendCheckBox.isSelected());
		options.setOption(Option.FORCE, forceCheckBox.isSelected());
		options.setOption(Option.SKIP, skipCheckBox.isSelected());
		options.setOption(Option.BACK_TRACE, backtraceCheckBox.isSelected());

		if(RProjectUtil.isVcsAddShowConfirmation(project))
		{
			options.setOption(Option.SVN, svnCheckBox.isSelected());
		}
	}

	public static String calcGeneralOptionsString(@Nonnull final JCheckBox backtraceCheckBox, @Nonnull final JCheckBox forceCheckBox, @Nonnull final JCheckBox pretendCheckBox, @Nonnull final JCheckBox skipCheckBox, @Nonnull final JCheckBox svnCheckBox)
	{
		final StringBuilder buff = new StringBuilder();

		if(backtraceCheckBox.isSelected())
		{
			buff.append(BACKTRACE_CMD_OPTION);
			buff.append(" ");
		}

		if(forceCheckBox.isSelected())
		{
			buff.append(FORCE_CMD_OPTION);
			buff.append(" ");
		}

		if(pretendCheckBox.isSelected())
		{
			buff.append(PRETEND_CMD_OPTION);
			buff.append(" ");
		}

		if(skipCheckBox.isSelected())
		{
			buff.append(SKIP_CMD_OPTION);
			buff.append(" ");
		}
		if(svnCheckBox.isSelected())
		{
			buff.append(SVN_CMD_OPTION);
			buff.append(" ");
		}
		return buff.toString();
	}

	public static String filterIoExceptionMessage(String message)
	{
		if(message == null)
		{
			return null;
		}
		@NonNls final String ioExceptionPrefix = "java.io.IOException:";
		if(message.startsWith(ioExceptionPrefix))
		{
			message = message.substring(ioExceptionPrefix.length());
		}
		return message;
	}


	public static void invokeGenerator(final Module uncommitedModule, final String processTitle, final String errorTitle, final String[] scriptParameters, @Nullable final
	RunContentDescriptorFactory descFactory, @Nullable final ThrowableRunnable<Exception> nextAction, @Nonnull final Sdk sdk)
	{
		try
		{
			//Save all opened documents
			FileDocumentManager.getInstance().saveAllDocuments();

			final ProcessListener listener = new ProcessAdapter()
			{
				@Override
				public void processTerminated(final ProcessEvent event)
				{
					// Synchronize files
					RailsFacetUtil.refreshRailsAppHomeContent(uncommitedModule);
					if(nextAction != null)
					{
						IdeaInternalUtil.runInsideWriteAction(nextAction);
					}
				}
			};
			final String title = uncommitedModule.getName() + ": " + processTitle;
			final RubyScriptRunnerArgumentsProvider provider = new RubyScriptRunnerArgumentsProvider(scriptParameters, null, null);

			RailsScriptRunner.runRailsScriptInCosole(sdk, uncommitedModule, listener, new Filter[]{
					new RFileLinksFilter(uncommitedModule),
					new GeneratorsLinksFilter(uncommitedModule)
			}, null, true, title, provider, descFactory);
		}
		catch(Exception exp)
		{
			RubyScriptRunner.showErrorMessage(uncommitedModule.getProject(), errorTitle, exp);
		}
	}
}