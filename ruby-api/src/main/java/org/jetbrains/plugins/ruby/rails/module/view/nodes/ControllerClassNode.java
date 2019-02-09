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

package org.jetbrains.plugins.ruby.rails.module.view.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.rails.module.view.RailsNodeVisitor;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.folders.PartialsFolder;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.HelpersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.ViewsConventions;
import org.jetbrains.plugins.ruby.rails.presentation.RControllerPresentationUtil;
import org.jetbrains.plugins.ruby.ruby.cache.RubyModuleCachesManager;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyModuleFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.treeStructure.SimpleNodeVisitor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 11.10.2006
 */
public class ControllerClassNode extends ClassNode
{

	public ControllerClassNode(final Module module, final RVirtualClass rVClass, final RFileInfo fileInfo)
	{

		super(module, rVClass, fileInfo);

		init(getElement(), RControllerPresentationUtil.getPresentation(getRubyClass()));
	}

	@Override
	public void accept(final SimpleNodeVisitor visitor)
	{
		if(visitor instanceof RailsNodeVisitor)
		{
			((RailsNodeVisitor) visitor).visitControllerNode();
			return;
		}
		super.accept(visitor);
	}

	@Override
	public RailsNode[] getChildren()
	{
		final ArrayList<RailsNode> children = new ArrayList<RailsNode>();
		final String className = getRubyClass().getName();

		final String cName = ControllersConventions.getControllerNameByClassName(className);
		final Module module = getModule();

		final String parentDirUrl = getParentDirUrl();
		if(cName != null && parentDirUrl != null)
		{
			/**
			 * Add layouts
			 */
			final List<VirtualFile> layouts = ViewsConventions.getLayouts(parentDirUrl, cName, module);
			for(VirtualFile layout : layouts)
			{
				children.add(new LayoutNode(module, layout));
			}

			/**
			 * Add helper
			 */
			final String helperUrl = HelpersConventions.getHelperURL(parentDirUrl, cName, module);
			assert helperUrl != null; // helper url for controller always exists

			final VirtualFile helperFile = VirtualFileManager.getInstance().findFileByUrl(helperUrl);
			if(helperFile != null)
			{
				final RubyModuleFilesCache cache = RubyModuleCachesManager.getInstance(module).getFilesCache();
				final RFileInfo helperFileInfo = cache.getUp2DateFileInfo(helperFile);
				assert helperFileInfo != null; // shouldn't be null
				final RVirtualModule rModule = HelpersConventions.getHelperModule(helperFileInfo.getRVirtualFile(), className);
				if(rModule != null)
				{
					children.add(new HelperNode(module, rModule, helperUrl));
				}
			}
		}
		/**
		 * Add actions
		 */
		final RailsNode[] actionNodes = super.getChildren();
		children.addAll(Arrays.asList(actionNodes));

		/**
		 * Add partial templates
		 */
		final VirtualFile folder = ViewsConventions.getViewsFolder(getVirtualFile(), module);
		if(folder != null)
		{
			children.add(new PartialsFolder(module, folder, this));
		}
		return children.toArray(new RailsNode[children.size()]);
	}

	@Override
	@Nonnull
	public RailsProjectNodeComparator.NodeType getType()
	{
		return RailsProjectNodeComparator.NodeType.CONTROLLER;
	}
}