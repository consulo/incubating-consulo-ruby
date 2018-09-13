package org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.impl;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.CallEnvironment;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.CallInstruction;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.Instruction;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 07.04.2008
 */
class RetInstruction extends InstructionImpl
{
	RetInstruction(int num)
	{
		super(null, num);
	}

	@Override
	public Iterable<? extends Instruction> succ(CallEnvironment env)
	{
		final Stack<CallInstruction> callStack = getStack(env, this);
		if(callStack.isEmpty())
		{
			return Collections.emptyList();
		}

		final CallInstruction callInstruction = callStack.peek();
		final List<InstructionImpl> succ = ((CallInstructionImpl) callInstruction).mySucc;
		final Stack<CallInstruction> copy = (Stack<CallInstruction>) callStack.clone();
		copy.pop();
		for(InstructionImpl instruction : succ)
		{
			env.update(copy, instruction);
		}

		return succ;
	}

	public String toString()
	{
		return super.toString() + " RETURN";
	}

	@Override
	protected String getElementPresentation()
	{
		return "";
	}
}
