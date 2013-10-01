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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.spacing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementType;
import com.intellij.formatting.Block;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlTokenType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 6, 2007
 */
public class RHTMLSpacingProcessor
{

	/**
	 * Calculates spacing between two ambigiuous children
	 *
	 * @param parentBlock    Parent block
	 * @param parentNode     Parent Node
	 * @param leftBlockNode  Left child node
	 * @param rightBlockNode Right child node
	 * @param settings       Current code style settings @return Spacing object
	 * @return spacing
	 */
	@Nullable
	public static Spacing getSpacing(@NotNull final Block parentBlock, @NotNull final ASTNode parentNode, @NotNull final ASTNode leftBlockNode, @NotNull final ASTNode rightBlockNode, final CodeStyleSettings settings)
	{

		final IElementType leftBlockType = leftBlockNode.getElementType();
		final IElementType rightBlockType = rightBlockNode.getElementType();
		final IElementType parentNodeType = parentNode.getElementType();

		//injections
		if(parentNodeType == RHTMLElementType.RHTML_XML_TAG)
		{
			//left child
			if(RHTMLTokenType.RHTML_SEPARATORS_STARTS.contains(leftBlockType))
			{
				if(leftBlockType == RHTMLTokenType.RHTML_SCRIPTLET_START && rightBlockType == RHTMLTokenType.OMIT_NEW_LINE)
				{
					return createNoSPacing(settings);
				}
				return createSingleSpacing(settings);
			}
			if(leftBlockType == RHTMLTokenType.OMIT_NEW_LINE)
			{
				if(RHTMLTokenType.RHTML_SEPARATORS_ENDS.contains(rightBlockType))
				{
					final ASTNode leftPrev = leftBlockNode.getTreePrev();
					if(leftPrev == null || !RHTMLTokenType.RHTML_SEPARATORS_STARTS.contains(leftPrev.getElementType()))
					{
						return createNoSPacing(settings);
					}
				}
				return createSingleSpacing(settings);
			}

			//right child
			if(rightBlockType == RHTMLTokenType.OMIT_NEW_LINE)
			{
				return createSingleSpacing(settings);
			}
			if(RHTMLTokenType.RHTML_SEPARATORS_ENDS.contains(rightBlockType))
			{
				return createSingleSpacing(settings);
			}
		}

		//attributes
		if(parentNodeType == XmlElementType.XML_ATTRIBUTE)
		{
			if(leftBlockType == XmlTokenType.XML_NAME || leftBlockType == XmlTokenType.XML_EQ || rightBlockType == XmlTokenType.XML_EQ || rightBlockType == XmlTokenType.XML_ATTRIBUTE_VALUE_START_DELIMITER)
			{
				createNoSPacing(settings);
			}
			if(rightBlockType == XmlTokenType.XML_NAME)
			{
				if(leftBlockType == XmlTokenType.XML_ATTRIBUTE_VALUE_END_DELIMITER)
				{
					createNoSPacing(settings);
				}
				if(RHTMLTokenType.RHTML_SEPARATORS_ENDS.contains(leftBlockType))
				{
					final ASTNode rightBlockNodePrev = rightBlockNode.getTreePrev();
					if(rightBlockNodePrev == null || !(rightBlockNodePrev.getPsi() instanceof PsiWhiteSpace))
					{
						return createSingleSpacing(settings);
					}
					createNoSPacing(settings);
				}
			}
		}
		// => left !=
		//        if (SpacingTokens.NO_SPACING_BEFORE.contains(rightBlockNode.getElementType())) {
		//            return NO_SPACING;
		//        }
		//        if (rightBlockNode.getPsi() instanceof RCondition) {
		//            return NO_EOL_SPACING;
		//        }
		return null;
	}

	private static Spacing createNoSPacing(final @NotNull CodeStyleSettings settings)
	{
		return Spacing.createSpacing(0, 0, 0, settings.KEEP_LINE_BREAKS, settings.KEEP_BLANK_LINES_IN_CODE);
	}

	private static Spacing createSingleSpacing(final @NotNull CodeStyleSettings settings)
	{
		return Spacing.createSpacing(1, 1, 0, settings.KEEP_LINE_BREAKS, settings.KEEP_BLANK_LINES_IN_CODE);
	}
}
