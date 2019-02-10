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

import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.module.view.RailsNodeVisitor;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.RailsNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.SimpleFileNode;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleNodeVisitor;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 25, 2007
 */
public class UserSubFolderNode extends FolderNode
{
	private boolean myIsUnderTestsRoot;

	public UserSubFolderNode(final Module module, final VirtualFile dir, final SimpleNode parent, final boolean isUnderTestsFolder)
	{
		super(module, dir, parent, initPresentationData(dir, isUnderTestsFolder));
		myIsUnderTestsRoot = isUnderTestsFolder;
	}

	private static PresentationData initPresentationData(final VirtualFile dir, final boolean testFolder)
	{
		final Image iconClosed;
		if(testFolder)
		{
			iconClosed = TestsSubFolderNode.TESTS_CLOSED;
		}
		else
		{
			iconClosed = RailsIcons.RAILS_FOLDER_CLOSED;
		}
		final String name = dir.getName();
		return new PresentationData(name, name, iconClosed, null);
	}


	@Override
	public void accept(SimpleNodeVisitor visitor)
	{
		if(visitor instanceof RailsNodeVisitor)
		{
			((RailsNodeVisitor) visitor).visitUserNode(myIsUnderTestsRoot);
			return;
		}
		super.accept(visitor);
	}

	public UserSubFolderNode(final Module module, final VirtualFile dir, final SimpleNode parent, final PresentationData data, final boolean isUnderTestsFolder)
	{
		super(module, dir, parent, data);
		myIsUnderTestsRoot = isUnderTestsFolder;
	}

	@Override
	protected void processNotDirectoryFile(final List<RailsNode> nodes, final VirtualFile file, final String url)
	{
		nodes.add(new SimpleFileNode(getModule(), file));
	}

	@Override
	@Nonnull
	public RailsProjectNodeComparator.NodeType getType()
	{
		return RailsProjectNodeComparator.NodeType.USER_FOLDERS_ROOT;
	}
}

