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

package org.jetbrains.plugins.ruby.rails.module.view.id;

import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import consulo.language.psi.PsiElement;
import consulo.virtualFileSystem.VirtualFileManager;
import org.jetbrains.plugins.ruby.rails.RailsUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.StandardRailsPaths;
import org.jetbrains.plugins.ruby.rails.module.view.RailsViewFoldersManager;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.ActionNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.ClassNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.ControllerClassNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.HelperNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.MethodNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.RailsProjectModuleNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.SimpleFileNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.folders.ControllerSubFolderNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.folders.FolderNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.folders.ModelSubFolderNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.folders.RailsApplicationFolderNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.folders.TestsSubFolderNode;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.HelpersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.MigrationsConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.ViewsConventions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import consulo.logging.Logger;
import consulo.module.Module;
import consulo.virtualFileSystem.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 03.03.2007
 */
public class NodeIdUtil
{
	private static Logger LOG = Logger.getInstance(NodeIdUtil.class.getName());

	private static final String APPLICATION_FOLDER = "APPLICATION";
	private static final String ROOT = "ROOT";

	@Nonnull
	public static NodeId createForFile(final String fileUrl)
	{
		return new NodeId(fileUrl);
	}

	@Nonnull
	public static NodeId createForDirectory(final String dirUrl, boolean isApplicationDir)
	{
		return new NodeId(dirUrl, null, isApplicationDir ? APPLICATION_FOLDER : null);
	}

	@Nonnull
	public static NodeId createForRoot()
	{
		return new NodeId(ROOT);
	}

	@Nonnull
	public static NodeId createForVirtualContainer(final RContainer elem)
	{
		return new NodeId(elem.getContainingFileUrl(), elem, null);
	}

	public static boolean isApplicationDirectory(final NodeId id)
	{
		return APPLICATION_FOLDER.equals(id.getParams());
	}

	//    public static NodeId processIfDirectory(final VirtualFile file, final String fileUrl,
	//                                             final RailsModuleSettings moduleSettings,
	//                                             boolean evalParentId) {
	//        if (file.isDirectory()) {
	//            if (!evalParentId) {
	//                return createForDirectory(fileUrl, false);
	//            }
	//            if (fileUrl.equals(moduleSettings.getControllerRootURL()) ||
	//                    fileUrl.equals(moduleSettings.getModelRootURL()) ||
	//                    fileUrl.equals(moduleSettings.getTestsStdUnitRootURL())) {
	//
	//                return createForDirectory(moduleSettings.getRailsApplicationRootURL(), false);
	//            }
	//            if (fileUrl.equals(moduleSettings.getRailsApplicationRootURL())) {
	//                return createForRoot();
	//            }
	//            final VirtualFile parent = file.getParent();
	//            if (parent != null) {
	//                return createForDirectory(parent.getUrl(), false);
	//            }
	//        }
	//        return null;
	//    }

	/**
	 * Calculates id of file parent node in RailsProjectTree. If file doesn't exist
	 * you may calculate id
	 * <p/>
	 * app/controllers/application.rb -> Application folder node id<br>
	 * ruby file or dir under app/controllers direcotry -> Parent directory id<br>
	 * ruby file or dir under app/models directory -> Parent directory id<br>
	 * helper under app/helpers directory -> Corresponding controller's id<br>
	 * view under app/views directory   -> Corresponding action method's ids<br>
	 * layout under app/views/layouts directory   -> Corresponding controller's id<br>
	 * ruby, yaml file or dir under app/tests directory -> Parent directory id<br>
	 *
	 * @param file    Some rails module file
	 * @param fileUrl url for file
	 * @param module  Rails module
	 * @return Id of general parent node for elements in file. If module isn't rails module
	 *         method returns null.
	 */
	@Nullable
	public static NodeId[] getRailsTreeNodeParentID(@Nullable final VirtualFile file, @Nonnull final String fileUrl, @Nonnull final Module module)
	{
		return getRailsTreeNodeCurrentOrParentID(file, fileUrl, module, true, null);
	}

	@Nullable
	public static NodeId[] getRailsTreeNodeParentID(@Nullable final VirtualFile file, @Nonnull final String fileUrl, @Nonnull final Module module, @Nullable final Object element)
	{
		return getRailsTreeNodeCurrentOrParentID(file, fileUrl, module, true, element);
	}

