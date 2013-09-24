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

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RFieldAttrReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.psi.RFNameReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.RSymbolNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RFName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 19, 2007
 */
public class RFNameImpl extends RNamedElementBase implements RFName {
    public RFNameImpl(@NotNull final ASTNode astNode) {
        super(astNode);
    }

    protected PsiReference createReference() {
        throw new UnsupportedOperationException("Method createReference is not implemented in org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.RFNameImpl");
    }

    public PsiReference getReference() {
        // We shouldn`t return this reference if we have RFieldAttrReference, overriding this reference
        final RSymbol symbol = RSymbolNavigator.getSymbolByObject(this);
        if (symbol!=null){
            final RCall rCall = PsiTreeUtil.getParentOfType(symbol, RCall.class);
            if (rCall!=null && rCall.getCallType().isAttributeCall()){
                for (PsiReference reference : rCall.getReferences()) {
                    if (reference instanceof RFieldAttrReference &&
                            ((RFieldAttrReference) reference).getRefValue() == symbol){
                        return null;
                    }
                }
            }
        }
        return new RFNameReference(this);
    }

    public void accept(@NotNull final PsiElementVisitor visitor) {
        if (visitor instanceof RubyElementVisitor) {
            ((RubyElementVisitor)visitor).visitRFName(this);
            return;
        }
        super.accept(visitor);
    }

    @Nullable
    protected String getPrefix() {
        return null;
    }

    protected void checkName(@NonNls @NotNull String newName) throws IncorrectOperationException {
        if (!TextUtil.isCID(newName) && !TextUtil.isFID(newName) && !TextUtil.isAID(newName)){
            throw new IncorrectOperationException(RBundle.message("rename.incorrect.name"));
        }
    }

    public PsiElement setName(@NotNull String newElementName) throws IncorrectOperationException {
        // We shouldn`t do anything if name is the same
        if (newElementName.equals(getName())){
            return null;
        }
        checkName(newElementName);
        final PsiElement element = RubyPsiUtil.getTopLevelElements(getProject(), ':' + newElementName).get(0).
                getChildByType(RPsiElement.class, 0);
        //noinspection ConstantConditions
        RubyPsiUtil.replaceInParent(this.getFirstChild(), element);
        return element;
    }

    @NotNull
    public RType getType(@Nullable final FileSymbol fileSymbol) {
        return RTypeUtil.createTypeBySymbol(fileSymbol, ResolveUtil.resolveToSymbol(fileSymbol, getReference()), Context.INSTANCE, true);
    }
}
