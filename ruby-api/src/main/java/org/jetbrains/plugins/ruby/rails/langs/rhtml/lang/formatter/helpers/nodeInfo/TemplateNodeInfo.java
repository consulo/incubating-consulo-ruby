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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.nodeInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;

public class TemplateNodeInfo extends NodeInfo
{
	private final ASTNode templateParentNode;
	private final ASTNode templateDataNode;
	@Nullable
	private final TextRange nextNodeTRange;

	@Nullable
	private final ASTNode nextTemplateNode;

	protected TemplateNodeInfo(@Nonnull final ASTNode templateParentNode, @Nonnull final ASTNode templateDataNode, @Nullable final TextRange nextNodeTRange, @Nullable final ASTNode nextTemplateNode)
	{

		this.templateParentNode = templateParentNode;
		this.templateDataNode = templateDataNode;
		this.nextNodeTRange = nextNodeTRange;
		this.nextTemplateNode = nextTemplateNode;
	}

	@Override
	public NodeType getType()
	{
		return NodeType.TEMPLATE_NODE;
	}

	@Nonnull
	public ASTNode getTemplateParentNode()
	{
		return templateParentNode;
	}

	@Nonnull
	public ASTNode getTemplateDataNode()
	{
		return templateDataNode;
	}

	@Override
	@Nullable
	public TextRange getNextNodeTRange()
	{
		return nextNodeTRange;
	}

	@Nullable
	public ASTNode getNextTemplateNode()
	{
		return nextTemplateNode;
	}
}
