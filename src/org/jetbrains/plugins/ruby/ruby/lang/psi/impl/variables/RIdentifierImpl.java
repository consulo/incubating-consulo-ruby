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
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.psi.RIdentifierReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.Scope;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeVariable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.arguments.RArgumentNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.iterators.RBlockVariableNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.references.RReferenceNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 05.09.2006
 */
public class RIdentifierImpl extends RNamedElementBase implements RIdentifier {

    public RIdentifierImpl(ASTNode astNode) {
        super(astNode);
    }

    public void accept(@NotNull PsiElementVisitor visitor){
        if (visitor instanceof RubyElementVisitor){
            ((RubyElementVisitor) visitor).visitRIdentifier(this);
            return;
        }
        super.accept(visitor);
    }


    protected PsiReference createReference() {
        return new RIdentifierReference(this);
    }

    @Nullable
    public ScopeVariable getScopeVariable() {
        if (isParameter() || isLocalVariable()){
            final Scope scope = ScopeUtil.findScopeByIdentifier(this);
            if (scope == null) {
                return null;
            }
            return scope.getVariableByName(getName());
        }                                                                                                         
        return null;
    }


    public boolean isLocalVariable() {
        if (isParameter()){
            return false;
        }

// Something::identifier or something.identifier
        if (RReferenceNavigator.getReferenceByRightPart(this)!=null){
            return false;
        }

// identifier call_args
        final PsiElement parent = getParent();
        //noinspection SimplifiableIfStatement
        if (parent instanceof RCall && ((RCall) parent).getPsiCommand() == this){
            return false;
        }

        return ScopeUtil.findScopeByIdentifier(this)!=null;
    }

    protected void checkName(@NonNls @NotNull final String newName) throws IncorrectOperationException {
        if (Character.isUpperCase(newName.charAt(0)) || !TextUtil.isCID(newName) && !TextUtil.isFID(newName)){
            throw new IncorrectOperationException(RBundle.message("rename.incorrect.name"));
        }
    }

    public boolean isParameter() {
        return isMethodParameter() || isBlockParameter();
    }

    public boolean isMethodParameter() {
        return RArgumentNavigator.getByRIdentifier(this)!=null;
    }

    public boolean isBlockParameter() {
        return RBlockVariableNavigator.getByIdentifier(this)!=null;
    }

    @Nullable
    protected String getPrefix() {
        return null;
    }

    @NotNull
    public RType getType(@Nullable final FileSymbol fileSymbol) {
        final ScopeVariable scopeVariable = getScopeVariable();
        if (scopeVariable!=null){
            return scopeVariable.getType(fileSymbol, this);
        }
        return super.getType(fileSymbol);
    }

    @NotNull
    public SearchScope getUseScope() {
        if (isParameter() || isLocalVariable()){
            return new LocalSearchScope(getContainingFile());
        }
        return super.getUseScope();
    }
}
 