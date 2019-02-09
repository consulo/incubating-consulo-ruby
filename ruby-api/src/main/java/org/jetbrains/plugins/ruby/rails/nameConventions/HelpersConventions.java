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

package org.jetbrains.plugins.ruby.rails.nameConventions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NonNls;

import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.StandardRailsPaths;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 08.05.2007
 */
public class HelpersConventions
{

	@NonNls
	public static String APPLICATION_HELPER = "ApplicationHelper";

	@NonNls
	public static final String ACTION_VIEW = RailsConstants.SDK_ACTION_VIEW;

	@NonNls
	public static final String ACTION_VIEW_NAME = "ActionView";
	@NonNls
	public static final String HELPERS_NAME = "Helpers";
	@NonNls
	public static final String HELPER = RailsConstants.HELPERS_MODULE_NAME_SUFFIX;

	public static final List<String> ACTION_VIEW_PATH = Arrays.asList(ACTION_VIEW_NAME, HELPERS_NAME);

	public static boolean isHelperFile(@Nonnull final RVirtualFile rFile, @Nullable final Module module)
	{
		return isHelperFile(rFile, module, RContainerUtil.getTopLevelModules(rFile));
	}

	public static boolean isHelperFile(@Nonnull final RVirtualFile rFile, @Nullable final Module module, @Nonnull final List<RVirtualModule> modules)
	{
		if(module == null)
		{
			return false;
		}

		final String fileUrl = rFile.getContainingFileUrl();
		final String fileName = VirtualFileUtil.removeExtension(rFile.getName());
		if(!fileName.endsWith(RailsConstants.HELPERS_FILE_NAME_SUFFIX))
		{
			return false;
		}

		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for Rails Module

		final String helpersRoot = railsPaths.getHelpersRootURL();
		if(!fileUrl.startsWith(helpersRoot))
		{
			return false;
		}

		for(RVirtualModule virtualModule : modules)
		{
			if(isHelperModule(virtualModule, module))
			{
				return true;
			}
		}
		return false;
	}

	@Nullable
	public static String getHelperNameByModuleName(@Nullable RVirtualModule rModule)
	{
		if(rModule == null)
		{
			return null;
		}
		//TODO may be rVClass.getClassName.get..
		return getHelperNameByModuleName(rModule.getName());
	}

	@Nullable
	public static String getHelperNameByModuleName(@Nullable final String className)
	{
		if(className == null)
		{
			return null;
		}
		final String name = NamingConventions.toUnderscoreCase(className);
		if(!name.endsWith(RailsConstants.HELPERS_FILE_NAME_SUFFIX))
		{
			return null;
		}
		return name.substring(0, name.length() - RailsConstants.HELPERS_FILE_NAME_SUFFIX.length() - 1);
	}

	public static boolean isHelperModule(@Nullable final RVirtualModule rModule, @Nonnull final Module module)
	{
		if(!RailsFacetUtil.hasRailsSupport(module) || getHelperNameByModuleName(rModule) == null)
		{
			return false;
		}

		/**
		 * TODO
		 *
		 * Uncomment next block when virtual file cache will be able
		 * to parse complicated class names(such as Module1:Module2:ClassName)
		 * into RVirtualElements hierarchy
		 */

          /*  final ArrayList<RVirtualContainer> path = RVirtualPsiUtils.getVirtualPath(rClass);
			final String controllersRootURL = getControllersRootURL(module);
            assert controllersRootURL != null; // for rails modules isn't null

            final StringBuffer buff = new StringBuffer(controllersRootURL);
            for (RVirtualContainer container : path) {
                if (container instanceof RVirtualFile) {
                    continue;
                }
                buff.append(VFS_PATH_SEPARATOR);
                buff.append(RailsUtil.getControllerFileName(container.getName()));
            }
        */
		assert rModule != null; // for rails modules isn't null
		final List<String> path = rModule.getFullPath();
		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null;

		final String helpersRootUrl = railsPaths.getHelpersRootURL();

		final StringBuilder buff = new StringBuilder(helpersRootUrl);
		for(String name : path)
		{
			buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
			buff.append(NamingConventions.toUnderscoreCase(name));
		}
		buff.append('.').append(RubyFileType.INSTANCE.getDefaultExtension());
		return buff.toString().equals(rModule.getContainingFileUrl());
	}

	@Nonnull
	public static List<VirtualFile> getBuiltInHelpers(@Nonnull final String actionViewFileUrl)
	{
		final VirtualFileManager manager = VirtualFileManager.getInstance();
		final VirtualFile helpersDir = manager.findFileByUrl(VirtualFileUtil.removeExtension(actionViewFileUrl));
		if(helpersDir == null)
		{
			return Collections.emptyList();
		}
		final Set<VirtualFile> files = new HashSet<VirtualFile>();
		RubyVirtualFileScanner.addRubyFiles(helpersDir, files);
		final ArrayList<VirtualFile> filesList = new ArrayList<VirtualFile>();
		for(VirtualFile virtualFile : files)
		{
			if(virtualFile.getNameWithoutExtension().endsWith(RailsConstants.HELPERS_FILE_NAME_SUFFIX))
			{
				// check only by file name conventions
				filesList.add(virtualFile);
			}
		}
		return filesList;
	}

