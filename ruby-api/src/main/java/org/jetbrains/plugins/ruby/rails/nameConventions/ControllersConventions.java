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

import java.util.List;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.StandardRailsPaths;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman.Chernyatchik
 * @date: May 7, 2007
 */
public class ControllersConventions
{
	private static final Logger LOG = Logger.getInstance(ControllersConventions.class.getName());

	@NonNls
	public static String APPLICATION_CONTROLLER = RailsConstants.APPLICATION_CONTROLLER_NAME;


	public static boolean isControllerFile(@NotNull final RVirtualFile rFile, @Nullable final Module module)
	{
		return isControllerFile(rFile, module, RContainerUtil.getTopLevelClasses(rFile));
	}

	public static boolean isControllerFile(@NotNull final RVirtualFile rFile, @Nullable final Module module, @NotNull final List<RVirtualClass> classes)
	{
		if(module == null)
		{
			return false;
		}

		final String fileUrl = rFile.getContainingFileUrl();
		final String fileName = VirtualFileUtil.removeExtension(rFile.getName());
		if(!fileName.endsWith(RailsConstants.CONTROLLERS_FILE_NAME_SUFFIX) && !RailsConstants.APPLICATION_NAME.equals(fileName.toLowerCase()))
		{
			return false;
		}

		final StandardRailsPaths moduleSettings = RailsFacetUtil.getRailsAppPaths(module);
		assert moduleSettings != null; //Not null for Module with Rails support

		final String controllersRoot = moduleSettings.getControllerRootURL();
		if(!fileUrl.startsWith(controllersRoot))
		{
			return false;
		}

		for(RVirtualClass virtualClass : classes)
		{
			if(isControllerClass(virtualClass, module))
			{
				return true;
			}
		}
		return false;
	}

	@Nullable
	public static String getControllerName(final VirtualFile controller)
	{
		return getControllerName(controller.getName());
	}

	@Nullable
	public static String getControllerName(@NotNull final String controllerFileName)
	{
		final String name = VirtualFileUtil.removeExtension(controllerFileName);
		if(RailsConstants.APPLICATION_NAME.equals(name))
		{
			return name;
		}
		if(!name.endsWith(RailsConstants.CONTROLLERS_FILE_NAME_SUFFIX))
		{
			return null;
		}
		return name.substring(0, name.length() - RailsConstants.CONTROLLERS_FILE_NAME_SUFFIX.length());
	}

	@Nullable
	public static String getControllerNameByClassName(@Nullable RVirtualClass rVClass)
	{
		if(rVClass == null)
		{
			return null;
		}
		return getControllerNameByClassName(rVClass.getName());
	}

	/**
	 * Returns short Controller class name of corresponding module. E.g
	 * Admin::HelloHelper -> HelloController
	 *
	 * @param helperModule HelperModule
	 * @return Name of Controller Class or ""(for not Controllers classes)
	 */
	@NotNull
	public static String getControllerClassNameByHelper(@NotNull final RVirtualModule helperModule)
	{
		final String moduleName = helperModule.getName();
		if(!moduleName.endsWith(RailsConstants.HELPERS_MODULE_NAME_SUFFIX))
		{
			return TextUtil.EMPTY_STRING;
		}
		return moduleName.substring(0, moduleName.length() - RailsConstants.HELPERS_MODULE_NAME_SUFFIX.length()) + RailsConstants.CONTROLLERS_CLASS_NAME_SUFFIX;
	}

	/**
	 * Returns controller name e.g.:
	 * "ApplicationController" -> "application"
	 *
	 * @param className Controller Class
	 * @return Controller name
	 */
	@Nullable
	public static String getControllerNameByClassName(@Nullable final String className)
	{
		if(className == null)
		{
			return null;
		}
		final String name = NamingConventions.toUnderscoreCase(className);
		if(!name.endsWith(RailsConstants.CONTROLLERS_FILE_NAME_SUFFIX))
		{
			return null;
		}
		return name.substring(0, name.length() - RailsConstants.CONTROLLERS_CLASS_NAME_SUFFIX.length() - 1);
	}

