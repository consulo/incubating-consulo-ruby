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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.iterators;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.iterators.RBlockVariables;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 15.06.2006
 */
public class RBlockVariablesImpl extends RPsiElementBase implements RBlockVariables {
    public RBlockVariablesImpl(ASTNode astNode) {
        super(astNode);
    }

    public void accept(@NotNull PsiElementVisitor visitor){
        if (visitor instanceof RubyElementVisitor){
            ((RubyElementVisitor) visitor).visitRBlockVariables(this);
            return;
        }
        super.accept(visitor);
    }

    @NotNull
    public List<RIdentifier> getVariables() {
        RListOfExpressions list = getChildByType(RListOfExpressions.class, 0);
        if (list !=null){
            return list.getChildrenByType(RIdentifier.class);
        }
        return getChildrenByType(RIdentifier.class);
    }
}
