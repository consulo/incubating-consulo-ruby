/*
 * Copyright 2000-2007 JetBrains s.r.o.
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

package org.jetbrains.plugins.ruby.ruby.codeInsight.types;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.containers.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.RubyPsiManager;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.Access;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.AssignAccess;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.Instruction;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.RControlFlowOwner;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RArgument;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RArgumentList;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.dataFlow.DFAEngine;
import org.jetbrains.plugins.ruby.ruby.lang.psi.dataFlow.types.TypeDfaInstance;
import org.jetbrains.plugins.ruby.ruby.lang.psi.dataFlow.types.TypesSemilattice;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RBinaryExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RUnaryExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.methodCall.RCallBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.references.RReferenceBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 21, 2008
 */
public class TypeInferenceHelper {
    private Ref<TypeInferenceContext> myTypeContext = new Ref<TypeInferenceContext>();

    public RType inferLocalVariableType(@NotNull final RControlFlowOwner owner,
                                        @NotNull final RIdentifier usage) {
        ProgressManager.getInstance().checkCanceled();

        final TypeInferenceContext context = getContext();
        if (context == null){
            return RType.NOT_TYPED;
        }
        Map<RIdentifier, RType> map = context.localVariablesTypesCache.get(owner);
        if (map == null){
            map = new HashMap<RIdentifier, RType>();
            context.localVariablesTypesCache.put(owner, map);
            inferLocalVariablesTypes(context, owner, map);
        }
        final RType type = map.get(usage);
        return type!=null ? type : RType.NOT_TYPED;
    }

    public void inferLocalVariablesTypes(@NotNull final TypeInferenceContext context,
                                         @NotNull final RControlFlowOwner owner,
                                         final Map<RIdentifier, RType> map) {
        context.methodsBeingInferred.add(owner);
        final Instruction[] flow = owner.getControlFlow();
        final TypeDfaInstance dfaInstance = new TypeDfaInstance(context.fileSymbol, map);
        final TypesSemilattice semilattice = new TypesSemilattice();
        final DFAEngine<Map<String, RType>> engine = new DFAEngine<Map<String, RType>>(flow, dfaInstance, semilattice);
        engine.performDFA();
        context.methodsBeingInferred.remove(owner);
    }

    public RType inferCallType(final RCallBase call) {
        TypeInferenceContext context = getContext();
        if (context == null) {
            return RType.NOT_TYPED;
        }
        final FileSymbol fileSymbol = context.fileSymbol;
        final PsiElement command = call.getPsiCommand();

// Constructors handling
        if (command instanceof RReference) {
            final RReferenceBase ref = (RReferenceBase) command;
            if (ref.isConstructorLike()){
                return ref.getType(fileSymbol);
            }
        }

// Common call handling
        return inferTypeOfReference(command.getReference(), call.getArguments());
    }


    public RType inferBinaryExpressionType(final RBinaryExpression expression) {
        return inferTypeOfReference(expression.getReference(), Collections.singletonList(expression.getRightOperand()));
    }

    public RType inferUnaryExpressionType(final RUnaryExpression expression) {
        return inferTypeOfReference(expression.getReference(), Collections.singletonList(expression.getElement()));
    }

    public RType inferTypeOfReference(@Nullable final PsiReference ref,
                                      final List<RPsiElement> callArgs) {
        if (ref == null) {
            return RType.NOT_TYPED;
        }
        TypeInferenceContext context = getContext();
        if (context == null) {
            return RType.NOT_TYPED;
        }
        final FileSymbol fileSymbol = context.fileSymbol;

        final Symbol symbol = ResolveUtil.resolveToSymbol(fileSymbol, ref);
        if (symbol == null){
            return RType.NOT_TYPED;
        }
        return inferCallTypeBySymbol(symbol, callArgs);
    }

    public RType inferCallTypeBySymbol(@NotNull final Symbol symbol,
                                       final List<RPsiElement> callArgs) {
        ProgressManager.getInstance().checkCanceled();

        final TypeInferenceContext context = RubyPsiManager.getInstance(symbol.getProject()).getTypeInferenceHelper().getContext();
        if (context == null) {
            return RType.NOT_TYPED;
        }
        final FileSymbol fileSymbol = context.fileSymbol;
// Java method handling
        if (symbol.getType() == Type.JAVA_METHOD){
            return RTypeUtil.createTypeBySymbol(fileSymbol, symbol, Context.ALL, true);
        }
// Common ruby method call handling
        final RVirtualElement prototype = symbol.getLastVirtualPrototype(fileSymbol);
        if (prototype==null){
            return RType.NOT_TYPED;
        }
        final RPsiElement element = RVirtualPsiUtil.findPsiByVirtualElement(prototype, symbol.getProject());
        if (!(element instanceof RMethod)) {
            return RType.NOT_TYPED;
        }

// Well, here we have all the types of arguments and can infer type of method
        return inferTypeOfMethodCall((RMethod) element, callArgs, context);
    }