	/**
	 * Return name of controller for HelperFileName without extension. Function uses
	 * name convention rule for rails helpers and controllers.
	 *
	 * @param fileName helper file
	 * @return name of corresponding controller
	 */
	@Nullable
	public static String getControllerNameByHelperFileName(@Nullable final String fileName)
	{
		if(fileName == null)
		{
			return null;
		}
		if(!fileName.endsWith(RailsConstants.HELPERS_FILE_NAME_SUFFIX))
		{
			return null;
		}
		return fileName.substring(0, fileName.length() - RailsConstants.HELPERS_FILE_NAME_SUFFIX.length());
	}

	/**
	 * Return name of controller for Layout filename without extension. Function uses
	 * name convention rule for rails helpers and views. File doesn't check than file is layout file.
	 *
	 * @param nameWOExt Name without ext
	 * @return name of corresponding controller
	 */
	@Nullable
	public static String getControllerNameByLayoutFileName(@NotNull final String nameWOExt)
	{
		return NamingConventions.toMixedCase(ViewsConventions.getActionMethodNameByView(nameWOExt));
	}

	@Nullable
	public static String getControllerClassNameByFileName(@Nullable final String controllerFileName)
	{
		if(controllerFileName == null)
		{
			return null;
		}
		final String name = VirtualFileUtil.removeExtension(controllerFileName);
		if(RailsConstants.APPLICATION_NAME.equals(name))
		{
			return RailsConstants.APPLICATION_CONTROLLER_NAME;
		}
		if(!name.endsWith(RailsConstants.CONTROLLERS_FILE_NAME_SUFFIX))
		{
			return null;
		}
		return NamingConventions.toMixedCase(name);
	}

	@Nullable
	public static String getControllerClassName(@Nullable final String controllerName)
	{
		if(controllerName == null)
		{
			return null;
		}
		return NamingConventions.toMixedCase(controllerName) + RailsConstants.CONTROLLERS_CLASS_NAME_SUFFIX;
	}

	@Nullable
	public static String getControllerFileName(@Nullable final String controllerClassName)
	{
		if(controllerClassName == null)
		{
			return null;
		}
		final StringBuilder buff = new StringBuilder();
		if(RailsConstants.APPLICATION_CONTROLLER_NAME.equals(controllerClassName))
		{
			buff.append(RailsConstants.APPLICATION_NAME);
		}
		else
		{
			buff.append(NamingConventions.toUnderscoreCase(controllerClassName));
		}
		buff.append('.');
		buff.append(RubyFileType.INSTANCE.getDefaultExtension());
		return buff.toString();
	}

	public static boolean isApplicationControllerFile(@NotNull final VirtualFile controller, final Module module)
	{
		return isApplicationControllerFile(controller.getUrl(), controller.getNameWithoutExtension(), module);
	}

	public static boolean isApplicationControllerFile(@NotNull final String fileUrl, @NotNull final String fileNameWOExt, final Module module)
	{
		/**
		 * Check for file name is used only for optimization
		 */
		return RailsConstants.APPLICATION_NAME.equals(fileNameWOExt) && fileUrl.equals(getApplicationControllerUrl(module));
	}

	@Nullable
	public static String getApplicationControllerUrl(final Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		if(settings == null)
		{
			return null;
		}
		final String root = settings.getControllerRootURL();
		return root + VirtualFileUtil.VFS_PATH_SEPARATOR + RailsConstants.APPLICATION_NAME + "." + RubyFileType.INSTANCE.getDefaultExtension();
	}

	@Nullable
	public static VirtualFile getApplicationControllerFile(final Module module)
	{
		final String controllerUrl = getApplicationControllerUrl(module);
		return controllerUrl == null ? null : VirtualFileManager.getInstance().findFileByUrl(controllerUrl);
	}

	/**
	 * Computes url for file dependent on controller url:
	 * [pathprefix/][path relative to controllers root/]fileName
	 * You can use this method to find view or helper file for controller
	 *
	 * @param controllerDirUrl url for folder with controller file
	 * @param module           Rails module
	 * @param fileName         file name
	 * @param pathPrefix       prefix will be inserted before relative path
	 * @return url for file
	 */
	@Nullable
	public static String getControllerDependentFileUrl(@NotNull final String controllerDirUrl, @NotNull final Module module, final String fileName, final String pathPrefix)
	{
		final String path = getRelativePathOfControllerFolder(controllerDirUrl, module);
		if(path == null)
		{
			return null;
		}

		final StringBuilder buff = new StringBuilder();
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		assert settings != null;
		buff.append(settings.getRailsApplicationRootURL());
		if(!TextUtil.isEmpty(pathPrefix))
		{
			buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
			buff.append(pathPrefix);
		}
		if(!TextUtil.isEmpty(path))
		{
			buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
			buff.append(path);
		}
		if(!TextUtil.isEmpty(fileName))
		{
			buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
			buff.append(fileName);
		}
		return buff.toString();
	}

