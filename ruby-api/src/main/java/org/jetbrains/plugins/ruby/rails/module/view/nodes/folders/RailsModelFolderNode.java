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

package org.jetbrains.plugins.ruby.rails.module.view.nodes.folders;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.StandardRailsPaths;
import org.jetbrains.plugins.ruby.rails.module.view.RailsNodeVisitor;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleNodeVisitor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 29.09.2006
 */
public class RailsModelFolderNode extends ModelSubFolderNode
{
	private static final String MODELS_VIEW_NAME = RBundle.message("rails.project.module.view.nodes.model.presentable");

	public RailsModelFolderNode(final Module module, final VirtualFile rootDir)
	{
		super(module, rootDir, null, initPresentationData());
	}

	@Override
	public void accept(final SimpleNodeVisitor visitor)
	{
		if(visitor instanceof RailsNodeVisitor)
		{
			((RailsNodeVisitor) visitor).visitModelNode();
			return;
		}
		super.accept(visitor);

	}

	private static PresentationData initPresentationData()
	{
		return new PresentationData(MODELS_VIEW_NAME, MODELS_VIEW_NAME, RailsIcons.RAILS_MODEL_NODES, null);
	}

	@Override
	@Nonnull
	public RailsProjectNodeComparator.NodeType getType()
	{
		return RailsProjectNodeComparator.NodeType.SPECIAL_FOLDER;
	}

	@Override
	public SimpleNode[] getChildren()
	{
		final ArrayList<SimpleNode> children = new ArrayList<SimpleNode>();
		//adds migrations
		final Module module = getModule();
		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for modules with Rails Support
		final VirtualFile migr = VirtualFileManager.getInstance().findFileByUrl(railsPaths.getMigrationsRootURL());
		if(migr != null)
		{
			children.add(new MigrationsFolder(module, migr, this));
		}

		//add other
		children.addAll(Arrays.asList(super.getChildren()));
		return children.toArray(new SimpleNode[children.size()]);
	}
}
