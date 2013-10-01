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
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.StandardRailsPaths;
import org.jetbrains.plugins.ruby.rails.langs.RJSFileType;
import org.jetbrains.plugins.ruby.rails.langs.RXMLFileType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileType;
import org.jetbrains.plugins.ruby.ruby.cache.RubyModuleCachesManager;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 08.05.2007
 */
public class ViewsConventions
{
	@NonNls
	public static final String CONTROLLER = "controller";
	public static final String HTML_ERB_VIEW_EXTENSION = "html.erb";

	public static boolean isRHTMLOrRJSViewFile(@NotNull final RVirtualFile rFile, @Nullable final Module module)
	{
		// supports rails 2.0 views
		if(module == null)
		{
			return false;
		}

		final String fileUrl = rFile.getContainingFileUrl();

		final String fileName = rFile.getName();
		if(!isRHTMLFile(fileName) && !isRJSFile(fileName))
		{
			return false;
		}

		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for modules with Rails Support

		return fileUrl.startsWith(railsPaths.getViewsRootURL());
	}

	public static boolean isRHTMLFile(final String fileName)
	{
		// supports rails 2.0 views
		final FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName(fileName);
		return fileType instanceof RHTMLFileType;
	}

	public static boolean isRJSFile(final String fileName)
	{
		// supports rails 2.0 views
		final FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName(fileName);
		return fileType instanceof RJSFileType;
	}

	/**
	 * @param fileName File name with extension
	 * @return true if view file can have such extension
	 */
	public static boolean isValidViewFileName(final String fileName)
	{
		// supports rails 2.0 views
		//change with containsviewByViewsFolder
		return isRHTMLFile(fileName) || isRXMLFile(fileName) || isRJSFile(fileName);
	}

	public static boolean isRXMLFile(final String fileName)
	{
		final FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName(fileName);
		return fileType instanceof RXMLFileType;
	}

	public static boolean containsViewsByViewsFolder(@Nullable VirtualFile viewsFolder, @NotNull final String actionName)
	{
		//change with isValidViewFileName
		if(viewsFolder == null)
		{
			return false;
		}

		final List<VirtualFile> list = findViewsInFolder(actionName, viewsFolder.getUrl());
		return !list.isEmpty();
	}

	@NotNull
	public static String getViewsFolderName(@Nullable final String controllerClassName)
	{
		// supports rails 2.0 views
		final String name = ControllersConventions.getControllerNameByClassName(controllerClassName);
		return name != null ? name : TextUtil.EMPTY_STRING;
	}

	@NotNull
	public static List<VirtualFile> getLayouts(@Nullable final String controllerDirUrl, @NotNull final String controllerName, final Module module)
	{
		// supports rails 2.0 views
		//supports application layout
		if(controllerDirUrl == null)
		{
			return Collections.emptyList();
		}

		final String path = ControllersConventions.getRelativePathOfControllerFolder(controllerDirUrl, module);
		if(path == null)
		{
			return Collections.emptyList();
		}

		final StringBuilder buff = new StringBuilder();
		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for modules with Rails Support
		buff.append(railsPaths.getRailsApplicationRootURL());
		buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
		buff.append(RailsConstants.LAYOUTS_PATH);
		if(!TextUtil.isEmpty(path))
		{
			buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
			buff.append(path);
		}

		return findLayoutsInFolder(controllerName, buff.toString());
	}

	@Nullable
	public static VirtualFile getViewsFolder(@Nullable final VirtualFile controller, @NotNull final Module module)
	{
		// supports rails 2.0 views
		if(controller == null || RailsConstants.APPLICATION_NAME.equals(controller.getNameWithoutExtension()))
		{
			return null;
		}

		final VirtualFile parent = controller.getParent();
		if(parent == null)
		{
			return null;
		}

		final VirtualFile file = ControllersConventions.getControllerDependentFile(parent.getUrl(), module, ControllersConventions.getControllerName(controller), RailsConstants.VIEWS_PATH);
		if(file != null && file.isDirectory())
		{
			return file;
		}
		return null;
	}

	//    /**
	//     * You shouldn't use with method with non-actions ruby method
	//     * @param action action of controller
	//     * @param module rails module
	//     * @return List of view urls
	////     */
	//    @NotNull
	//    public static List<VirtualFile> getViews(@NotNull final RVirtualMethod action,
	//                                                @NotNull final Module module) {
	//        final RClass rClass;
	//        try {
	//            rClass = (RClass)action.getVirtualParentContainer();
	//        } catch (ClassCastException exp) {
	//            return Collections.emptyList();
	//        }
	//        final VirtualFile file = rClass.getContainingFile().getVirtualFile();
	//        if (RailsConstants.APPLICATION_NAME.equals(file.getNameWithoutExtension())) {
	//            return Collections.emptyList();
	//        }
	//        final String controllerDirUrl = file.getParent().getUrl();
	//        return getViews(action, controllerDirUrl,
	//                                       ControllersConventions.getControllerNameByClassName(rClass),
	//                                       module);
	//    }

