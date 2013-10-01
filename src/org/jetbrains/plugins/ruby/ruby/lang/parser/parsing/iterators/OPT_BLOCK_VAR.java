package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.iterators;


import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;

/**
 * @author oleg
 */
public class OPT_BLOCK_VAR implements RubyTokenTypes
{
/*
	opt_block_var	: none
                | '|' '|'
                | tOROP
                | '|' block_var '|'
            ;
*/

	public static void parse(final RBuilder builder)
	{
		if(builder.compareAndEat(tPIPE))
		{
			BLOCK_VAR.parse(builder);
			builder.match(tPIPE);
		}
	}
}