	/**
	 * Works right if file exist.
	 *
	 * @param file       Virtual File (must exist)
	 * @param fileUrl    File url
	 * @param module     Rails Module
	 * @param psiElement Specifies element in file for NodeId
	 * @return NodeId for current element
	 */
	@Nullable
	public static NodeId[] getRailsTreeNodeID(@Nullable final VirtualFile file, @Nonnull final String fileUrl, @Nonnull final Module module, @Nullable final PsiElement psiElement)
	{
		return getRailsTreeNodeCurrentOrParentID(file, fileUrl, module, false, psiElement);
	}

	@Nullable
	private static NodeId[] getRailsTreeNodeCurrentOrParentID(@Nullable final VirtualFile file, @Nonnull final String fileUrl, @Nonnull final Module module, final boolean forParent, @Nullable final Object selectedElement)
	{
		if(!RailsFacetUtil.hasRailsSupport(module))
		{
			return null;
		}
		final String fileName = VirtualFileUtil.getFileName(fileUrl);
		final String nameWOExt = VirtualFileUtil.getNameWithoutExtension(fileName);
		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for Rails Module
		final String parentDirUrl = VirtualFileUtil.getParentDir(fileUrl);

		/**
		 * If file belongs to controllers
		 */
		final String controllersRoot = railsPaths.getControllerRootURL();
		if(fileUrl.startsWith(controllersRoot))
		{
			return processElementFromControllersRoot(file, fileUrl, module, forParent, selectedElement, fileName, nameWOExt, parentDirUrl, controllersRoot);
		}

		/**
		 * If file belongs to models folder
		 */
		final String modelsRoot = railsPaths.getModelRootURL();
		if(fileUrl.startsWith(modelsRoot))
		{
			return processElementFromModelsRoot(file, fileUrl, module, forParent, selectedElement, fileName, parentDirUrl, modelsRoot);
		}

		/**
		 * If file belongs to helpers folder
		 */
		if(fileUrl.startsWith(railsPaths.getHelpersRootURL()))
		{
			return processElementFromHelpersRoot(file, module, forParent, selectedElement, fileName, nameWOExt, parentDirUrl, controllersRoot);
		}

		/**
		 * If file belongs to layouts folder
		 */
		if(fileUrl.startsWith(railsPaths.getLayoutsRootURL()))
		{
			return processElementFromLayoutsRoot(fileUrl, module, forParent, fileName, nameWOExt, parentDirUrl, controllersRoot);
		}

		/**
		 * If file belongs to view folder
		 */
		final String viewsRootUrl = railsPaths.getViewsRootURL();
		if(fileUrl.startsWith(viewsRootUrl))
		{
			return processElementFromViewsRoot(file, fileUrl, module, forParent, fileName, nameWOExt, railsPaths, parentDirUrl);
		}

		/**
		 * If file belongs to tests folder
		 */
		final String testRootUrl = railsPaths.getTestsStdUnitRootURL();
		if(fileUrl.startsWith(testRootUrl))
		{
			return processElementFromTestsRoot(file, fileUrl, module, forParent, selectedElement, fileName, parentDirUrl, modelsRoot);
		}

		/**
		 * If file belongs to migrations folder
		 */
		final String migrationsRootUrl = railsPaths.getMigrationsRootURL();
		if(fileUrl.startsWith(migrationsRootUrl))
		{
			return processElementFromMigrationsRoot(fileUrl, forParent, fileName, parentDirUrl, modelsRoot, migrationsRootUrl);
		}

		/**
		 * If file is db/schema.rb
		 */
		final String schemaUrl = MigrationsConventions.getSchemaURL(module);
		if(fileUrl.equals(schemaUrl))
		{
			return forParent ? new NodeId[]{FolderNode.generateNodeId(migrationsRootUrl)} : new NodeId[]{SimpleFileNode.generateNodeId(fileUrl)};
		}

		/**
		 * If file is user file
		 */
		final Set<String> urls = RailsViewFoldersManager.getInstance(module).getRailsViewUserFolderUrls();
		final List<NodeId> infos = new ArrayList<NodeId>();
		for(String url : urls)
		{
			processElementFromUsersRoots(file, fileUrl, module, forParent, fileName, parentDirUrl, infos, url);
		}
		if(!infos.isEmpty())
		{
			return infos.toArray(new NodeId[infos.size()]);
		}

		return null;
	}