	@NotNull
	public static List<VirtualFile> getViews(@NotNull final RVirtualMethod method, @NotNull final String controllerDirUrl, @NotNull final String controllerName, @NotNull final Module module)
	{

		final String actionName = method.getName();
		final String url = ControllersConventions.getControllerDependentFileUrl(controllerDirUrl, module, controllerName, RailsConstants.VIEWS_PATH);

		return findViewsInFolder(actionName, url);
	}

	@NotNull
	public static List<VirtualFile> getPartialViews(@NotNull final String controllerDirUrl, @NotNull final String controllerName, @NotNull final Module module)
	{

		final String url = ControllersConventions.getControllerDependentFileUrl(controllerDirUrl, module, controllerName, RailsConstants.VIEWS_PATH);
		if(url != null)
		{
			return findPartialViews(VirtualFileManager.getInstance().findFileByUrl(url));
		}
		return Collections.emptyList();
	}

	protected static List<VirtualFile> findViewsInFolder(final @NotNull String actionName, final @Nullable String url)
	{
		final VirtualFileManager vFManager = VirtualFileManager.getInstance();
		final List<VirtualFile> viewsUrls = new ArrayList<VirtualFile>();
		if(url != null)
		{
			final VirtualFile viewsDir = vFManager.findFileByUrl(url);
			if(viewsDir != null)
			{
				final VirtualFile[] files = viewsDir.getChildren();
				for(VirtualFile file : files)
				{
					if(isViewFile(file) && actionName.equals(getActionMethodNameByView(file)))
					{
						viewsUrls.add(file);
					}
				}
			}
		}
		return viewsUrls;
	}

	protected static List<VirtualFile> findLayoutsInFolder(final @NotNull String controllerName, final @Nullable String url)
	{
		final VirtualFileManager vFManager = VirtualFileManager.getInstance();
		final List<VirtualFile> viewsUrls = new ArrayList<VirtualFile>();
		if(url != null)
		{
			final VirtualFile viewsDir = vFManager.findFileByUrl(url);
			if(viewsDir != null)
			{
				final VirtualFile[] files = viewsDir.getChildren();
				for(VirtualFile file : files)
				{
					if(isLayoutFile(file) && controllerName.equals(getActionMethodNameByView(file)))
					{
						viewsUrls.add(file);
					}
				}
			}
		}
		return viewsUrls;
	}

	@Nullable
	public static String getRelativePathOfLayoutsFolder(@Nullable final String fileUrl, @NotNull final Module module)
	{
		// supports rails 2.0 views
		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for modules with Rails Support
		final String root = railsPaths.getLayoutsRootURL();
		if(fileUrl == null)
		{
			return null;
		}
		return VirtualFileUtil.getRelativePath(fileUrl, root);
	}

	@Nullable
	public static String getRelativePathOfViewsFolder(@Nullable final String fileUrl, @NotNull final Module module)
	{
		// supports rails 2.0 views
		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for modules with Rails Support
		final String root = railsPaths.getViewsRootURL();
		if(fileUrl == null)
		{
			return null;
		}
		return VirtualFileUtil.getRelativePath(fileUrl, root);
	}

	/**
	 * Searches all partial view templates in template folder.
	 * E.g.
	 * If ~/rails/app/views/store contains _form.rhtml and list.rhtml templates
	 * this function will return array with one file - ~/rails/app/views/store/_form.rhtml
	 *
	 * @param folder Template folder
	 * @return partial views in folder.
	 */
	public static List<VirtualFile> findPartialViews(@Nullable final VirtualFile folder)
	{
		// supports rails 2.0 views
		ArrayList<VirtualFile> partials = new ArrayList<VirtualFile>();
		if(folder != null)
		{
			for(VirtualFile file : folder.getChildren())
			{
				if(isValidPartialViewFile(file))
				{
					partials.add(file);
				}
			}
		}
		return partials;
	}

	public static boolean isValidPartialViewFile(@NotNull final VirtualFile file)
	{
		// supports rails 2.0 views
		final String ext = file.getExtension();
		final String name = file.getName();

		return !file.isDirectory() && !TextUtil.isEmpty(ext) && isPartialViewName(name);
	}

	/**
	 * Check if file name is template file and if it is acceptible for partial view.
	 *
	 * @param fileName File name with extension
	 * @return true if is partial view.
	 */
	public static boolean isPartialViewName(@NotNull final String fileName)
	{
		// supports rails 2.0 views
		return fileName.startsWith("_") && isValidViewFileName(fileName);
	}