	/**
	 * Searches file dependent on controller url.
	 * For more details see <code>RailsUtil.getControllerDependent()</code>
	 *
	 * @param controllerDirUrl url for folder with controller file
	 * @param module           Rails module
	 * @param fileName         file name
	 * @param pathPrefix       prefix will be inserted before relative path
	 * @return url for file
	 */
	@Nullable
	public static VirtualFile getControllerDependentFile(final String controllerDirUrl, final Module module, final String fileName, final String pathPrefix)
	{

		final String url = getControllerDependentFileUrl(controllerDirUrl, module, fileName, pathPrefix);
		return url != null ? VirtualFileManager.getInstance().findFileByUrl(url) : null;
	}

	/**
	 * Returns full controller name according rails naming conventions.
	 * For example
	 * [module root]/server1/admin/login_module/login_controller.rb
	 * -> Server1::Admin::LoginModule::Login
	 *
	 * @param module         Rails module with file
	 * @param controllerFile file of controller
	 * @return full controller name
	 */
	@Nullable
	public static String getControllerFullName(@NotNull final Module module, @NotNull final VirtualFile controllerFile)
	{
		final StringBuilder pathBuffer = new StringBuilder();
		final VirtualFile controllersRoot = org.jetbrains.plugins.ruby.rails.RailsUtil.getControllersRoot(module);
		if(controllersRoot == null)
		{
			return null;
		}

		final String cRoot = controllersRoot.getPath();
		final VirtualFile parentDir = controllerFile.getParent();
		LOG.assertTrue(parentDir != null, "Directory of file[" + controllerFile + "] cant be null");

		final String parentDirPath = parentDir.getPath();
		final String fsPath = VirtualFileUtil.getRelativePath(parentDirPath, cRoot);

		//may be called for controllers in tests.. [root]/test/unit/...
		if(fsPath == null)
		{
			return null;
		}

		pathBuffer.append(org.jetbrains.plugins.ruby.rails.RailsUtil.toModulesPath(fsPath));
		if(pathBuffer.length() > 0)
		{
			pathBuffer.append(RubyUtil.MODULES_PATH_SEPARATOR);
		}
		final String controllerName = getControllerName(controllerFile);
		if(TextUtil.isEmpty(controllerName))
		{
			return null;
		}
		pathBuffer.append(controllerName);
		return pathBuffer.toString();
	}

	public static boolean isControllerClass(@Nullable final RVirtualClass rClass, @NotNull final Module module)
	{
		if(!RailsFacetUtil.hasRailsSupport(module) || getControllerNameByClassName(rClass) == null)
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
		assert rClass != null; // for rails modules isn't null
		final List<String> path = rClass.getFullPath();
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		assert settings != null;

		final String controllersRootURL = settings.getControllerRootURL();
		final StringBuilder buff = new StringBuilder(controllersRootURL);

		final String url = rClass.getContainingFileUrl();
		final VirtualFile file = rClass.getVirtualFile();

		if(file == null)
		{
			return false;
		}
		// Application controller conventions differ from ordinary controllers conventions.
		if(ControllersConventions.isApplicationControllerFile(file, module))
		{
			return true;
		}

		for(String name : path)
		{
			buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
			buff.append(NamingConventions.toUnderscoreCase(name));
		}
		buff.append('.').append(RubyFileType.INSTANCE.getDefaultExtension());
		return buff.toString().equals(url);
	}

