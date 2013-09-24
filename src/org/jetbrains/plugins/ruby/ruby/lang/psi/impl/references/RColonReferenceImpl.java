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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.references;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.Colon3Reference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RQualifiedReference;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RColonReference;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 07.06.2006
 */
public class RColonReferenceImpl extends RReferenceBase implements RColonReference {
    private static final TokenSet TS_COLONS = TokenSet.create(RubyTokenTypes.tCOLON2, RubyTokenTypes.tCOLON3);

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof RubyElementVisitor) {
            ((RubyElementVisitor)visitor).visitRColonReference(this);
            return;
        }
        super.accept(visitor);
    }

    public RColonReferenceImpl(ASTNode astNode) {
        super(astNode);
    }

    @NotNull
    public PsiElement getDelimiter() {
        PsiElement colon = getChildByFilter(TS_COLONS, 0);
        assert colon!=null;
        return colon;
    }

    @NotNull
    public Type getType() {
        return Type.COLON_REF;
    }

    @Nullable
    public RQualifiedReference getReference() {
        if (isColon3Reference()){
            final RPsiElement value = getValue();
            if (value != null) {
                return new Colon3Reference(getProject(), this, value);
            }
        }
        return super.getReference();
    }

    public boolean isColon3Reference() {
        return getChildByFilter(RubyTokenTypes.tCOLON3, 0)!=null;
    }
}
