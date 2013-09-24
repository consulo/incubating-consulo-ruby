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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.blocks;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RElseBlock;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 16.06.2006
 */
public class RElseBlockImpl extends RPsiElementBase implements RElseBlock {
    public RElseBlockImpl(ASTNode astNode) {
        super(astNode);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof RubyElementVisitor) {
            ((RubyElementVisitor)visitor).visitRElseBlock(this);
            return;
        }
        super.accept(visitor);
    }

    @NotNull
    public RCompoundStatement getBody() {
        return RubyPsiUtil.getChildByType(this, RCompoundStatement.class, 0);
    }
}