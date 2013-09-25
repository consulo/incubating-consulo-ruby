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

package org.jetbrains.plugins.ruby.ruby.refactoring.introduceVariable;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.LastSymbolStorage;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RPseudoConstant;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RClassVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RInstanceVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author: Oleg.Shpynov
 * @date: 25.10.2007
 */
public class NamesSuggestor {
    public static String[] getSuggestedNames(@NotNull final PsiElement element){
        final NameSuggestingVisitor suggestingVisitor = new NameSuggestingVisitor();
        element.accept(suggestingVisitor);
        return suggestingVisitor.getNames();
    }

    private static class NameSuggestingVisitor extends RubyElementVisitor{
        private List<String> names = new ArrayList<String>();

        @NotNull
        public String[] getNames() {
            return names.toArray(new String[names.size()]);
        }

        private void generateNamesBy(@NotNull final String name){
            NameSuggestorUtil.addNames(names, name);
        }

        private void generateNamesByType(@NotNull final String typeName){
            NameSuggestorUtil.addNamesByType(names, typeName);
        }

        @Override
		public void visitElement(PsiElement element) {
            if (element instanceof RExpression){
                final FileSymbol fileSymbol = LastSymbolStorage.getInstance(element.getProject()).getSymbol();
                final RType RType = ((RExpression) element).getType(fileSymbol);
                final String name = RType.getName();
                if (name!=null){
                    generateNamesByType(name);
                }
            }
        }
        @Override
		public void visitRListOfExpressions(RListOfExpressions rListOfExpressions) {
            generateNamesBy("list");
            generateNamesBy("items");
            super.visitRListOfExpressions(rListOfExpressions);
        }

        @Override
		public void visitRConstant(RConstant rConstant) {
            generateNamesBy(rConstant.getName());
            super.visitRConstant(rConstant);
        }

        @Override
		public void visitRIdentifier(RIdentifier rIdentifier) {
            //noinspection ConstantConditions
            generateNamesBy(rIdentifier.getName());
            super.visitRIdentifier(rIdentifier);
        }

        @Override
		public void visitRInstanceVariable(RInstanceVariable rInstanceVariable) {
            generateNamesBy(rInstanceVariable.getName());
            super.visitRInstanceVariable(rInstanceVariable);
        }

        @Override
		public void visitRClassVariable(RClassVariable rClassVariable) {
            generateNamesBy(rClassVariable.getName());
            super.visitRClassVariable(rClassVariable);
        }

        @Override
		public void visitRCall(RCall rCall) {
            generateNamesBy(rCall.getCommand());
            super.visitRCall(rCall);
        }

        @Override
		public void visitRReference(RReference rReference) {
            final RPsiElement value = rReference.getValue();
            if (value!=null){
                generateNamesBy(value.getText());
            }
            super.visitRReference(rReference);
        }

        @Override
		public void visitRPseudoConstant(RPseudoConstant rPseudoConstant) {
            if (RubyTokenTypes.kSELF.toString().equals(rPseudoConstant.getText())){
                names.add("instance");
            }
            super.visitRPseudoConstant(rPseudoConstant);
        }
    }
}
