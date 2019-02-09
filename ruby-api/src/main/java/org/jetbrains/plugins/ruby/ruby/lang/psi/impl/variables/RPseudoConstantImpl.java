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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NonNls;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.psi.RPseudoConstantReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.CoreTypes;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RPseudoConstant;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 05.09.2006
 */
public class RPseudoConstantImpl extends RNamedElementBase implements RPseudoConstant
{

	public static Map<String, String> TYPE_MAP = new HashMap<String, String>();

	static
	{
		TYPE_MAP.put(RubyTokenTypes.kFALSE.toString(), CoreTypes.FalseClass);
		TYPE_MAP.put(RubyTokenTypes.kFILE.toString(), CoreTypes.String);
		TYPE_MAP.put(RubyTokenTypes.kLINE.toString(), CoreTypes.Fixnum);
		TYPE_MAP.put(RubyTokenTypes.kNIL.toString(), CoreTypes.NilClass);
		TYPE_MAP.put(RubyTokenTypes.kTRUE.toString(), CoreTypes.TrueClass);
	}

	public RPseudoConstantImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	public void accept(@Nonnull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRPseudoConstant(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	protected PsiReference createReference()
	{
		return new RPseudoConstantReference(this);
	}

	@Override
	@Nullable
	protected String getPrefix()
	{
		return null;
	}

	@Override
	@Nonnull
	public RType getType(@Nullable final FileSymbol fileSymbol)
	{
		final String text = getText();
		// kSELF or kSUPER
		if(RubyTokenTypes.kSELF.toString().equals(text) || RubyTokenTypes.kSUPER.toString().equals(text))
		{
			return super.getType(fileSymbol);
		}

		// Core types
		final String coreType = TYPE_MAP.get(text);
		if(coreType != null)
		{
			if(CoreTypes.TrueClass.equals(coreType) || CoreTypes.FalseClass.equals(coreType))
			{
				return RTypeUtil.getBooleanType(fileSymbol);
			}
			return RTypeUtil.createTypeBySymbol(fileSymbol, SymbolUtil.getTopLevelClassByName(fileSymbol, coreType), Context.INSTANCE, true);
		}
		return RType.NOT_TYPED;
	}

	@Override
	protected void checkName(@NonNls @Nonnull String newName) throws IncorrectOperationException
	{
		if(!TextUtil.isCID(newName))
		{
			throw new IncorrectOperationException(RBundle.message("rename.incorrect.name"));
		}
	}
}