	private static NodeId[] processElementFromMigrationsRoot(String fileUrl, boolean forParent, String fileName, String parentDirUrl, String modelsRoot, String migrationsRootUrl)
	{
		if(fileUrl.equals(migrationsRootUrl))
		{
			return forParent ? new NodeId[]{ModelSubFolderNode.generateNodeId(modelsRoot)} : new NodeId[]{FolderNode.generateNodeId(fileUrl)};
		}
		if(RubyVirtualFileScanner.isRubyFile(fileName))
		{
			return forParent ? new NodeId[]{FolderNode.generateNodeId(parentDirUrl)} : new NodeId[]{SimpleFileNode.generateNodeId(fileUrl)};
		}
		return null;
	}

	private static NodeId[] processElementFromTestsRoot(VirtualFile file, String fileUrl, Module module, boolean forParent, Object selectedElement, String fileName, String parentDirUrl, String modelsRoot)
	{
		if(isDirectory(file, fileName))
		{
			if(fileUrl.equals(modelsRoot))
			{
				// file corresponds to RailsControllersFolderNode
				return forParent ? new NodeId[]{RailsProjectModuleNode.generateNodeId(module)} : new NodeId[]{TestsSubFolderNode.generateNodeId(fileUrl)};
			}
			// file is directory under app/controllers
			return new NodeId[]{TestsSubFolderNode.generateNodeId(forParent ? parentDirUrl : fileUrl)};
		}
		if(RubyVirtualFileScanner.isRubyFile(fileName))
		{
			if(forParent)
			{
				final RContainer parentVContainer = getParentContanerForSelection(selectedElement);
				if(parentVContainer == null || parentVContainer instanceof RFile)
				{
					return new NodeId[]{TestsSubFolderNode.generateNodeId(parentDirUrl)};
				}
				return getNodeIdForClassOrControllerOrMethodInFile(file, module, parentVContainer);
			}
			else
			{
				return getNodeIdForClassOrControllerOrMethodInFile(file, module, selectedElement);
			}
		}
		if(RailsUtil.isYMLFile(fileName))
		{
			return forParent ? new NodeId[]{TestsSubFolderNode.generateNodeId(parentDirUrl)} : new NodeId[]{SimpleFileNode.generateNodeId(fileUrl)};
		}
		return null;
	}

	private static void processElementFromUsersRoots(VirtualFile file, String fileUrl, Module module, boolean forParent, String fileName, String parentDirUrl, List<NodeId> infos, String url)
	{
		if(fileUrl.equals(url))
		{
			infos.add(forParent ? RailsProjectModuleNode.generateNodeId(module) : FolderNode.generateNodeId(fileUrl));
		}
		if(fileUrl.startsWith(url))
		{
			if(forParent)
			{
				infos.add(FolderNode.generateNodeId(parentDirUrl));
			}
			else
			{
				if(isDirectory(file, fileName))
				{
					infos.add(FolderNode.generateNodeId(fileUrl));
				}
				else
				{
					infos.add(SimpleFileNode.generateNodeId(fileUrl));
				}
			}
		}
	}