    private RType inferTypeOfMethodCall(@NotNull final RMethod method,
                                        final List<RPsiElement> callArgs,
                                        @NotNull final TypeInferenceContext context) {
        final FileSymbol fileSymbol = context.fileSymbol;
        final RType commentType = TypeCommentsHelper.tryToExtractTypeFromComment(method, fileSymbol);
        if (commentType!=null){
            return commentType;
        }

        // Check for infinite loop
        if (context.methodsBeingInferred.contains(method) || context.depth > TypeInferenceContext.MAX_DEPTH){
            return RType.NOT_TYPED;
        }

        try{
            context.methodsBeingInferred.add(method);
            context.depth++;

            // we remove method variables caches of method
            context.localVariablesTypesCache.remove(method);
            final HashMap<RIdentifier, RType> localVariablesTypes = new HashMap<RIdentifier, RType>();
            context.localVariablesTypesCache.put(method, localVariablesTypes);

            // Here we set types of arguments of call
            final RArgumentList argumentList = method.getArgumentList();
            if (argumentList!=null) {
                // all other types are handled inside TypeDfaInstance.fun in MethodParameterAccess case
                final List<RArgument> arguments = argumentList.getArguments();
                for (int i = 0; i < arguments.size(); i++) {
                    final RArgument argument = arguments.get(i);
                    final RIdentifier id = argument.getIdentifier();
                    final ArgumentInfo.Type argType = argument.getType();
                    if (argType == ArgumentInfo.Type.SIMPLE || argType == ArgumentInfo.Type.PREDEFINED) {
                        final RPsiElement arg = i<callArgs.size() ? callArgs.get(i) : null;
                        if (arg instanceof RExpression) {
                            localVariablesTypes.put(id, ((RExpression) arg).getType(fileSymbol));
                        }
                    }
                }
            }
            final Instruction[] flow = method.getControlFlow();
            final TypeDfaInstance dfaInstance = new TypeDfaInstance(fileSymbol, localVariablesTypes);
            final TypesSemilattice semilattice = new TypesSemilattice();
            final DFAEngine<Map<String, RType>> engine = new DFAEngine<Map<String, RType>>(flow, dfaInstance, semilattice);
            engine.performDFA();


            // here we iterate over last instructions to get joint type
            final Instruction lastInstruction = flow[flow.length - 1];
            RType result = RType.NOT_TYPED;
            for (Instruction i : lastInstruction.allPred()) {
                // Here we should take types of statements!!!
                final PsiElement statement = RubyPsiUtil.getStatement(i.getElement());
                if (statement instanceof RExpression) {
                    result = RTypeUtil.joinOr(result, ((RExpression) statement).getType(fileSymbol));
                }
            }

            return result;
        } finally{
            context.methodsBeingInferred.remove(method);
            // we remove method variables caches of method
            context.localVariablesTypesCache.remove(method);
            context.depth--;
        }
    }

    @Nullable
    public TypeInferenceContext getContext() {
        return myTypeContext.get();
    }

    public void testAndSet(@Nullable final FileSymbol fileSymbol) {
        final TypeInferenceContext context = getContext();
        if (context == null){
            myTypeContext.set(new TypeInferenceContext(fileSymbol));
        }
    }

    public void clearContext() {
        myTypeContext.set(null);
    }

    public RType inferUsageType(final Access access) {
        final TypeInferenceContext context = getContext();
        if (context == null) {
          return RType.NOT_TYPED;
        }
        if (access instanceof AssignAccess) {
          final RPsiElement value = ((AssignAccess) access).getValue();
          if (value instanceof RExpression) {
            final RExpression expression = (RExpression)value;
            // Prevent stack overflow
            if (context.expressionsBeingInferred.contains(expression)){
              return RType.NOT_TYPED;
            }
            try {
              context.expressionsBeingInferred.add(expression);
              return expression.getType(context.fileSymbol);
            }
            finally {
              context.expressionsBeingInferred.remove(expression);
            }
          }
        }
        return RType.NOT_TYPED;
    }
}
