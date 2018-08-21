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
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeId;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeIdUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 13.10.2006
 */
public class HelperNode extends RailsNode
{
	private RVirtualModule myRModule;

	public HelperNode(final Module module, final RVirtualModule rModule, final String fileUrl)
	{
		super(module);

		myRModule = rModule;
		init(generateNodeId(rModule), initPresentationData(rModule.getName()));
		assert getVirtualFileUrl().equals(fileUrl);
	}

	public static NodeId generateNodeId(final RVirtualModule rModule)
	{
		return NodeIdUtil.createForVirtualContainer(rModule);
	}

	@Override
	public RailsNode[] getChildren()
	{
		final List<RailsNode> children = new ArrayList<RailsNode>();

		for(RVirtualStructuralElement element : RContainerUtil.selectVirtualElementsByType(myRModule.getVirtualStructureElements(), StructureType.METHOD))
		{
			assert element instanceof RVirtualMethod;
			children.add(new MethodNode(getModule(), (RVirtualMethod) element, getVirtualFileUrl()));
		}

		return children.toArray(new RailsNode[children.size()]);
	}

	@Override
	@NotNull
	public RailsProjectNodeComparator.NodeType getType()
	{
		return RailsProjectNodeComparator.NodeType.HELPER;
	}

	private static PresentationData initPresentationData(final String name)
	{
		return new PresentationData(name, name, RailsIcons.RAILS_HELPERS_NODES, RailsIcons.RAILS_HELPERS_NODES, null);
	}
}
