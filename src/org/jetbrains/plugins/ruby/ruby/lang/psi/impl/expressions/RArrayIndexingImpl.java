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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RQualifiedReference;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RArrayIndexing;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 14.07.2005
 */
public class RArrayIndexingImpl extends RPsiElementBase implements RArrayIndexing
{
	public RArrayIndexingImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	@Nullable
	public RPsiElement getReciever()
	{
		PsiElement object = getFirstChild();
		return object instanceof RPsiElement ? (RPsiElement) object : null;
	}

	public boolean isInDefinition()
	{
		return RAssignmentExpressionNavigator.getAssignmentByLeftPart(this) != null;
	}

	@Override
	@NotNull
	public PsiReference[] getReferences()
	{
		final PsiElement lBracket = getChildByFilter(RubyTokenTypes.tfLBRACK, 0);
		final PsiElement rBracket = getChildByFilter(RubyTokenTypes.tRBRACK, 0);
		final String name = isInDefinition() ? RubyTokenTypes.tASET.toString() : RubyTokenTypes.tAREF.toString();
		final ArrayList<PsiReference> refs = new ArrayList<PsiReference>();
		if(lBracket != null)
		{
			refs.add(new RQualifiedReference(getProject(), this, getReciever(), lBracket, RReference.Type.COLON_REF, name));
		}
		if(rBracket != null)
		{
			refs.add(new RQualifiedReference(getProject(), this, getReciever(), rBracket, RReference.Type.COLON_REF, name));
		}
		return refs.toArray(new PsiReference[refs.size()]);
	}

	@Override
	@Nullable
	public RListOfExpressions getValue()
	{
		PsiElement list = getChildByFilter(RubyElementTypes.LIST_OF_EXPRESSIONS, 0);
		return list instanceof RListOfExpressions ? (RListOfExpressions) list : null;
	}
}
