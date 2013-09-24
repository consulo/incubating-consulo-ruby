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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.*;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 13.07.2006
 */
public class RBodyStatementImpl extends RPsiElementBase implements RBodyStatement {
    public RBodyStatementImpl(ASTNode astNode) {
        super(astNode);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof RubyElementVisitor) {
            ((RubyElementVisitor)visitor).visitRBodyStatement(this);
            return;
        }
        super.accept(visitor);
    }


    @Nullable
    public RCompoundStatement getBlock() {
        return RubyPsiUtil.getChildByType(this, RCompoundStatement.class, 0);
    }

    public List<RRescueBlock> getRescueBlocks() {
        return RubyPsiUtil.getChildrenByType(this, RRescueBlock.class);
    }

    @Nullable
    public REnsureBlock getEnsureBlock() {
        return RubyPsiUtil.getChildByType(this, REnsureBlock.class, 0);
    }

    @Nullable
    public RElseBlock getElseBlock() {
        return RubyPsiUtil.getChildByType(this, RElseBlock.class, 0);
    }
}
