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

package org.jetbrains.plugins.ruby.ruby.inspections.ducktype;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.LastSymbolStorage;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Message;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RMethodTypeUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.inspections.RubyInspectionVisitor;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.assoc.RAssoc;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.methodCall.RCallNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.references.RReferenceNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RArrayToArguments;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 24, 2007
 */
public class RubyDuckTypeVisitor extends RubyInspectionVisitor {
    private static final Logger LOG = Logger.getInstance(RubyDuckTypeVisitor.class.getName());

    public RubyDuckTypeVisitor(@NotNull final ProblemsHolder holder) {
        super(holder);
    }

    public void visitRCall(@NotNull final RCall call) {
        // It`s often operation
        ProgressManager.getInstance().checkCanceled();

        final PsiElement command = call.getPsiCommand();
        final List<PsiElement> elements = ResolveUtil.multiResolve(command);
// no inspection for more than 1 posibility
        if (elements.size()!=1){
            return;
        }

        final FileSymbol fileSymbol = LastSymbolStorage.getInstance(call.getProject()).getSymbol();
        matchCallAndMethod(elements.get(0), call, call.getArguments(), fileSymbol);
    }

    public void visitRIdentifier(final RIdentifier rIdentifier) {
        // It`s often operation
        ProgressManager.getInstance().checkCanceled();

        if (RCallNavigator.getByCommand(rIdentifier)!=null ||
                RReferenceNavigator.getReferenceByLeftPart(rIdentifier)!=null ||
                RReferenceNavigator.getReferenceByRightPart(rIdentifier)!=null){
            return;
        }

        if (rIdentifier.isParameter() || rIdentifier.isLocalVariable()){
            return;
        }

        final List<PsiElement> elements = ResolveUtil.multiResolve(rIdentifier);
// no inspection for more than 1 posibility
        if (elements.size()!=1){
            return;
        }

        final FileSymbol fileSymbol = LastSymbolStorage.getInstance(rIdentifier.getProject()).getSymbol();
        matchCallAndMethod(elements.get(0), rIdentifier, Collections.<RPsiElement>emptyList(), fileSymbol);
    }

    public void matchCallAndMethod(@NotNull final PsiElement element,
                                   final RPsiElement call,
                                   @NotNull List<RPsiElement> callArgs,
                                   @Nullable final FileSymbol fileSymbol){
        // TODO[oleg] PsiMethod support
        if (!(element instanceof RMethod)){
            return;
        }
        final RMethod method = (RMethod) element;

        final int number = callArgs.size();
// We process arrayToArguments and assoc to Hash notation!
        int arrayToArgIndex = -1;
        int hashIndex = -1;
        for (int i = 0; i < callArgs.size(); i++) {
            final RPsiElement arg = callArgs.get(i);
            if (arg instanceof RArrayToArguments) {
                arrayToArgIndex = i;
                break;
            }
            if (arg instanceof RAssoc){
                hashIndex = i;
                break;
            }
        }
        final int min = RMethodTypeUtil.getMinNumberOfArguments(method);
        final int max = RMethodTypeUtil.getMaxNumberOfArguments(method);

        LOG.assertTrue(max == -1 || min <= max, "Wrong number of arguments found!");

        if (number < min && arrayToArgIndex == -1 ||
            number > max && max != -1) {
          if (min == max) {
            registerProblem(call, RBundle.message("inspection.duck.typing.number.of.arguments.error", min, number));
          }
          else if (number < min) {
            registerProblem(call, RBundle.message("inspection.duck.typing.number.of.arguments.at.least.error", min, number));
          }
          else if (number > max && (hashIndex == -1 || hashIndex >= max)) {
            registerProblem(call, RBundle.message("inspection.duck.typing.number.of.arguments.max.error", max, number));
          }
        }

        final List<ExpectedMessages> requiredTypes = RubyDuckTypeUtil.getMethodRequiredTypes((RMethod) element);

        int numberToInspect = Math.min(requiredTypes.size(), callArgs.size());
        if (arrayToArgIndex!=-1){
            numberToInspect = Math.min(arrayToArgIndex, numberToInspect);
        }
        if (hashIndex!=-1){
            numberToInspect = Math.min(hashIndex, numberToInspect);
        }

// iterating over the arguments
        for (int i = 0; i < numberToInspect; i++) {
            final RPsiElement callArg = callArgs.get(i);
            final ExpectedMessages requiredType = requiredTypes.get(i);
            final RType callArgType = callArg instanceof RExpression ? ((RExpression) callArg).getType(fileSymbol) : RType.NOT_TYPED;
            if (callArgType.isTyped()){
                for (Message message : requiredType.getExpectedMessages()) {
                    if (!callArgType.matchesMessage(message)){
                        final String errorMessage =
                                RBundle.message("inspection.duck.typing.cannot.find.method", callArg.getText(), message.getName(), message.getArgumentsNumber());
                        registerProblem(callArg, errorMessage);
                    }
                }
            }
        }
    }
}
