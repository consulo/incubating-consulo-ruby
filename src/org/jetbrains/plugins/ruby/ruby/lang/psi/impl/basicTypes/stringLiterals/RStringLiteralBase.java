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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.ParamContext;
import org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.ParamDef;
import org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.ParamDefReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.ParamDefUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.CoreTypes;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RExpressionSubstitution;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 14.08.2006
 */
public class RStringLiteralBase extends RPsiElementBase implements RStringLiteral {
    public RStringLiteralBase(ASTNode astNode) {
        super(astNode);
    }

    @Override
	public String getContent() {
        String content = "";
        List<PsiElement> list = getPsiContent();
        for (PsiElement e : list){
            content+=e.getText();
        }
        return content;
    }

    @Override
	public boolean hasExpressionSubstitutions() {
        return getExpressionSubstitutions().size()!=0;
    }

    @Override
	public List<RExpressionSubstitution> getExpressionSubstitutions() {
        List<PsiElement> list = RubyPsiUtil.getChildrenByFilter(this, RubyElementTypes.EXPR_SUBTITUTION);
        ArrayList<RExpressionSubstitution> exprList = new ArrayList<RExpressionSubstitution>();
        for (PsiElement e : list){
            exprList.add((RExpressionSubstitution) e);
        }
        return exprList;
    }

    @Override
	public List<PsiElement> getPsiContent() {
        return RubyPsiUtil.getChildrenByFilter(this, BNF.tSTRING_LIKE_CONTENTS);
    }

    @Override
	@NotNull
    public RType getType(@Nullable final FileSymbol fileSymbol) {
        return RTypeUtil.createTypeBySymbol(fileSymbol, SymbolUtil.getTopLevelClassByName(fileSymbol, CoreTypes.String), Context.INSTANCE, true);
    }

    @Override
	public PsiReference getReference() {
        ParamContext paramContext = ParamDefUtil.getParamContext(this);
        if (paramContext == null) return null;
        ParamDef paramDef = ParamDefUtil.getParamDef(paramContext);
        if (paramDef != null) {
            return new ParamDefReference(this, paramDef, paramContext);
        }
        return null;
    }
}