	private static NodeId[] processElementFromViewsRoot(VirtualFile file, String fileUrl, Module module, boolean forParent, String fileName, String nameWOExt, StandardRailsPaths railsPaths, String parentDirUrl)
	{
		/**
		 * If shared partials
		 */
		final String sharedPartials = railsPaths.getDefaultSharedPartialsRootURL();
		if(fileUrl.startsWith(sharedPartials))
		{
			// shared partials root
			if(fileUrl.equals(sharedPartials))
			{
				return forParent ? new NodeId[]{RailsProjectModuleNode.generateNodeId(module)} : new NodeId[]{FolderNode.generateNodeId(fileUrl)};
			}
			//other folders and partials
			if(isDirectory(file, fileName))
			{
				return new NodeId[]{FolderNode.generateNodeId(forParent ? parentDirUrl : fileUrl)};
			}
			if(!ViewsConventions.isPartialViewName(fileName))
			{
				return null;
			}
			return forParent ? new NodeId[]{FolderNode.generateNodeId(parentDirUrl)} : new NodeId[]{SimpleFileNode.generateNodeId(fileUrl)};
		}
		// if partials root or other dir
		if(isDirectory(file, fileName))
		{
			final String controllerUrl = ControllersConventions.getControllerUrlByViewsFolderUrl(fileUrl, railsPaths);
			final RClass rClass = getControllerClass(controllerUrl, module);
			if(forParent)
			{
				return rClass == null ? null  // other directories doesn't supported here
						: new NodeId[]{ControllerClassNode.generateNodeId(rClass)};
			}
			return new NodeId[]{FolderNode.generateNodeId(fileUrl)};
		}

		// if view template
		if(!ViewsConventions.isValidViewFileName(fileName))
		{
			return null;
		}

		LOG.assertTrue(ViewsConventions.hasValidTemplatePath(fileUrl, railsPaths), (forParent ? "Parent" : "Element") + " node id for [" + fileUrl + "]: Url isn't valid template path.");

		final String controllerUrl = ControllersConventions.getControllerUrlByViewUrl(fileUrl, railsPaths);
		final RClass rClass = getControllerClass(controllerUrl, module);

		//partial view file
		if(ViewsConventions.isPartialViewName(fileName))
		{
			return forParent ? new NodeId[]{FolderNode.generateNodeId(parentDirUrl)} : new NodeId[]{SimpleFileNode.generateNodeId(fileUrl)};
		}

		//view
		if(rClass == null)
		{
			return null;
		}

		final RMethod rMethod = RVirtualPsiUtil.getMethodWithoutArgumentsByName(rClass, nameWOExt);
		if(rMethod != null)
		{
			return forParent ? new NodeId[]{MethodNode.generateNodeId(rMethod)} : new NodeId[]{SimpleFileNode.generateNodeId(fileUrl)};
		}
		else
		{
			return null;
		}
	}

	private static NodeId[] processElementFromLayoutsRoot(String fileUrl, Module module, boolean forParent, String fileName, String nameWOExt, String parentDirUrl, String controllersRoot)
	{
		if(!ViewsConventions.isValidLayoutFileName(fileName))
		{
			return null;
		}
		final String controllerName = ControllersConventions.getControllerNameByLayoutFileName(nameWOExt);
		final String relativePath = ViewsConventions.getRelativePathOfLayoutsFolder(parentDirUrl, module);

		final RClass rClass = getControllerClass(controllerName, relativePath, controllersRoot, module);
		if(rClass != null)
		{
			return forParent ? new NodeId[]{ControllerClassNode.generateNodeId(rClass)} : new NodeId[]{SimpleFileNode.generateNodeId(fileUrl)};
		}
		else
		{
			return null;
		}
	}

	private static NodeId[] processElementFromHelpersRoot(VirtualFile file, Module module, boolean forParent, Object selectedElement, String fileName, String nameWOExt, String parentDirUrl, String controllersRoot)
	{
		if(!RubyVirtualFileScanner.isRubyFile(fileName))
		{
			return null;
		}

		final RContainer parentVContainer = getParentContanerForSelection(selectedElement);
		if(parentVContainer == null || parentVContainer instanceof RFile)
		{
			//toplevel element in file
			final String controllerName = ControllersConventions.getControllerNameByHelperFileName(nameWOExt);
			final String relativePath = HelpersConventions.getRelativePathOfHelperFolder(parentDirUrl, module);
			final RClass rClass = getControllerClass(controllerName, relativePath, controllersRoot, module);
			if(rClass != null)
			{
				return forParent ? new NodeId[]{ControllerClassNode.generateNodeId(rClass)} : getNodeIdForHelper(selectedElement, module, file);
			}
			else
			{
				return null;
			}
		}
		else
		{
			final Object elem = forParent ? parentVContainer : selectedElement;
			//Helper Module
			if(elem instanceof RModule)
			{
				return getNodeIdForHelper(elem, module, file);
			}
			//Other classes or methods
			return getNodeIdForClassOrControllerOrMethodInFile(file, module, elem);
		}
	}

