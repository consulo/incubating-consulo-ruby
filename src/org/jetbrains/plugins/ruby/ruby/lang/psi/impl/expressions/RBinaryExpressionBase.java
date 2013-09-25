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

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.RubyPsiManager;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RQualifiedReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.TypeInferenceHelper;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RBinaryExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 23, 2007
 */
public abstract class RBinaryExpressionBase extends RPsiElementBase implements RBinaryExpression {
    public RBinaryExpressionBase(@NotNull ASTNode astNode) {
        super(astNode);
    }

    @Override
	public void accept(@NotNull PsiElementVisitor visitor){
        if (visitor instanceof RubyElementVisitor){
            ((RubyElementVisitor) visitor).visitRBinaryExpression(this);
            return;
        }
        super.accept(visitor);
    }

    @Override
	@NotNull
    public IElementType getOperationType() {
        final PsiElement op = getOperation();
        //noinspection ConstantConditions
        return op.getNode().getElementType();
    }

    @NotNull
    private PsiElement getOperation() {
        //noinspection ConstantConditions
        return getChildByFilter(BNF.tBINARY_OPS, 0);
    }

    @Override
	@NotNull
    public RPsiElement getLeftOperand() {
        //noinspection ConstantConditions
        return PsiTreeUtil.getPrevSiblingOfType(getOperation(), RPsiElement.class);
    }

    @Override
	@Nullable
    public RPsiElement getRightOperand() {
        //noinspection ConstantConditions
        return PsiTreeUtil.getNextSiblingOfType(getOperation(), RPsiElement.class);
    }

    @Override
	@NotNull
    public PsiReference getReference() {
        if (getOperation().getText().equals(RubyTokenTypes.tNEQ.toString())){
            return new RQualifiedReference(getProject(), this, getLeftOperand(), getOperation(), RReference.Type.COLON_REF, RubyTokenTypes.tEQ.toString());
        }
        if (getOperation().getText().equals(RubyTokenTypes.tNMATCH.toString())){
            return new RQualifiedReference(getProject(), this, getLeftOperand(), getOperation(), RReference.Type.COLON_REF, RubyTokenTypes.tMATCH.toString());
        }
        return new RQualifiedReference(getProject(), this, getLeftOperand(), getOperation(), RReference.Type.COLON_REF);
    }

    @Override
	@NotNull
    public RType getType(@Nullable final FileSymbol fileSymbol) {
        final TypeInferenceHelper helper = RubyPsiManager.getInstance(getProject()).getTypeInferenceHelper();
        helper.testAndSet(fileSymbol);
        return helper.inferBinaryExpressionType(this);
    }
}
