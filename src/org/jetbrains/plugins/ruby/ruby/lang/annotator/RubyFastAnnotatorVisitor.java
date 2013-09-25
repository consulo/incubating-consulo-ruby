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

package org.jetbrains.plugins.ruby.ruby.lang.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RAliasStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RArgument;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.RLBeginStatementImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.names.RNameNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions.RAssignmentExpressionNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions.RSelfAssingmentExpressionNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.global.RNthRefImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RColonReference;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RClassVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RInstanceVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.global.RGlobalVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 6, 2007
 */
public class RubyFastAnnotatorVisitor extends RubyElementVisitor {
    private AnnotationHolder myHolder;

    public RubyFastAnnotatorVisitor(@NotNull final AnnotationHolder holder){
        myHolder = holder;
    }

    @Override
	public void visitRAliasStatement(RAliasStatement rAliasStatement) {
        if (rAliasStatement.getPsiNewName() instanceof RNthRefImpl) {
            myHolder.createErrorAnnotation(rAliasStatement, RBundle.message("annotation.error.cannot.make.alias.for.nth.variable"));
        }
    }

    // parse.y line 486
    @Override
	public void visitRLBeginStatement(RLBeginStatementImpl rlBeginStatement) {
        if (rlBeginStatement.getParentContainer() instanceof RMethod) {
            myHolder.createErrorAnnotation(rlBeginStatement, RBundle.message("annotation.error.begin.in.method"));
        }
    }

    // parse.y line 882
    @Override
	public void visitRIdentifier(RIdentifier rIdentifier) {
        if (RNameNavigator.getRName(rIdentifier) != null) {
            myHolder.createErrorAnnotation(rIdentifier, RBundle.message("annotation.error.class.module.name.must.be.constant"));
        }
    }

    @Override
	public void visitRConstant(RConstant rConstant) {
        final PsiElement parent = rConstant.getParent();
        if (parent instanceof RColonReference &&
                ((RColonReference) parent).getValue() == rConstant){

            // parse.y line 825, 831, 864, 870
            if (RAssignmentExpressionNavigator.getAssignmentByLeftPart(parent)!= null){
                myHolder.createErrorAnnotation(RAssignmentExpressionNavigator.getAssignmentByLeftPart(parent),
                        RBundle.message("annotation.error.dynamic.constant.assignment"));
            }
            // parse.y line 1060, 1065
            if (RSelfAssingmentExpressionNavigator.getSelfAssignmentByLeftPart(parent)!= null){
                myHolder.createErrorAnnotation(RSelfAssingmentExpressionNavigator.getSelfAssignmentByLeftPart(parent),
                        RBundle.message("annotation.error.constant.reassignment"));
            }
        }
        if (RSelfAssingmentExpressionNavigator.getSelfAssignmentByLeftPart(rConstant)!= null){
            myHolder.createErrorAnnotation(RSelfAssingmentExpressionNavigator.getSelfAssignmentByLeftPart(rConstant),
                    RBundle.message("annotation.error.constant.reassignment"));
        }
    }

    // parse.y line 1624
    @Override
	public void visitRClass(RClass rClass) {
        if (rClass.getParentContainer() instanceof RMethod){
            myHolder.createErrorAnnotation(rClass.getFirstChild(), RBundle.message("annotation.error.class.in.method"));
        }
    }

    // parse.y line 1662
    @Override
	public void visitRModule(RModule rModule) {
        if (rModule.getParentContainer() instanceof RMethod){
            myHolder.createErrorAnnotation(rModule.getFirstChild(), RBundle.message("annotation.error.module.in.method"));
        }
    }

    // parse.y line 2281, 2285, 2289, 2293
    @Override
	public void visitRParameter(RArgument rArgument) {
        final PsiElement firstChild = rArgument.getFirstChild();
        if (firstChild instanceof RConstant){
            myHolder.createErrorAnnotation(rArgument, RBundle.message("annotation.error.formal.arg.cannot.be.constant"));
        }
        if (firstChild instanceof RInstanceVariable){
            myHolder.createErrorAnnotation(rArgument, RBundle.message("annotation.error.formal.arg.cannot.be.inst.var"));
        }
        if (firstChild instanceof RGlobalVariable){
            myHolder.createErrorAnnotation(rArgument, RBundle.message("annotation.error.formal.arg.cannot.be.global.var"));
        }
        if (firstChild instanceof RClassVariable){
            myHolder.createErrorAnnotation(rArgument, RBundle.message("annotation.error.formal.arg.cannot.be.class.var"));
        }
    }

// todo: parse.y line 2371
/*

 public void visitRBodyStatement(final RBodyStatement rBodyStatement) {
        final RElseBlock elseBlock = rBodyStatement.getElseBlock();
        if (elseBlock !=null && rBodyStatement.getRescueBlocks().isEmpty()){
            myHolder.createWarningAnnotation(elseBlock, "else without rescue is useless");
        }
    }

    public void visitRStringLiteral(final RStringLiteral rStringLiteral){
// String to symbol intention
        if (StringToSymbolIntention.canIntent(rStringLiteral)){
            Annotation annotation = myHolder.createInformationAnnotation(rStringLiteral, null);
            annotation.registerFix(new StringToSymbolIntention(rStringLiteral));
        }
    }

    public void visitRDStringLiteral(final RDStringLiteral rDStringLiteral){
// Sting to symbol intention
        if (StringToSymbolIntention.canIntent(rDStringLiteral)){
            Annotation annotation = myHolder.createInformationAnnotation(rDStringLiteral, null);
            annotation.registerFix(new StringToSymbolIntention(rDStringLiteral));
        }
    }
*/
}
