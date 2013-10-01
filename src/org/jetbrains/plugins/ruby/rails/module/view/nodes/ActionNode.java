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
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.ViewsConventions;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleNode;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 15.10.2006
 */
public class ActionNode extends MethodNode
{
	// For node id generator see MethodNode.generateNodeId(),
	// if you want change it, then fix RailsProjectViewPane.buildNodeIdsPath()

	private final String myControllerDirUrl;
	private final String myControllerName;

	public ActionNode(final Module module, final RVirtualMethod method, final String controllerDirUrl, final String fileUrl, final String controllerName)
	{
		super(module, method, fileUrl);
		myControllerDirUrl = controllerDirUrl;
		myControllerName = controllerName;
	}

	@Override
	public SimpleNode[] getChildren()
	{
		final List<RailsNode> childNodes = new ArrayList<RailsNode>();
		final Module module = getModule();

		if(ControllersConventions.isValidActionMethod(getMethod()))
		{
			final List<VirtualFile> views = ViewsConventions.getViews(getMethod(), myControllerDirUrl, myControllerName, module);
			for(VirtualFile view : views)
			{
				childNodes.add(new SimpleFileNode(module, view));
			}
		}
		return childNodes.toArray(new RailsNode[childNodes.size()]);
	}

	@Override
	@NotNull
	public RailsProjectNodeComparator.NodeType getType()
	{
		return RailsProjectNodeComparator.NodeType.ACTION;
	}
}
