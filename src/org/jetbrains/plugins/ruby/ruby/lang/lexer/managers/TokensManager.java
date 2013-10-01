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

package org.jetbrains.plugins.ruby.ruby.lang.lexer.managers;

import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.lexer._RubyLexer;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.managers.state.Expr;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 10.09.2006
 */
public class TokensManager implements RubyTokenTypes
{

	final private StatesManager myStatesManager;
	private boolean spaceSeen;

	public TokensManager(final _RubyLexer lexer)
	{
		myStatesManager = lexer.getStatesManager();
	}

	public void reset()
	{
		spaceSeen = false;
	}

	/**
	 * @return true if resword is allowed now
	 */
	public boolean reswordAllowed()
	{
		return !isFnameAllowed();
	}

	/**
	 * @return true if "Identifier=" allowed
	 */
	public boolean isAssignOpAllowed()
	{
		return myStatesManager.getExpr() == Expr.FNAME;
	}

	/**
	 * @return true if fname allowed, i.e. tAREF, tASET, tUPLUS_OP, tUMINUS_OP
	 */
	public boolean isFnameAllowed()
	{
		final Expr expr = myStatesManager.getExpr();
		return expr == Expr.FNAME || expr == Expr.DOT_OR_COLON;
	}

	public boolean isColon2Allowed()
	{
		final Expr expr = myStatesManager.getExpr();
		return expr == Expr.END ||
				expr == Expr.CMD_ARG ||
				expr == Expr.ARG && !spaceSeen;
	}

	/**
	 * @return true if stringlike beginnig with % allowed
	 */
	public boolean stringAllowed()
	{
		return stringAllowed(false);
	}

	/**
	 * @param followCharIsWhiteSpaceOrEol Following character is whitespace or Eol
	 * @return true if string or regexp, beginning with /, % allowed
	 */
	public boolean stringAllowed(final boolean followCharIsWhiteSpaceOrEol)
	{
		final Expr expr = myStatesManager.getExpr();
		return !isFnameAllowed() && (expr == Expr.BEG || expr == Expr.CMD_BRACE || expr == Expr.MID ||
				expr == Expr.ARG && spaceSeen && !followCharIsWhiteSpaceOrEol);
	}

	/**
	 * @param followCharIsWhiteSpaceOrEol Following character is whitespace or Eol
	 * @return true if unary operation is allowed
	 */
	public boolean unaryAllowed(final boolean followCharIsWhiteSpaceOrEol)
	{
		final Expr expr = myStatesManager.getExpr();
		return !isFnameAllowed() && (expr == Expr.BEG || expr == Expr.CMD_BRACE || expr == Expr.MID || expr == Expr.CMD_ARG ||
				expr == Expr.ARG && spaceSeen && !followCharIsWhiteSpaceOrEol);
	}

	public boolean isHeredocAllowed()
	{
		return myStatesManager.getExpr() != Expr.CLASS && unaryAllowed(false);
	}

	/**
	 * Process the token seen and returns is
	 *
	 * @param type DuckType of token seen
	 * @return type
	 */
	public IElementType process(IElementType type)
	{
		spaceSeen = (type == tWHITE_SPACE);
		myStatesManager.process(type);
		return type;
	}

	public boolean ignoreEOL()
	{
		final Expr expr = myStatesManager.getExpr();
		return !myStatesManager.isAfterHeredoc() && (expr == Expr.BEG || expr == Expr.MID || expr == Expr.CMD_BRACE && myStatesManager.ignoreEol ||
				expr == Expr.DOT_OR_COLON || expr == Expr.CLASS || expr == Expr.FNAME);
	}

	/**
	 * @return true if fTokens allowed, i.e. func(, func{, func[, tCOLON2
	 */
	public boolean isFTokenAllowed()
	{
		return !spaceSeen && isExprEnd();
	}

	/**
	 * @return true if Arg token allowed, i.e. tLPAREN_ARG
	 */
	public boolean isArgTokenAllowed()
	{
		return spaceSeen && myStatesManager.getExpr() == Expr.ARG;
	}

	public boolean isExprEnd()
	{
		final Expr expr = myStatesManager.getExpr();
		return expr == Expr.END || expr == Expr.ARG;
	}
}
