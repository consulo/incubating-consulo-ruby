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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RCaseStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RWhenCase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RElseBlock;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class RCaseStatementImpl extends RPsiElementBase implements RCaseStatement {
    public RCaseStatementImpl(ASTNode astNode) {
        super(astNode);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof RubyElementVisitor) {
            ((RubyElementVisitor)visitor).visitRCaseStatement(this);
            return;
        }
        super.accept(visitor);
    }

    public RPsiElement getExpression() {
        return RubyPsiUtil.getChildByType(this, RPsiElement.class, 0);
    }

    public List<RWhenCase> getCases() {
        return RubyPsiUtil.getChildrenByType(this, RWhenCase.class);
    }

    public RElseBlock getElseCase() {
        return RubyPsiUtil.getChildByType(this, RElseBlock.class, 0);
    }

    public RPsiElement getLoopBody() {
        throw new UnsupportedOperationException("getLoopBody is not implemented in org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.RCaseStatementImpl");
    }
}
