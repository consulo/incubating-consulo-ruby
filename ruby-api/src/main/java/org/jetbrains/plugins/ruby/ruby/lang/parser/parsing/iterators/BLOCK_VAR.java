package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.iterators;


import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.assignments.MLHS_OR_LHS;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * @author oleg
 */
public class BLOCK_VAR
{
	/*
		block_var	: lhs
				| mlhs
				;
	*/
	@Nonnull
	public static IElementType parse(final RBuilder builder)
	{
		RMarker iteratorVariablesMarker = builder.mark();
		IElementType result = MLHS_OR_LHS.parse(builder);
		if(result != RubyElementTypes.EMPTY_INPUT)
		{
			iteratorVariablesMarker.done(RubyElementTypes.BLOCK_VARIABLES);
			return RubyElementTypes.BLOCK_VARIABLES;
		}

		iteratorVariablesMarker.rollbackTo();
		builder.error(ErrorMsg.expected(RBundle.message("parsing.block.variables")));
		return RubyElementTypes.EMPTY_INPUT;
	}
}