	private static NodeId[] processElementFromModelsRoot(VirtualFile file, String fileUrl, Module module, boolean forParent, Object selectedElement, String fileName, String parentDirUrl, String modelsRoot)
	{
		if(isDirectory(file, fileName))
		{
			if(fileUrl.equals(modelsRoot))
			{
				// file corresponds to RailsModelesFolderNode
				return forParent ? new NodeId[]{RailsProjectModuleNode.generateNodeId(module)} : new NodeId[]{ModelSubFolderNode.generateNodeId(fileUrl)};
			}
			// file is directory under app/models
			return new NodeId[]{ModelSubFolderNode.generateNodeId(forParent ? parentDirUrl : fileUrl)};
		}
		if(!RubyVirtualFileScanner.isRubyFile(fileName))
		{
			return null;
		}
		if(forParent)
		{
			if(selectedElement == null || (selectedElement instanceof RClass && RVirtualPsiUtil.getContainingRVClass((RClass) selectedElement) == null))
			{
				return new NodeId[]{ControllerSubFolderNode.generateNodeId(parentDirUrl)};
			}

			final RContainer parentVContainer = getParentContanerForSelection(selectedElement);
			if(parentVContainer == null)
			{
				return new NodeId[]{ControllerSubFolderNode.generateNodeId(parentDirUrl)};
			}
			return getNodeIdForClassOrControllerOrMethodInFile(file, module, parentVContainer);
		}
		else
		{
			return getNodeIdForClassOrControllerOrMethodInFile(file, module, selectedElement);
		}
	}

	private static NodeId[] processElementFromControllersRoot(VirtualFile file, String fileUrl, Module module, boolean forParent, Object selectedElement, String fileName, String nameWOExt, String parentDirUrl, String controllersRoot)
	{
		if(isDirectory(file, fileName))
		{
			if(fileUrl.equals(controllersRoot))
			{
				// file corresponds to RailsControllersFolderNode
				return forParent ? new NodeId[]{RailsProjectModuleNode.generateNodeId(module)} : new NodeId[]{ControllerSubFolderNode.generateNodeId(fileUrl)};
			}
			// file is directory under app/controllers
			return new NodeId[]{ControllerSubFolderNode.generateNodeId(forParent ? parentDirUrl : fileUrl)};
		}
		if(!RubyVirtualFileScanner.isRubyFile(fileName))
		{
			return null;
		}
		//file is application.rb
		if(ControllersConventions.isApplicationControllerFile(fileUrl, nameWOExt, module))
		{
			if(forParent)
			{
				return new NodeId[]{RailsApplicationFolderNode.generateNodeId(controllersRoot)};
			}
			else
			{
				return getNodeIdForClassOrControllerOrMethodInFile(file, module, selectedElement);
			}
		}
		//other controllers
		if(forParent)
		{
			if(selectedElement == null || (selectedElement instanceof RClass && ControllersConventions.isControllerClass((RClass) selectedElement, module)))
			{
				return new NodeId[]{ControllerSubFolderNode.generateNodeId(parentDirUrl)};
			}
			final RContainer parentVContainer = getParentContanerForSelection(selectedElement);
			if(parentVContainer == null)
			{
				return new NodeId[]{ControllerSubFolderNode.generateNodeId(parentDirUrl)};
			}
			return getNodeIdForClassOrControllerOrMethodInFile(file, module, parentVContainer);
		}
		else
		{
			return getNodeIdForClassOrControllerOrMethodInFile(file, module, selectedElement);
		}
	}

	@Nullable
	private static RContainer getParentContanerForSelection(@Nullable final Object selectedElement)
	{
		if(selectedElement instanceof RContainer)
		{
			return ((RContainer) selectedElement).getVirtualParentContainer();
		}
		else if(selectedElement instanceof PsiElement)
		{
			return RubyPsiUtil.getParentVContainer((PsiElement) selectedElement);
		}
		else
		{
			return null;
		}
	}

	private static NodeId[] getNodeIdForHelper(@Nullable final Object element, @Nonnull final Module module, @Nullable final VirtualFile file)
	{
		final RModule rModule;
		if(element instanceof RModule)
		{
			rModule = getAsVirtualModule((RModule) element);
		}
		else if(element instanceof RModule)
		{
			rModule = (RModule) element;
		}
		else
		{
			rModule = RContainerUtil.getFirstModuleInFile(file, module);
		}

		if(rModule == null)
		{
			return null;
		}
		return new NodeId[]{HelperNode.generateNodeId(rModule)};
	}

