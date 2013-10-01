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

import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.module.view.RailsNodeVisitor;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleNodeVisitor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 27, 2007
 */
public class RailsSharedPatialsFolderNode extends SharedPartialsSubFolderNode
{
	private static final String SHARED_PARTIALS_FOLDER = RBundle.message("rails.project.module.view.nodes.shared.partials.presentable");

	public RailsSharedPatialsFolderNode(final Module module, final VirtualFile folder)
	{
		super(module, folder, null, initPresentationData());
	}

	@Override
	public void accept(SimpleNodeVisitor visitor)
	{
		if(visitor instanceof RailsNodeVisitor)
		{
			((RailsNodeVisitor) visitor).visitSharedPartialsNode();
			return;
		}
		super.accept(visitor);
	}

	private static PresentationData initPresentationData()
	{
		return new PresentationData(SHARED_PARTIALS_FOLDER, SHARED_PARTIALS_FOLDER, RailsIcons.RAILS_PARTIALS_OPEN, RailsIcons.RAILS_PARTIALS_CLOSED, null);
	}
}