	/**
	 * @param dirUrl Url for dir in Model, Controller, Layouts, Views or Helper directories.
	 * @param module Rails module
	 * @return corresponding relative path in controllers folder or null if module ins't rails module
	 *         or corresponding dir can't be calculated.
	 */
	@Nullable
	public static String getControllerCorrespondingDir(@NotNull final String dirUrl, @NotNull final Module module)
	{
		if(!RailsFacetUtil.hasRailsSupport(module))
		{
			return null;
		}

		final StandardRailsPaths moduleSettings = RailsFacetUtil.getRailsAppPaths(module);
		assert moduleSettings != null; //Not null for module with Rails support

		/**
		 * If file belongs to controllers
		 */
		final String controllersRoot = moduleSettings.getControllerRootURL();
		if(dirUrl.startsWith(controllersRoot))
		{
			return dirUrl;
		}

		/**
		 * If file belongs to helpers folder
		 */
		if(dirUrl.startsWith(moduleSettings.getHelpersRootURL()))
		{
			return HelpersConventions.getRelativePathOfHelperFolder(dirUrl, module);
		}

		/**
		 * If file belongs to layouts folder
		 */
		if(dirUrl.startsWith(moduleSettings.getLayoutsRootURL()))
		{
			return ViewsConventions.getRelativePathOfLayoutsFolder(dirUrl, module);
		}

		/**
		 * If file belongs to view folder
		 */
		final String viewsRootUrl = moduleSettings.getViewsRootURL();
		if(dirUrl.startsWith(viewsRootUrl))
		{
			return ViewsConventions.getRelativePathOfViewsFolder(dirUrl, module);
		}
		return null;
	}

	@Nullable
	public static String getControllerURL(final String helperDirUrl, final String controllerName, final Module module)
	{

		final String path = HelpersConventions.getRelativePathOfHelperFolder(helperDirUrl, module);
		if(path == null)
		{
			return null;
		}

		final StringBuilder buff = new StringBuilder();
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		assert settings != null;
		buff.append(settings.getRailsApplicationRootURL());
		buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
		buff.append(RailsConstants.CONTROLLERS_PATH);
		if(!TextUtil.isEmpty(path))
		{
			buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
			buff.append(path);
		}
		buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
		buff.append(controllerName);
		buff.append(RailsConstants.CONTROLLERS_FILE_NAME_SUFFIX);
		buff.append(".");
		buff.append(RubyFileType.INSTANCE.getDefaultExtension());
		return buff.toString();
	}

	/**
	 * Computes url for controller file
	 * For example:
	 * file://C:/home/idea_proj2/commit_test/rails1/app/views/admin/admin/eee.rhtml ->
	 * file://C:/home/idea_proj2/commit_test/rails1/app/controllers/admin/admin_controller.rb
	 *
	 * @param viewFileUrl Url for *.rhtml file
	 * @param railsPaths  Rails module settings
	 * @return url for controller file
	 * @throws StringIndexOutOfBoundsException
	 *          if file isn't in templates folder.
	 *          At first you should check <code>hasValidTemplatePath()</code>.
	 */
	@NotNull
	public static String getControllerUrlByViewUrl(@NotNull final String viewFileUrl, @NotNull final StandardRailsPaths railsPaths)
	{
		// supports rails 2.0 views
		return getControllerUrlByViewUrl(viewFileUrl, railsPaths.getViewsRootURL(), railsPaths.getControllerRootURL());
	}

	/**
	 * Url of Controller with views folder same as given folderUrl.
	 * E.g. for /rails/app/view/admin/user -> /rails/app/controllers/admin/user_controller.rb
	 *
	 * @param folderUrl  Views folder url of some controller
	 * @param railsPaths Rails module settings
	 * @return Url
	 */
	@NotNull
	public static String getControllerUrlByViewsFolderUrl(@NotNull final String folderUrl, @NotNull final StandardRailsPaths railsPaths)
	{

		// supports rails 2.0 views
		final String viewsRootUrl = railsPaths.getViewsRootURL();
		final String controllersRootUrl = railsPaths.getControllerRootURL();

		final StringBuilder buff = new StringBuilder();
		buff.append(controllersRootUrl);
		final int start = folderUrl.indexOf(viewsRootUrl) + viewsRootUrl.length();
		buff.append(folderUrl.substring(start));
		buff.append(RailsConstants.CONTROLLERS_FILE_NAME_SUFFIX);
		buff.append('.');
		buff.append(RubyFileType.INSTANCE.getDefaultExtension());
		return buff.toString();
	}

