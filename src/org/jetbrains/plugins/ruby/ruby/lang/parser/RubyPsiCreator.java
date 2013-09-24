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

package org.jetbrains.plugins.ruby.ruby.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.assoc.RAssocImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.assoc.RAssocListImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.RArrayImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.RFloatConstantImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.RIntegerConstantImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.RSymbolImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.RExpressionSubstitutionImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.baseString.RDStringLiteralImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.baseString.RStringLiteralImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.baseString.RStringsImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.heredocs.RHeredocIdImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.heredocs.RHeredocValueImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.regexp.RRegexpLiteralImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.words.RDWordsImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.words.RWordsImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.xString.RDXStringLiteralImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.xString.RXStringLiteralImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.*;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.blocks.*;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.classes.RClassImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.classes.RClassObjectImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.classes.RObjectClassImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.RCommandArgumentListImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.RFunctionArgumentListImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.RMethodImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.RSingletonMethodImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.arguments.RArgumentImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.arguments.RArrayArgumentImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.arguments.RBlockArgumentImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.arguments.RPredefinedArgumentImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.modifierStatements.*;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.modules.RModuleImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.names.RClassNameImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.names.RMethodNameImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.names.RModuleNameImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.names.RSuperClassImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions.*;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.iterators.RBlockCallImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.iterators.RBlockVariablesImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.iterators.RBraceCodeBlockImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.iterators.RDoCodeBlockImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.methodCall.RArrayToArgumentsImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.methodCall.RBlockToArgumentImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.methodCall.RCommandCallImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.methodCall.RFunctionCallImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.references.RColonReferenceImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.references.RDotReferenceImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.*;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.fields.RClassVariableImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.fields.RInstanceVariableImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.global.RBackRefImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.global.RGlobalVariableImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.global.RNthRefImpl;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.05.2006
 */
class RubyPsiCreator implements RubyElementTypes{