	/**
	 * Checks if view is located in templates folder
	 * For example:
	 * file://C:/home/rails1/app/views/admin/admin/eee.rhtml ->   true
	 * file://C:/home/rails1/app/eee.rhtml -> false
	 *
	 * @param viewFileUrl Url for *.rhtml file
	 * @param settings    Rails module settings
	 * @return if view is located in templates folder
	 */
	public static boolean hasValidTemplatePath(@NotNull final String viewFileUrl, @NotNull final StandardRailsPaths settings)
	{
		// supports rails 2.0 views
		final String templatesRoot = settings.getViewsRootURL();
		return viewFileUrl.startsWith(templatesRoot);
	}

	/**
	 * @param fileName File name with extension
	 * @return true if layout file can have such extension
	 */
	public static boolean isValidLayoutFileName(final String fileName)
	{
		// supports rails 2.0 views
		return isRHTMLFile(fileName) || isRXMLFile(fileName);
	}

	public static boolean isApplicationLayoutFile(final VirtualFile layout)
	{
		// supports rails 2.0 views
		return RailsConstants.APPLICATION_NAME.equals(getActionMethodNameByView(layout)) && isLayoutFile(layout);
	}

	public static boolean isViewFile(@Nullable final VirtualFile fileOrDir)
	{
		// supports rails 2.0 views
		return !(fileOrDir == null || fileOrDir.isDirectory()) && isValidViewFileName(fileOrDir.getName());
	}

	public static boolean isLayoutFile(@Nullable final VirtualFile fileOrDir)
	{
		// supports rails 2.0 views
		return !(fileOrDir == null || fileOrDir.isDirectory()) && isValidLayoutFileName(fileOrDir.getName());
	}

	@Nullable
	public static RVirtualClass getControllerByView(@NotNull final VirtualFile viewFile, @NotNull final Module moduleWithRails)
	{
		// supports rails 2.0 views
		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(moduleWithRails);
		assert railsPaths != null;
		final String controllerUrl = ControllersConventions.getControllerUrlByViewUrl(viewFile.getUrl(), railsPaths);
		final VirtualFile controllerFile = VirtualFileManager.getInstance().findFileByUrl(controllerUrl);
		if(controllerFile == null)
		{
			return null;
		}
		final RFileInfo rInfo = RubyModuleCachesManager.getInstance(moduleWithRails).getFilesCache().getUp2DateFileInfo(controllerFile);
		if(rInfo == null)
		{
			return null;
		}
		final List<RVirtualClass> classes = RContainerUtil.getTopLevelClasses(rInfo.getRVirtualFile());
		for(RVirtualClass rVirtualClass : classes)
		{
			if(ControllersConventions.isControllerClass(rVirtualClass, moduleWithRails))
			{
				return rVirtualClass;
			}
		}
		return null;
	}

	/**
	 * Returns action name for view file. Method dosen't check that file is really view file
	 *
	 * @param viewFile View file
	 * @return action name
	 */
	@NotNull
	public static String getActionMethodNameByView(@NotNull final VirtualFile viewFile)
	{
		// action.formatRenderer or action.format.renderer
		//e.g. "view.rhtml" or "view.rjs" or "view.html.erb"
		return getActionMethodNameByView(viewFile.getNameWithoutExtension());
	}

	/**
	 * Returns action name for view file. Method dosen't check that file is really view file
	 *
	 * @param viewFileNameWOExt View file name without extension
	 * @return action name
	 */
	@NotNull
	public static String getActionMethodNameByView(@NotNull final String viewFileNameWOExt)
	{
		// action.formatRenderer or action.format.renderer
		//e.g. "view.rhtml" or "view.rjs" or "view.html.erb"

		final int actionNameSeparator = viewFileNameWOExt.lastIndexOf(".");
		if(actionNameSeparator == -1)
		{
			//old view name format
			return viewFileNameWOExt;
		}

		// action.format.renderer
		//new views name format
		return viewFileNameWOExt.substring(0, actionNameSeparator);
	}

	/**
	 * @param module Rails module
	 * @param rClass RClass, maybe controller
	 * @return views folder if class is controller or null
	 */
	@Nullable
	public static VirtualFile getViewsFolderByClass(@NotNull final Module module, @NotNull final RClass rClass)
	{
		//Controller class must belong to ruby file or module
		final RVirtualContainer rClassParentContainer = rClass.getParentContainer();
		if(rClassParentContainer == null || !(rClassParentContainer instanceof RFile || rClassParentContainer instanceof RModule))
		{
			return null;
		}

		// Must be Controller
		if(!rClass.getName().endsWith(RailsConstants.CONTROLLERS_CLASS_NAME_SUFFIX))
		{
			return null;
		}
		return getViewsFolder(rClass.getContainingFile().getVirtualFile(), module);
	}
}
