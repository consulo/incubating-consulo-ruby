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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.basicTypes;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 27.06.2006
 */
public class VARIABLE
{
/*
	variable	: tIDENTIFIER
            | tIVAR
            | tGVAR
            | tCONSTANT
            | tCVAR
            | kNIL
            | kSELF
            | kTRUE
            | kFALSE
            | k__FILE__
            | k__LINE__
            ;
*/

	@NotNull
	public static IElementType parse(final RBuilder builder)
	{
		if(builder.compare(BNF.tVARIABLES))
		{
			if(builder.compare(RubyTokenTypes.kSUPER))
			{
				builder.parseSingleToken(RubyTokenTypes.kSUPER, RubyElementTypes.PSEUDO_CONSTANT);
				return RubyElementTypes.SUPER;
			}
			if(builder.compare(BNF.kPSEUDO_CONSTANTS))
			{
				return builder.parseSingleToken(BNF.kPSEUDO_CONSTANTS, RubyElementTypes.PSEUDO_CONSTANT);
			}
			if(builder.compare(RubyTokenTypes.tIVAR))
			{
				return builder.parseSingleToken(RubyTokenTypes.tIVAR, RubyElementTypes.INSTANCE_VARIABLE);
			}
			if(builder.compare(RubyTokenTypes.tGVAR))
			{
				return builder.parseSingleToken(RubyTokenTypes.tGVAR, RubyElementTypes.GLOBAL_VARIABLE);
			}
			if(builder.compare(RubyTokenTypes.tCVAR))
			{
				return builder.parseSingleToken(RubyTokenTypes.tCVAR, RubyElementTypes.CLASS_VARIABLE);
			}
			if(builder.compare(RubyTokenTypes.tIDENTIFIER))
			{
				return builder.parseSingleToken(RubyTokenTypes.tIDENTIFIER, RubyElementTypes.IDENTIFIER);
			}
			if(builder.compare(RubyTokenTypes.tCONSTANT))
			{
				return builder.parseSingleToken(RubyTokenTypes.tCONSTANT, RubyElementTypes.CONSTANT);
			}
		}
		return RubyElementTypes.EMPTY_INPUT;
	}


}
