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

import java.lang.reflect.Field;
import java.util.Stack;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.lexer._RubyLexer;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.managers.state.BraceCounter;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.managers.state.Delimiter;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.managers.state.Expr;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.managers.state.LexState;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.managers.state.State;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.TokenBNF;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 21.08.2006
 */
public class StatesManager implements RubyTokenTypes
{

	@NonNls
	private static final String STATE_PATTERN = "(YYINITIAL)|(.*_STATE)";

	private Stack<State> myStatesStack;
	private _RubyLexer myLexer;

	public StatesManager(final _RubyLexer lexer)
	{
		myLexer = lexer;
		myStatesStack = new Stack<State>();
	}

	public void reset()
	{
		myStatesStack.clear();
	}

	/**
	 * turns the lexer state to previous lexical state
	 */
	public void toPreviousState()
	{
		assert (myStatesStack.size() >= 2);
		myStatesStack.pop();
		myLexer.yybegin(myStatesStack.peek().getYYState());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////// To new state section ////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * turns lexer to new lexical state
	 *
	 * @param beginDelimiter Begin delimiter. is used when in string like states
	 * @param state          State id
	 */
	public void toStringLikeState(final int state, char beginDelimiter)
	{
		final State newState = new State(state);
		newState.addComponent(new BraceCounter(1));
		newState.addComponent(new Delimiter(beginDelimiter, TextUtil.getCloseDelim(beginDelimiter)));
		changeLexState(newState);
	}

	/**
	 * turns lexer to new lexical state. Without baseComponent
	 *
	 * @param state State id
	 */
	public void toState(final int state)
	{
		final State newState = new State(state);
		newState.addComponent(new LexState());
		changeLexState(newState);
	}

	/**
	 * turns lexer to variable subt state.
	 *
	 * @param state State id
	 */
	public void toVarSubtState(final int state)
	{
		assert state == _RubyLexer.IN_VAR_SUBT_STATE;
		final State newState = new State(state);
		changeLexState(newState);
	}

	/**
	 * turns lexer to expression subt state.
	 *
	 * @param state State id
	 */
	public void toExprSubtState(final int state)
	{
		assert state == _RubyLexer.IN_EXPR_SUBT_STATE;
		final State newState = new State(state);
		newState.addComponent(new BraceCounter(1));
		newState.addComponent(new LexState());
		changeLexState(newState);
	}

	/**
	 * Turns to new state
	 *
	 * @param newState New state object
	 */
	private void changeLexState(final State newState)
	{
		myStatesStack.push(newState);
		myLexer.yybegin(newState.getYYState());
	}

	/**
	 * Debug function. Returns the name of lexical state
	 *
	 * @param state lexical state number
	 * @return corresponding to the state number state name
	 */
	@Nullable
	private String getStateName(final int state)
	{
		Field[] fields = myLexer.getClass().getFields();
		for(Field f : fields)
		{
			if(f.getName().toUpperCase().matches(STATE_PATTERN))
			{
				try
				{
					if(f.getInt(this) == state)
					{
						return f.getName();
					}
				}
				catch(IllegalAccessException e)
				{
					// ignore
				}

			}
		}
		return null;
	}

	/**
	 * @return Current state name
	 */
	@Nullable
	public String getStateName()
	{
		return getStateName(getState());
	}

	/**
	 * @return current state
	 */
	public int getState()
	{
		return getStateObject().getYYState();
	}

	/**
	 * @return current state Object
	 */
	@NotNull
	private State getStateObject()
	{
		State currentState = myStatesStack.peek();
		assert (myLexer.yystate() == currentState.getYYState());
		return currentState;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Delimiter component functions /////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Nullable
	private Delimiter getDelimiter()
	{
		return getStateObject().getComponent(Delimiter.class);
	}

	/**
	 * @return Current state begin delimiter. For states with EndDelimiter component added.
	 */
	public char getBeginDelimiter()
	{
		final Delimiter delimiter = getDelimiter();
		assert delimiter != null;
		return delimiter.getBeginDelimiter();
	}

	/**
	 * @return Current state end delimiter. For states with EndDelimiter component added.
	 */
	public char getEndDelimiter()
	{
		final Delimiter delimiter = getDelimiter();
		assert delimiter != null;
		return delimiter.getEndDelimiter();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// BraceCounter component functions //////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Nullable
	private BraceCounter getBraceCounter()
	{
		return getStateObject().getComponent(BraceCounter.class);
	}

	/**
	 * @return true BraceCounter is empty, false otherwise.
	 */
	public boolean wasEndBraceSeen()
	{
		final BraceCounter braceCounter = getBraceCounter();
		assert braceCounter != null;
		return braceCounter.isEmpty();
	}

	/**
	 * Adds new open brace to current BraceCounter
	 */
	public void processOpenBrace()
	{
		final BraceCounter braceCounter = getBraceCounter();
		assert braceCounter != null;
		braceCounter.processOpenBrace();
	}

	/**
	 * Adds new close brace to current BraceCounter
	 */
	public void processCloseBrace()
	{
		final BraceCounter braceCounter = getBraceCounter();
		assert braceCounter != null;
		braceCounter.processCloseBrace();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// BaseComponent functions ///////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Nullable
	private LexState getLexState()
	{
		return getStateObject().getComponent(LexState.class);
	}

	// if first alias item passed
	private boolean aliasOutBound1;
	// if second alias item passed
	private boolean aliasOutBound2;

	// If in def with parens
	private int defParensBalance;

	// If in CMD_BRACE after first pipe
	private boolean pipeSeen;
	boolean ignoreEol;

	// Getters

	public boolean isInDefState()
	{
		final LexState lexState = getLexState();
		assert lexState != null;
		return lexState.isInDef();
	}

	public boolean isInDef()
	{
		return isInDefState() && defParensBalance == 0;
	}

	public boolean isInAlias()
	{
		final LexState lexState = getLexState();
		assert lexState != null;
		return lexState.isInAlias();
	}

	public boolean isInUndef()
	{
		final LexState lexState = getLexState();
		assert lexState != null;
		return lexState.isInUndef();
	}

	public boolean isAfterHeredoc()
	{
		final LexState lexState = getLexState();
		assert lexState != null;
		return lexState.isAfterHeredoc();
	}

	public boolean isAfterSymBeg()
	{
		final LexState lexState = getLexState();
		assert lexState != null;
		return lexState.isAfterSymBeg();
	}

	public boolean isDoCondExpected()
	{
		final LexState lexState = getLexState();
		assert lexState != null;
		return lexState.isDoCondExpected();
	}

	public Expr getExpr()
	{
		final LexState lexState = getLexState();
		assert lexState != null;
		return lexState.getExpr();
	}

	// Setters

	private void setInDef(boolean value)
	{
		final LexState lexState = getLexState();
		assert lexState != null;
		lexState.setInDef(value);
		defParensBalance = 0;
	}

	private void setInAlias(boolean value)
	{
		final LexState lexState = getLexState();
		assert lexState != null;
		lexState.setInAlias(value);
		aliasOutBound1 = false;
		aliasOutBound2 = false;
	}

	private void setInUndef(boolean value)
	{
		final LexState lexState = getLexState();
		assert lexState != null;
		lexState.setInUndef(value);
	}

	private void setAfterSymBeg(boolean value)
	{
		final LexState lexState = getLexState();
		assert lexState != null;
		lexState.setAfterSymBeg(value);
	}

	private void setExpr(Expr value)
	{
		final LexState lexState = getLexState();
		assert lexState != null;
		lexState.setExpr(value);
	}

	public void setAfterHeredoc(boolean value)
	{
		final LexState lexState = getLexState();
		assert lexState != null;
		lexState.setAfterHeredoc(value);
	}

	public void setDoCondExpected(boolean value)
	{
		final LexState lexState = getLexState();
		assert lexState != null;
		lexState.setDoCondExpected(value);
	}


	/**
	 * Changes BaseComponent properties
	 *
	 * @param type Last token returned by lexer
	 */
	public void process(final IElementType type)
	{
		// if State has no lex state component
		if(getLexState() == null)
		{
			return;
		}
		processToken(type);
		// Change state if needed
		changeStateIfNeeded();
	}

	private void processToken(final IElementType type)
	{
		////// AftertSYMBEG ////////////////////////////////////////////////////////////////////////////////////////////////////
		if(isAfterSymBeg())
		{
			setAfterSymBeg(false);
			setExpr(Expr.END);
			if(!isInAlias())
			{
				return;
			}
		}
		else if(type == tSYMBEG)
		{
			setAfterSymBeg(true);
			setExpr(Expr.FNAME);
		}

		// if type isn`t comment or whitespace
		if(!BNF.tWHITESPACES_OR_COMMENTS.contains(type))
		{

			////// ExprCmdBrace ////////////////////////////////////////////////////////////////////////////////////////////////////
			if(getExpr() == Expr.CMD_BRACE)
			{
				ignoreEol = !pipeSeen || type == tCOMMA;
				if(type == tPIPE)
				{
					// we change state to Expr.BEG after second pipe
					if(pipeSeen)
					{
						setExpr(Expr.BEG);
						return;
					}
					// it`s only the first pipe
					pipeSeen = true;
				}

				// We stay in this state until closing pipe
				if(pipeSeen)
				{
					return;
				}
				else
				{
					// We havn`t seen first pipe and should PROCESS MORE
					setExpr(Expr.BEG);
				}
			}

			////// InALIAS /////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(isInAlias())
			{
				if(!isAfterSymBeg())
				{
					if(aliasOutBound1)
					{
						aliasOutBound2 = true;
					}
					aliasOutBound1 = true;
				}

				if(aliasOutBound2)
				{
					setInAlias(false);
					setExpr(Expr.END);
					return;
				}
			}
			else if(type == kALIAS)
			{
				setInAlias(true);
				setExpr(Expr.FNAME);
				return;
			}

			////// InDEF ///////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(isInDefState())
			{
				if(getExpr() == Expr.FNAME && BNF.tFNAME.contains(type))
				{
					setExpr(Expr.CMD_ARG);
					return;
				}
				else if(BNF.tTERM_TOKENS.contains(type))
				{
					setInDef(false);
					setExpr(Expr.BEG);
					return;
				}
				else if(BNF.tLPARENS.contains(type))
				{
					defParensBalance++;
				}
				else if(type == tRPAREN)
				{
					if(defParensBalance == 1)
					{
						setInDef(false);
						setExpr(Expr.BEG);
						return;
					}
					else
					{
						defParensBalance--;
					}
				}
			}
			else if(type == kDEF)
			{
				setInDef(true);
				setExpr(Expr.FNAME);
				return;
			}

			////// InUNDEF /////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(isInUndef())
			{
				if(getExpr() == Expr.FNAME)
				{
					if(BNF.tFNAME.contains(type))
					{
						setExpr(Expr.END);
						return;
					}
				}
				else if(type == tCOMMA)
				{
					setExpr(Expr.FNAME);
					return;
				}
				else
				{
					setInUndef(false);
					setExpr(Expr.END);
					return;
				}
			}
			else if(type == kUNDEF)
			{
				setInUndef(true);
				setExpr(Expr.FNAME);
				return;
			}

			////// AfterHEREDOC_ID /////////////////////////////////////////////////////////////////////////////////////////////////
			if(type == tHEREDOC_ID)
			{
				setAfterHeredoc(true);
			}

			////// isDoCondExpected ////////////////////////////////////////////////////////////////////////////////////////////////
			if(type == kFOR || type == kWHILE || type == kUNTIL)
			{
				setDoCondExpected(true);
			}

			if(type == kDO_COND || BNF.tTERM_TOKENS.contains(type) || TokenBNF.tOUTER_ELEMENTS.contains(type))
			{
				setDoCondExpected(false);
			}

			////// ExprkCLASS /////////////////////////////////////////////////////////////////////////////////////////////////////
			if(getExpr() != Expr.FNAME && type == kCLASS)
			{
				setExpr(Expr.CLASS);
				return;
			}

			////// ExprDotOrColon //////////////////////////////////////////////////////////////////////////////////////////////////
			if(BNF.tDOT_OR_COLON.contains(type))
			{
				setExpr(getExpr() == Expr.FNAME || getExpr() == Expr.CMD_ARG ? Expr.FNAME : Expr.DOT_OR_COLON);
				return;
			}

			////// ExprCmdBrace ////////////////////////////////////////////////////////////////////////////////////////////////////
			if((getExpr() == Expr.END || getExpr() == Expr.ARG) && BNF.tCODE_BLOCK_BEG_TOKENS.contains(type))
			{
				setExpr(Expr.CMD_BRACE);
				pipeSeen = false;
				ignoreEol = true;
				return;
			}

			////// ExpArg //////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(getExpr() == Expr.BEG && (type == kRETURN || type == kYIELD || type == kSUPER || type == kRESCUE || type == kDEFINED) ||
					getExpr() == Expr.BEG && BNF.tOPERATION.contains(type) ||
					getExpr() == Expr.DOT_OR_COLON && BNF.tOPERATION2.contains(type))
			{
				setExpr(Expr.ARG);
				return;
			}
			/////// ExprBeg ////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(BNF.tEXPR_BEG_AFTER_TOKENS.contains(type))
			{
				setExpr(Expr.BEG);
				return;
			}
			////// ExprMid ////////////////////////////////////////////////////////////////////////////////////////////////////////
			if((getExpr() == Expr.END || getExpr() == Expr.ARG) && BNF.tBINARY_OPS.contains(type))
			{
				setExpr(Expr.MID);
				return;
			}
			////// ExprEnd ////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(getExpr() != Expr.FNAME && BNF.EXPR_LAST_TOKEN.contains(type))
			{
				setExpr(Expr.END);
			}
		}
	}

	private void changeStateIfNeeded()
	{
		final LexState lexState = getLexState();
		if(lexState == null)
		{
			return;
		}
		final boolean inSpecialState = lexState.isSpecialState();

		final State stateObject = getStateObject();
		final int currentState = stateObject.getYYState();

		// we should change states only if in YYINITIAL or in SPECIAL_STATE
		if(!(currentState == _RubyLexer.YYINITIAL || currentState == _RubyLexer.SPECIAL_STATE))
		{
			return;
		}

		if(inSpecialState && currentState == _RubyLexer.YYINITIAL)
		{
			stateObject.setYYState(_RubyLexer.SPECIAL_STATE);
			myLexer.yybegin(_RubyLexer.SPECIAL_STATE);
		}

		if(!inSpecialState && currentState == _RubyLexer.SPECIAL_STATE)
		{
			stateObject.setYYState(_RubyLexer.YYINITIAL);
			myLexer.yybegin(_RubyLexer.YYINITIAL);
		}
	}
}
