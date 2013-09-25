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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
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
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 13.06.2006
 */
public class RSymbolImpl extends RPsiElementBase implements RSymbol {

    public RSymbolImpl(ASTNode astNode) {
        super(astNode);
    }

    @Override
	@NotNull
    public PsiElement getObject() {
        //noinspection ConstantConditions
        return getLastChild();
    }

    @Override
	public void accept(@NotNull final PsiElementVisitor visitor) {
        if (visitor instanceof RubyElementVisitor){
            ((RubyElementVisitor) visitor).visitRSymbol(this);
            return;
        }
        super.accept(visitor);
    }

    @Override
	@NotNull
    public PsiReference[] getReferences() {
        final PsiElement content = getObject();
        final RFile rFile = RubyPsiUtil.getRFile(content);
        assert rFile!=null;
        ParamContext paramContext = ParamDefUtil.getParamContext(this);
        if (paramContext != null) {
            ParamDef paramDef = ParamDefUtil.getParamDef(paramContext);
            if (paramDef != null) {
                return new PsiReference[] { new ParamDefReference(this, paramDef, paramContext) };
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }

    @Override
	@NotNull
    public RType getType(@Nullable final FileSymbol fileSymbol) {
        return RTypeUtil.createTypeBySymbol(fileSymbol, SymbolUtil.getTopLevelClassByName(fileSymbol, CoreTypes.Symbol), Context.INSTANCE, true);
    }
}
