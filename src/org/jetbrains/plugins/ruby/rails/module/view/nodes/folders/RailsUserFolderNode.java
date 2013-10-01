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

import javax.swing.Icon;

import org.jetbrains.plugins.ruby.rails.RailsIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleNode;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 25, 2007
 */
public class RailsUserFolderNode extends UserSubFolderNode
{
	public RailsUserFolderNode(final Module module, final VirtualFile dir, final SimpleNode parent, final boolean isTestFolder)
	{
		super(module, dir, parent, initPresentationData(dir, isTestFolder), isTestFolder);
	}

	private static PresentationData initPresentationData(final VirtualFile dir, final boolean testFolder)
	{
		final Icon iconOpened;
		final Icon iconClosed;
		if(testFolder)
		{
			iconOpened = RailsTestsFolderNode.TEST_ROOT_OPENED;
			iconClosed = RailsTestsFolderNode.TEST_ROOT_CLOSED;
		}
		else
		{
			iconOpened = RailsIcons.RAILS_FOLDER_OPENED;
			iconClosed = RailsIcons.RAILS_FOLDER_CLOSED;

		}
		final String name = dir.getName();
		return new PresentationData(name, name, iconOpened, iconClosed, null);
	}
}