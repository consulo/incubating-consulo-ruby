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

package org.jetbrains.plugins.ruby.ruby.lang.formatter.processors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.RubyBlock;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.models.RWrapAndIndentCOMPSTMT;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.models.indent.RIndentCOMPSTMT;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.heredocs.RHeredocValue;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RBodyStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 01.08.2006
 */
public class RubyIndentProcessor implements RubyTokenTypes
{

	/**
	 * Calculates indent, based on code style, between parent block and blockNode node
	 *
	 * @param parent        parent block
	 * @param blockNode     blockNode node
	 * @param prevBlockNode previous block node
	 * @return indent
	 */
	@Nonnull
	public static Indent getChildIndent(@Nonnull final RubyBlock parent, @Nullable final ASTNode prevBlockNode, @Nonnull final ASTNode blockNode)
	{
		final PsiElement psiParent = parent.getNode().getPsi();
		final PsiElement psiChild = blockNode.getPsi();
		final IElementType childType = blockNode.getElementType();

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// RFile
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if(psiParent instanceof RFile)
		{
			return Indent.getNoneIndent();
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// if heredoc content, heredoc_end or block comment
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if(psiChild instanceof RHeredocValue || childType == tHEREDOC_END ||
				BNF.tBLOCK_COMMENT_TOKENS.contains(childType))
		{
			return Indent.getAbsoluteNoneIndent();
		}


		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// TLINE_COMMENT
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if(childType == TLINE_COMMENT)
		{
			// if line comment has absolutely no indent, we shouldn`t touch it
			final ASTNode prevNode = blockNode.getTreePrev();
			if(prevNode != null && prevNode.getElementType() == tEOL)
			{
				return Indent.getAbsoluteNoneIndent();
			}
			if(psiParent instanceof RWrapAndIndentCOMPSTMT || psiParent instanceof RContainer)
			{
				return Indent.getNormalIndent();
			}
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// if after tCONTINUATION ContinuationIndent needed
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if(prevBlockNode != null)
		{
			if(BNF.tCONTINUATION_INDENT.contains(prevBlockNode.getElementType()) && !(psiChild instanceof RBodyStatement || psiChild instanceof RCompoundStatement))
			{
				return Indent.getContinuationIndent();
			}
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////// RIndentCOMPSTMT ////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if(psiParent instanceof RIndentCOMPSTMT && psiChild instanceof RCompoundStatement)
		{
			return Indent.getNormalIndent();
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////// Default Indent /////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		return Indent.getNoneIndent();
	}
}
