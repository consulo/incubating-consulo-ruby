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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.rails.RailsUtil;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeId;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeIdUtil;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 28.09.2006
 */
public class RailsProjectRootNode extends RailsAbstractNode
{
	private Map<Module, RailsProjectModuleNode> myModulesNodes = new HashMap<>();

	public RailsProjectRootNode(@Nonnull final Project project)
	{
		super(project);
		init(generateNodeId(), new PresentationData(null, null, null, null));
	}

	public static NodeId generateNodeId()
	{
		return NodeIdUtil.createForRoot();
	}

	@Override
	public SimpleNode[] getChildren()
	{
		final Map<Module, RailsProjectModuleNode> newNodes = new HashMap<Module, RailsProjectModuleNode>();
		final Module[] allRailsModules = RailsUtil.getAllModulesWithRailsSupport(myProject);
		for(final Module module : allRailsModules)
		{
			RailsProjectModuleNode node = myModulesNodes.get(module);
			if(node == null)
			{
				node = new RailsProjectModuleNode(module);
			}
			newNodes.put(module, node);
		}
		myModulesNodes = newNodes;
		final Collection<RailsProjectModuleNode> moduleNodes = newNodes.values();
		return moduleNodes.toArray(new SimpleNode[moduleNodes.size()]);
	}

	@Override
	@Nonnull
	public RailsProjectNodeComparator.NodeType getType()
	{
		return RailsProjectNodeComparator.NodeType.ROOT;
	}
}