	/**
	 * Computes url for controller file
	 * For example:
	 * file://C:/home/rails1/app/views/admin/admin/eee.rhtml ->
	 * file://C:/home/rails1/app/controllers/admin/admin_controller.rb
	 * (Supports Rails 2.0 views)
	 *
	 * @param viewFileUrl        Url for *.rhtml file
	 * @param viewsRootUrl       views root folder ([rails project folder]/app/views)
	 * @param controllersRootUrl views root folder ([rails project folder]/app/controllers)
	 * @return url for controller file
	 * @throws StringIndexOutOfBoundsException
	 *          if file isn't in templates folder.
	 *          At first you should check <code>hasValidTemplatePath()</code>.
	 */
	@NotNull
	public static String getControllerUrlByViewUrl(@NotNull final String viewFileUrl, @NotNull final String viewsRootUrl, @NotNull final String controllersRootUrl)
	{
		// supports rails 2.0 views
		final StringBuilder buff = new StringBuilder();
		buff.append(controllersRootUrl);
		final int start = viewFileUrl.indexOf(viewsRootUrl) + viewsRootUrl.length();
		final int end = viewFileUrl.lastIndexOf(VirtualFileUtil.VFS_PATH_SEPARATOR);
		buff.append(viewFileUrl.substring(start, end));
		buff.append(RailsConstants.CONTROLLERS_FILE_NAME_SUFFIX);
		buff.append('.');
		buff.append(RubyFileType.INSTANCE.getDefaultExtension());
		return buff.toString();
	}

	@Nullable
	public static RClass getControllerByViewFile(final PsiFile file, final Module module)
	{
		// supports rails 2.0 views
		final VirtualFile viewFile = file.getVirtualFile();
		assert viewFile != null;

		final String fileUrl = viewFile.getUrl();
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);

		if(!ViewsConventions.hasValidTemplatePath(fileUrl, settings))
		{
			return null;
		}

		final String controllerUrl = getControllerUrlByViewUrl(fileUrl, settings);
		final VirtualFile controllerFile = VirtualFileManager.getInstance().findFileByUrl(controllerUrl);
		if(controllerFile == null)
		{
			return null;
		}

		return getControllerClass(module, controllerFile);
	}

	private static RClass getControllerClass(final Module module, final VirtualFile controllerFile)
	{
		final PsiFile psiFile = PsiManager.getInstance(module.getProject()).findFile(controllerFile);
		if(!(psiFile instanceof RFile))
		{
			return null;
		}

		return RContainerUtil.getClassByName((RFile) psiFile, getControllerClassNameByFileName(controllerFile.getName()));
	}

	@Nullable
	public static RMethod getActionByViewFile(final PsiFile file, final Module module)
	{
		// supports rails 2.0 views
		final VirtualFile viewFile = file.getVirtualFile();
		assert viewFile != null;

		final RClass controllerClass = getControllerByViewFile(file, module);
		if(controllerClass == null)
		{
			return null;
		}

		return (RMethod) RVirtualPsiUtil.getMethodWithoutArgumentsByName(controllerClass, ViewsConventions.getActionMethodNameByView(viewFile));
	}

	/**
	 * Checks if name is valid action name. Action name should be identifier
	 * and in underscored case.
	 *
	 * @param name action name
	 * @return true if is valid
	 */
	public static boolean isValidActionName(@Nullable final String name)
	{
		//noinspection ConstantConditions
		return NamingConventions.isInUnderscoredCase(name) && name.charAt(0) != '_';
	}

	public static boolean isValidActionMethod(@Nullable final RVirtualMethod method)
	{
		return method != null && method.getArgumentInfos().size() == 0;
		//               && method.getAccessModifier().equals(AccessModifier.PUBLIC); //TODO
	}

	@Nullable
	public static String getRelativePathOfControllerFolder(@NotNull final String fileUrl, @NotNull final Module module)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		assert settings != null;
		final String root = settings.getControllerRootURL();
		return VirtualFileUtil.getRelativePath(fileUrl, root);
	}

	@NotNull
	public static String toValidActionName(@Nullable final String name)
	{
		String newName = NamingConventions.toUnderscoreCase(name);
		while(!TextUtil.isEmpty(newName) && newName.charAt(0) == '_')
		{
			newName = newName.substring(1);
		}
		return newName;
	}

	@Nullable
	public static RClass getControllerClassByShortName(@NotNull final Module module, @NotNull final String name)
	{
		final StandardRailsPaths settings = RailsFacetUtil.getRailsAppPaths(module);
		assert settings != null;
		final String root = settings.getControllerRootURL();
		String url = root + "/" + name + RailsConstants.CONTROLLERS_FILE_NAME_SUFFIX + ".rb";
		final VirtualFile controllerFile = VirtualFileManager.getInstance().findFileByUrl(url);
		if(controllerFile == null)
		{
			return null;
		}
		return getControllerClass(module, controllerFile);
	}
}
