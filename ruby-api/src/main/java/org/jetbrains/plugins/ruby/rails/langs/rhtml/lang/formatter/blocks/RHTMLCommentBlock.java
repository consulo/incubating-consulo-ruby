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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.RHTMLFormatterUtil;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.impl.source.parsing.ChameleonTransforming;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 21, 2007
 */
public class RHTMLCommentBlock extends RHTMLBlock
{
	private Indent myChildrenIndent;

	public RHTMLCommentBlock(@Nonnull final ASTNode node, @Nullable final Indent indent, @Nullable final Alignment alignment, @Nullable final Wrap wrap, final XmlFormattingPolicy xmlFormattingPolicy)
	{
		super(node, indent, wrap, xmlFormattingPolicy, alignment);
		// "<$# ".length = 4
		myChildrenIndent = Indent.getSpaceIndent(4);
	}

	@Override
	@Nonnull
	protected List<Block> buildChildren()
	{
		ChameleonTransforming.transformChildren(myNode);

		final ArrayList<Block> result = new ArrayList<Block>();
		final Alignment alignment = Alignment.createAlignment();

		ASTNode child = myNode.getFirstChildNode();
		while(child != null)
		{
			if(RHTMLFormatterUtil.canBeCorrectBlock(child))
			{
				final IElementType childNodeType = child.getElementType();
				if(childNodeType == RHTMLTokenType.RHTML_COMMENT_START || childNodeType == RHTMLTokenType.RHTML_COMMENT_END)
				{
					//TODO move to settings - format comments or not
					result.add(new RHTMLBlock(child, Indent.getNoneIndent(), null, myXmlFormattingPolicy, alignment));
				}
				else
				{
					//TODO move to settings - align rhtml comments or not
					// thus indent =  Indent.getAbsoluteNoneIndent() or childrenIndent
					result.add(new RHTMLBlock(child, Indent.getAbsoluteNoneIndent(), null, myXmlFormattingPolicy, null));
				}
			}
			child = child.getTreeNext();
		}
		return result;
	}

	@Override
	@Nonnull
	public ChildAttributes getChildAttributes(int newChildIndex)
	{
		return new ChildAttributes(myChildrenIndent, null);
	}
}