	public static VirtualFile getApplicationHelperFile(final Module module)
	{
		final String controllerUrl = getApplicationHelperUrl(module);
		return controllerUrl == null ? null : VirtualFileManager.getInstance().findFileByUrl(controllerUrl);
	}

	@Nullable
	public static String getApplicationHelperUrl(final Module module)
	{
		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		if(railsPaths == null)
		{
			return null;
		}
		final String root = railsPaths.getHelpersRootURL();
		return root + VirtualFileUtil.VFS_PATH_SEPARATOR + RailsConstants.APPLICATION_HELPER_FILE_NAME + "." + RubyFileType.INSTANCE.getDefaultExtension();
	}

	@Nonnull
	public static String getHelperFileName(@Nullable final String controllerClassName)
	{
		final String name = ControllersConventions.getControllerNameByClassName(controllerClassName);
		if(name == null)
		{
			return TextUtil.EMPTY_STRING;
		}
		final StringBuilder buff = new StringBuilder(name);
		buff.append(RailsConstants.HELPERS_FILE_NAME_SUFFIX);
		buff.append('.');
		buff.append(RubyFileType.INSTANCE.getDefaultExtension());
		return buff.toString();
	}

	@Nonnull
	public static String getHelperModuleName(@Nullable final String controllerClassName)
	{
		final String name = ControllersConventions.getControllerNameByClassName(controllerClassName);
		if(name == null)
		{
			return TextUtil.EMPTY_STRING;
		}
		final StringBuilder buff = new StringBuilder(NamingConventions.toMixedCase(name));
		buff.append(RailsConstants.HELPERS_MODULE_NAME_SUFFIX);
		return buff.toString();
	}

	@Nullable
	public static String getHelperURL(@Nonnull final String controllerDirUrl, @Nonnull final String controllerName, final Module module)
	{

		final String path = ControllersConventions.getRelativePathOfControllerFolder(controllerDirUrl, module);
		if(path == null)
		{
			return null;
		}

		final StringBuilder buff = new StringBuilder();
		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null;
		buff.append(railsPaths.getRailsApplicationRootURL());
		buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
		buff.append(RailsConstants.HELPERS_PATH);
		if(!TextUtil.isEmpty(path))
		{
			buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
			buff.append(path);
		}
		buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
		buff.append(controllerName);
		buff.append(RailsConstants.HELPERS_FILE_NAME_SUFFIX);
		buff.append(".");
		buff.append(RubyFileType.INSTANCE.getDefaultExtension());
		return buff.toString();
	}

	/**
	 * @param rFile               Ruby file with controller
	 * @param controllerClassName Controller's class name
	 * @return Ruby helper module
	 */
	@Nullable
	public static RVirtualModule getHelperModule(@Nullable final RVirtualFile rFile, @Nonnull final String controllerClassName)
	{
		if(rFile == null)
		{
			return null;
		}

		//TODO Use navigator
		final String moduleName = getHelperModuleName(controllerClassName);

		for(RVirtualStructuralElement element : RContainerUtil.selectVirtualElementsByType(rFile.getVirtualStructureElements(), StructureType.MODULE))
		{
			assert element instanceof RVirtualModule;
			final RVirtualModule module = (RVirtualModule) element;
			if(moduleName.equals(module.getName()))
			{
				return module;
			}
		}
		return null;
	}

	@Nullable
	public static String getRelativePathOfHelperFolder(@Nullable final String fileUrl, @Nonnull final Module module)
	{
		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null;
		final String root = railsPaths.getHelpersRootURL();
		if(fileUrl == null)
		{
			return null;
		}
		return VirtualFileUtil.getRelativePath(fileUrl, root);
	}

	public static boolean isApplicationHelperFile(final VirtualFile helper)
	{
		return (RailsConstants.APPLICATION_NAME + RailsConstants.HELPERS_FILE_NAME_SUFFIX).equals(helper.getNameWithoutExtension());
	}

	/**
	 * Returns short Helper module name of corresponding contoller. E.g
	 * Admin::HelloController -> HelloHelper
	 *
	 * @param controllerClass Controller class
	 * @return Name of Helper Module or ""(for not Controllers classes)
	 */
	@Nonnull
	public static String getHelperModuleNameByController(@Nonnull final RVirtualClass controllerClass)
	{
		final String className = controllerClass.getName();
		if(!className.endsWith(RailsConstants.CONTROLLERS_CLASS_NAME_SUFFIX))
		{
			return TextUtil.EMPTY_STRING;
		}
		return className.substring(0, className.length() - RailsConstants.CONTROLLERS_CLASS_NAME_SUFFIX.length()) + RailsConstants.HELPERS_MODULE_NAME_SUFFIX;
	}
}