	@Nullable
	private static NodeId[] getNodeIdForClassOrControllerOrMethodInFile(@Nullable final VirtualFile file, @Nonnull final Module module, @Nullable final Object element)
	{
		if(file == null)
		{
			return null;
		}

		final RClass vClass;
		final RMethod vMethod;

		if(element instanceof RClass)
		{
			vClass = getAsVirtualClass((RClass) element);
			vMethod = null;
		}
		else if(element instanceof RMethod)
		{
			vMethod = getAsVirtualMethod((RMethod) element);
			vClass = vMethod == null ? getAsVirtualClass(RVirtualPsiUtil.getContainingRVClass((RMethod) element)) : getAsVirtualClass(RVirtualPsiUtil.getContainingRVClass(vMethod));
		}
		else
		{
			vClass = getAsVirtualClass(RContainerUtil.getFirstClassInFile(file, module));
			vMethod = null;
		}

		if(vClass == null && vMethod == null)
		{
			return null;
		}

		if(ControllersConventions.isControllerClass(vClass, module))
		{
			return vMethod == null ? new NodeId[]{ControllerClassNode.generateNodeId(vClass)} : new NodeId[]{ActionNode.generateNodeId(vMethod)};
		}
		else
		{
			return vMethod == null ? new NodeId[]{ClassNode.generateNodeId(vClass)} : new NodeId[]{MethodNode.generateNodeId(vMethod)};
		}
	}

	@Nullable
	private static RClass getAsVirtualClass(@Nullable final RClass rVirtualClass)
	{
		if(rVirtualClass instanceof RClass)
		{
			final RContainer container = RVirtualPsiUtil.findVirtualContainer((RContainer) rVirtualClass);
			return container instanceof RClass ? (RClass) container : null;
		}
		else
		{
			return rVirtualClass;
		}
	}

	@Nullable
	private static RModule getAsVirtualModule(@Nullable final RModule rVirtualModule)
	{
		if(rVirtualModule instanceof RModule)
		{
			final RContainer container = RVirtualPsiUtil.findVirtualContainer((RContainer) rVirtualModule);
			return container instanceof RModule ? (RModule) container : null;
		}
		else
		{
			return rVirtualModule;
		}
	}

	@Nullable
	private static RMethod getAsVirtualMethod(@Nullable final RMethod rVirtualMethod)
	{
		if(rVirtualMethod instanceof RMethod)
		{
			final RContainer container = RVirtualPsiUtil.findVirtualContainer((RContainer) rVirtualMethod);
			return container instanceof RMethod ? (RMethod) container : null;
		}
		else
		{
			return rVirtualMethod;
		}
	}

	private static boolean isDirectory(@Nullable final VirtualFile file, @Nonnull final String fileName)
	{
		return file != null ? file.isDirectory() : TextUtil.isEmpty(VirtualFileUtil.getExtension(fileName));
	}

	private static RClass getControllerClass(@Nonnull final String controllerUrl, final Module module)
	{
		final VirtualFile controllerFile = VirtualFileManager.getInstance().findFileByUrl(controllerUrl);
		if(controllerFile == null)
		{
			return null;
		}
		final PsiFile psiFile = PsiManager.getInstance(module.getProject()).findFile(controllerFile);
		if(!(psiFile instanceof RFile))
		{
			return null;
		}

		final List<RClass> allClasses = RContainerUtil.getTopLevelClasses((RFile) psiFile);
		for(RClass rClass : allClasses)
		{
			if(ControllersConventions.isControllerClass(rClass, module))
			{
				return rClass;
			}
		}
		return null;
	}

	private static RClass getControllerClass(final String controllerName, final String relativePath, final String controllersRoot, final Module module)
	{
		if(relativePath == null)
		{
			return null;
		}

		final String controllerClassName = ControllersConventions.getControllerClassName(controllerName);
		final String controllerFileName = ControllersConventions.getControllerFileName(controllerClassName);
		if(controllerFileName == null)
		{
			return null;
		}

		final StringBuilder buff = new StringBuilder(controllersRoot);
		if(!relativePath.equals(TextUtil.EMPTY_STRING))
		{
			buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
			buff.append(relativePath);
		}
		buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
		buff.append(controllerFileName);
		return getControllerClass(buff.toString(), module);
	}
}
