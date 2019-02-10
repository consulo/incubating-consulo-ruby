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
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.module.view.RailsNodeVisitorAdapter;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.ClassNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.RailsNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.SimpleFileNode;
import org.jetbrains.plugins.ruby.ruby.cache.RubyModuleCachesManager;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleNodeVisitor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 08.10.2006
 */
public abstract class FolderNode extends SimpleFileNode
{
	private final SimpleNode myParentNode;

	public FolderNode(final Module module, final VirtualFile dir, final SimpleNode parent, final PresentationData data)
	{

		super(module, dir);
		myParentNode = parent;

		init(getElement(), data);
	}

	public FolderNode(final Module module, final VirtualFile dir, final SimpleNode parent)
	{
		this(module, dir, parent, initPresentationData(dir.getName()));
	}

	protected void processNotDirectoryFile(final List<RailsNode> nodes, final VirtualFile file, final String url)
	{
		if(!RubyVirtualFileScanner.isRubyFile(file))
		{
			return;
		}
		final RubyModuleCachesManager manager = RubyModuleCachesManager.getInstance(getModule());
		final RFileInfo rFileInfo = manager.getFilesCache().getUp2DateFileInfo(file);
		if(rFileInfo == null)
		{
			return;
		}

		final List<RVirtualClass> allClasses = RContainerUtil.getTopLevelClasses(rFileInfo.getRVirtualFile());

		for(final RVirtualClass rClass : allClasses)
		{
			nodes.add(createClassNode(rClass, rFileInfo));
		}
	}

	protected ClassNode createClassNode(final RVirtualClass rClass, final RFileInfo rFileInfo)
	{
		return new ClassNode(getModule(), rClass, rFileInfo);
	}

	@Override
	public void accept(SimpleNodeVisitor visitor)
	{
		myParentNode.accept(visitor);
	}

	@Override
	public SimpleNode[] getChildren()
	{
		final List<RailsNode> children = new ArrayList<RailsNode>();
		final Module module = getModule();

		final VirtualFile directory = getVirtualFile();
		assert directory != null;
		final List<VirtualFile> files = RubyVirtualFileScanner.searchFilesUnderDirectory(module, directory, true);
		for(final VirtualFile file : files)
		{
			if(!file.isDirectory())
			{
				processNotDirectoryFile(children, file, file.getUrl());
				continue;
			}
			/**
			 * searchFilesUnderDirectory() includes directory itself
			 */
			if(file.equals(directory))
			{
				continue;
			}
			/**
			 * Create sub folder with corresponding type
			 */
			accept(new RailsNodeVisitorAdapter()
			{
				@Override
				public void visitModelNode()
				{
					children.add(new ModelSubFolderNode(module, file, FolderNode.this));
				}

				@Override
				public void visitControllerNode()
				{
					children.add(new ControllerSubFolderNode(module, file, FolderNode.this));
				}

				@Override
				public void visitTestNode()
				{
					children.add(new TestsSubFolderNode(module, file, FolderNode.this));
				}

				@Override
				public void visitUserNode(final boolean isUnderTestsRoot)
				{
					children.add(new UserSubFolderNode(module, file, FolderNode.this, isUnderTestsRoot));
				}

				@Override
				public void visitSharedPartialsNode()
				{
					children.add(new SharedPartialsSubFolderNode(module, file, FolderNode.this));
				}
			});
		}
		return children.toArray(new RailsNode[children.size()]);
	}

	@Override
	@Nonnull
	public RailsProjectNodeComparator.NodeType getType()
	{
		return RailsProjectNodeComparator.NodeType.FOLDER;
	}

	@Override
	public boolean expandOnDoubleClick()
	{
		return true;
	}

	private static PresentationData initPresentationData(final String name)
	{
		return new PresentationData(name, name, RailsIcons.RAILS_FOLDER_CLOSED, null);
	}

	protected SimpleNode getParentNode()
	{
		return myParentNode;
	}
}
