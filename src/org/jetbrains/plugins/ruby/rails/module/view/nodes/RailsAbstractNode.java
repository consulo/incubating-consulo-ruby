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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeId;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 02.03.2007
 */
public abstract class RailsAbstractNode extends SimpleNode
{

	protected RailsAbstractNode[] CHILDREN_EMPTY = new RailsAbstractNode[0];

	private NodeId myId;
	private ItemPresentation myPresentation;
	private Object[] myEqObjects;

	public RailsAbstractNode(final Project project)
	{
		super(project);
	}

	public void init(final NodeId id, @NotNull final ItemPresentation presentation)
	{
		myId = id;
		myEqObjects = new Object[]{id};
		myPresentation = presentation;
		setIcon(myPresentation.getIcon(false));
	}

	@Override
	public Object[] getEqualityObjects()
	{
		return myEqObjects;
	}

	/**
	 * @return Node type for sorting in <code>RailsProjectNodeComparator</code>
	 */
	@NotNull
	public abstract RailsProjectNodeComparator.NodeType getType();

	@Override
	public NodeId getElement()
	{
		return myId;
	}

	@Override
	protected void doUpdate()
	{
		myPresentation = updatePresentation(myPresentation);
		setIcon(myPresentation.getIcon(false));
		setPlainText(myPresentation.getPresentableText());
		setIcon(getIcon());
	}

	/**
	 * You can override this method and change presentation data.
	 *
	 * @param presentation Presentation data for update.
	 * @return updated presentation
	 */
	protected ItemPresentation updatePresentation(final ItemPresentation presentation)
	{
		return presentation;
	}


	@Override
	public boolean expandOnDoubleClick()
	{
		return false;
	}

	@Override
	public SimpleNode[] getChildren()
	{
		return CHILDREN_EMPTY;
	}


}