    @NotNull
    public static PsiElement create(ASTNode node) {
        IElementType type = node.getElementType();


// numeric literals
        if (type == INTEGER)
            return new RIntegerConstantImpl(node);

        if (type == FLOAT)
            return new RFloatConstantImpl(node);

// calls
        if (type == FUNCTION_CALL)
            return new RFunctionCallImpl(node);

        if (type == COMMAND_CALL)
            return new RCommandCallImpl(node);

        if (type == COMMAND_ARGUMENT_LIST)
            return new RCommandArgumentListImpl(node);

        if (type == FUNCTION_ARGUMENT_LIST)
            return new RFunctionArgumentListImpl(node);

        if (type == ARRAY_TO_ARGS)
            return new RArrayToArgumentsImpl(node);

        if (type == BLOCK_TO_ARG)
            return new RBlockToArgumentImpl(node);

        if (type == BLOCK_CALL)
            return new RBlockCallImpl(node);

        if (type == BLOCK_VARIABLES)
            return new RBlockVariablesImpl(node);

        if (type == SYMBOL)
            return new RSymbolImpl(node);

        if (type == SHIFT_EXPRESSION)
            return new RShiftExpressionImpl(node);

        if (type == BIT_EXPRESSION)
            return new RBitExpressionImpl(node);



        if (type == MATH_BINARY_EXPRESSION)
            return new RMathBinExpressionImpl(node);

        if (type == NEGATIVE_EXPRESSION)
            return new RNegativeExpressionImpl(node);

        if (type == RANGE_EXPRESSION)
             return new RRangeExpressionImpl(node);

// assignments
        if (type == ASSIGNMENT_EXPRESSION)
             return new RAssignmentExpressionImpl(node);

        if (type == SELF_ASSIGNMENT_EXPRESSION)
             return new RSelfAssignmentExpressionImpl(node);

        if (type == MULTI_ASSIGNMENT_EXPRESSION)
             return new RMultiAssignmentExpressionImpl(node);

        if (type == EXPRESSION_IN_PARENS)
            return new RExpressionInParensImpl(node);

        if (type == UNARY_EXPRESSION)
            return new RUnaryExpressionBase(node);


// boolean
        if (type == BOOL_BINARY_EXPRESSION)
            return new RBoolBinExpressionImpl(node);

        if (type == BOOL_NEGATIVE_EXPRESSION)
            return new RBoolNegExpressionImpl(node);

        if (type == BOOL_MATCHING_EXPRESSION)
            return new RBoolMatchingImpl(node);

// variables
        if (type == IDENTIFIER)
            return new RIdentifierImpl(node);

        if (type == CONSTANT)
            return new RConstantImpl(node);

        if (type == INSTANCE_VARIABLE)
            return new RInstanceVariableImpl(node);

        if (type == CLASS_VARIABLE)
            return new RClassVariableImpl(node);

        if (type == GLOBAL_VARIABLE)
            return new RGlobalVariableImpl(node);

        if (type == PSEUDO_CONSTANT || type == SUPER)
            return new RPseudoConstantImpl(node);

        if (type == OP)
            return new ROperationImpl(node);

        if (type == FID)
            return new RFidImpl(node);

        if (type == BACKREF)
            return new RBackRefImpl(node);

        if (type == NTHREF)
            return new RNthRefImpl(node);

// references
        if (type == ARRAY_REFERENCE)
            return new RArrayIndexingImpl(node);

        if (type == DOT_REFERENCE)
            return new RDotReferenceImpl(node);

        if (type == COLON_REFERENCE)
            return new RColonReferenceImpl(node);

// heredocs
        if (type == HEREDOC_ID)
            return new RHeredocIdImpl(node);

        if (type == HEREDOC_VALUE)
            return new RHeredocValueImpl(node);

// strings
        if (type == STRING)
            return new RDStringLiteralImpl(node);

        if (type == NI_STRING)
            return new RStringLiteralImpl(node);

        if (type == STRINGS)
            return new RStringsImpl(node);

        if (type == EXPR_SUBTITUTION)
            return new RExpressionSubstitutionImpl(node);



// x strings
        if (type == X_STRING)
            return new RDXStringLiteralImpl(node);

        if (type == NI_X_STRING)
            return new RXStringLiteralImpl(node);

 // words
        if (type == WORDS)
               return new RDWordsImpl(node);

        if (type == NI_WORDS)
               return new RWordsImpl(node);

// array
        if (type == ARRAY)
            return new RArrayImpl(node);

// assocs
        if (type == ASSOC_LIST)
            return new RAssocListImpl(node);

        if (type == ASSOC)
            return new RAssocImpl(node);

// regular expressions
        if (type == REGEXP)
               return new RRegexpLiteralImpl(node);

        if (type == TERNARY_EXPRESSION)
            return new RTernaryExpressionImpl(node);




// control structures
        if (type == IF_STATEMENT)
            return new RIfStatementImpl(node);

        if (type == CONDITION)
            return new RConditionImpl(node);

        if (type == IF_MOD_STATEMENT)
            return new RIfModStatementImpl(node);

        if (type == UNLESS_STATEMENT)
            return new RUnlessStatementImpl(node);

        if (type == UNLESS_MOD_STATEMENT)
            return new RUnlessModStatementImpl(node);

        if (type == CASE_STATEMENT)
            return new RCaseStatementImpl(node);

        if (type == WHEN_CASE)
            return new RWhenCaseImpl(node);

        if (type == WHILE_STATEMENT)
            return new RWhileStatementImpl(node);

        if (type == WHILE_MOD_STATEMENT)
            return new RWhileModStatementImpl(node);

        if (type == UNTIL_STATEMENT)
            return new RUntilStatementImpl(node);

        if (type == UNTIL_MOD_STATEMENT)
            return new RUntilModStatementImpl(node);

        if (type == FOR_STATEMENT)
            return new RForStatementImpl(node);

        if (type == YIELD_STATEMENT)
            return new RYieldStatementImpl(node);

        if (type == BEGIN_END_BLOCK_STATEMENT)
            return new RBeginEndBlockStatementImpl(node);

        if (type == RETRY_STATEMENT)
            return new RRetryStatementImpl(node);

        if (type == RETURN_STATEMENT)
            return new RReturnStatementImpl(node);

        if (type == BREAK_STATEMENT)
            return new RBreakStatementImpl(node);

        if (type == NEXT_STATEMENT)
            return new RNextStatementImpl(node);

        if (type == REDO_STATEMENT)
            return new RRedoStatementImpl(node);

        if (type == LBEGIN_STATEMENT)
            return new RLBeginStatementImpl(node);

        if (type == LEND_STATEMENT)
            return new RLEndStatementImpl(node);

        if (type == ALIAS_STATEMENT)
            return new RAliasStatementImpl(node);

        if (type == UNDEF_STATEMENT)
            return new RUndefStatementImpl(node);

        if (type == DEFINED_STATEMENT)
            return new RDefinedStatementImpl(node);

// classes modules etc
        if (type == MODULE)
            return new RModuleImpl(node);

        if (type == MODULE_NAME)
            return new RModuleNameImpl(node);

        if (type == FNAME)
            return new RFNameImpl(node);

        if (type == CLASS)
            return new RClassImpl(node);

        if (type == CLASS_NAME)
            return new RClassNameImpl(node);

        if (type == SUPER_CLASS)
            return new RSuperClassImpl(node);

        if (type == OBJECT_CLASS)
            return new RObjectClassImpl(node);

        if (type == CLASS_OBJECT)
            return new RClassObjectImpl(node);


        if (type == METHOD)
            return new RMethodImpl(node);

        if (type == ARGUMENT)
            return new RArgumentImpl(node);

        if (type == ARRAY_ARGUMENT)
            return new RArrayArgumentImpl(node);

        if (type == BLOCK_ARGUMENT)
            return new RBlockArgumentImpl(node);

        if (type == METHOD_NAME)
            return new RMethodNameImpl(node);

        if (type == SINGLETON_METHOD)
            return new RSingletonMethodImpl(node);

        if (type == PREDEFINED_ARGUMENT)
            return new RPredefinedArgumentImpl(node);

// blocks
        if (type == BODY_STATEMENT)
            return new RBodyStatementImpl(node);

        if (type == LIST_OF_EXPRESSIONS)
            return new RListOfExpressionsImpl(node);

        if (type == COMPOUND_STATEMENT)
            return new RCompoundStatementImpl(node);

        if (type == ELSE_BLOCK)
            return new RElseBlockImpl(node);

        if (type == ELSIF_BLOCK)
            return new RElsifBlockImpl(node);

        if (type == RESCUE_BLOCK)
            return new RRescueBlockImpl(node);

        if (type == RESCUE_MOD_STATEMENT)
            return new RRescueModStatementImpl(node);

        if (type == ENSURE_BLOCK)
            return new REnsureBlockImpl(node);

        if (type == DO_CODE_BLOCK)
            return new RDoCodeBlockImpl(node);

        if (type == BRACE_CODE_BLOCK)
            return new RBraceCodeBlockImpl(node);

        return new RPsiElementBase(node);
    }
}
