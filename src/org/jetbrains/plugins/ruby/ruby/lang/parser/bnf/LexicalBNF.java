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

package org.jetbrains.plugins.ruby.ruby.lang.parser.bnf;

import com.intellij.psi.tree.TokenSet;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 05.09.2006
 */
public interface LexicalBNF {
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////// Lexical structures //////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    final TokenSet VARIABLES = TokenSet.create(
            RubyElementTypes.PSEUDO_CONSTANT,
            RubyElementTypes.INSTANCE_VARIABLE,
            RubyElementTypes.GLOBAL_VARIABLE,
            RubyElementTypes.CLASS_VARIABLE,
            RubyElementTypes.IDENTIFIER,
            RubyElementTypes.CONSTANT
    );

    final TokenSet OPERATION = TokenSet.create(
            RubyElementTypes.IDENTIFIER,
            RubyElementTypes.CONSTANT,
            RubyElementTypes.FID,
            RubyElementTypes.SUPER
    );

    @SuppressWarnings({"UnusedDeclaration"})
    final TokenSet REFERENCES = TokenSet.create(
            RubyElementTypes.COLON_REFERENCE,
            RubyElementTypes.ARRAY_REFERENCE,
            RubyElementTypes.DOT_REFERENCE
    );

    @SuppressWarnings({"UnusedDeclaration"})
    final TokenSet ASSIGNMENT_EXPRESSIONS = TokenSet.create(
            RubyElementTypes.ASSIGNMENT_EXPRESSION,
            RubyElementTypes.SELF_ASSIGNMENT_EXPRESSION,
            RubyElementTypes.MULTI_ASSIGNMENT_EXPRESSION
    );

    @SuppressWarnings({"UnusedDeclaration"})
    final TokenSet BINARY_EXPRESSIONS = TokenSet.create(
            RubyElementTypes.BOOL_MATCHING_EXPRESSION,
            RubyElementTypes.SHIFT_EXPRESSION,
            RubyElementTypes.BIT_EXPRESSION,
            RubyElementTypes.RANGE_EXPRESSION,
            RubyElementTypes.MATH_BINARY_EXPRESSION,
            RubyElementTypes.BOOL_BINARY_EXPRESSION
    );

    @SuppressWarnings({"UnusedDeclaration"})
    final TokenSet UNARY_EXPRESSIONS = TokenSet.create(
            RubyElementTypes.EXPRESSION_IN_PARENS,
            RubyElementTypes.NEGATIVE_EXPRESSION,
            RubyElementTypes.BOOL_NEGATIVE_EXPRESSION
    );

    final TokenSet NUMERICAL_LITERALS = TokenSet.create(
            RubyElementTypes.INTEGER,
            RubyElementTypes.FLOAT
    );

    final TokenSet STRING_LITERALS = TokenSet.create(
            RubyElementTypes.STRING,
            RubyElementTypes.NI_STRING,
            RubyElementTypes.X_STRING,
            RubyElementTypes.NI_X_STRING,
            RubyElementTypes.WORDS,
            RubyElementTypes.NI_WORDS,
            RubyElementTypes.REGEXP,
            RubyElementTypes.HEREDOC_ID,
            RubyElementTypes.HEREDOC_VALUE
    );

    @SuppressWarnings({"UnusedDeclaration"})
    final TokenSet LITERALS = TokenSet.orSet(
            NUMERICAL_LITERALS,
            STRING_LITERALS
    );

    @SuppressWarnings({"UnusedDeclaration"})
    final TokenSet CALL_EXPRESSIONS = TokenSet.create(
            RubyElementTypes.FUNCTION_CALL,
            RubyElementTypes.COMMAND_CALL,
            RubyElementTypes.BLOCK_CALL
    );

    final TokenSet VAR_LHS = VARIABLES;

    final TokenSet NOT_VAR_LHS = TokenSet.create(
                RubyElementTypes.DOT_REFERENCE,
                RubyElementTypes.COLON_REFERENCE,
                RubyElementTypes.ARRAY_REFERENCE,
                RubyElementTypes.BACKREF, 
                RubyElementTypes.NTHREF
            );

    // which lexical constructions able to be LHS
    final TokenSet LHS = TokenSet.orSet(
            VAR_LHS,
            NOT_VAR_LHS
    );

    // command_object call_args. Which lexical construction able to be COMMAND_OBJECT
    final TokenSet COMMAND_OBJECTS = TokenSet.orSet(
            OPERATION,
            TokenSet.create(
                    RubyElementTypes.COLON_REFERENCE,
                    RubyElementTypes.DOT_REFERENCE
            )
    );

    // iterator_object brace_block. Which lexical construction able to be iterator_command
    final TokenSet ITERATOR_OBJECTS = TokenSet.orSet(
            COMMAND_OBJECTS,
            TokenSet.create(
                    RubyElementTypes.FUNCTION_CALL,
                    RubyElementTypes.COMMAND_CALL,
                    RubyElementTypes.BLOCK_CALL)
            );


}
