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
package org.jetbrains.plugins.ruby.ruby.lang.psi.dataFlow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.CallEnvironment;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.CallInstruction;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.Instruction;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.RControlFlowUtil;

public class DFAEngine<E>
{
	private static final int MAX_LOOP = 20;

	private Instruction[] myFlow;

	private DfaInstance<E> myDfa;
	private Semilattice<E> mySemilattice;


	public DFAEngine(Instruction[] flow, DfaInstance<E> dfa, Semilattice<E> semilattice)
	{
		myFlow = flow;
		myDfa = dfa;
		mySemilattice = semilattice;
	}

	private static class MyCallEnvironment implements CallEnvironment
	{
		ArrayList<Stack<CallInstruction>> myEnv;

		private MyCallEnvironment(int instructionNum)
		{
			myEnv = new ArrayList<Stack<CallInstruction>>(instructionNum);
			for(int i = 0; i < instructionNum; i++)
			{
				myEnv.add(new Stack<CallInstruction>());
			}
		}

		@Override
		public Stack<CallInstruction> callStack(Instruction instruction)
		{
			return myEnv.get(instruction.num());
		}

		@Override
		public void update(Stack<CallInstruction> callStack, Instruction instruction)
		{
			myEnv.set(instruction.num(), callStack);
		}
	}

	@SuppressWarnings({"AssignmentToForLoopParameter"})
	public ArrayList<E> performDFA()
	{
		final ArrayList<E> info = new ArrayList<E>(myFlow.length);
		final CallEnvironment env = new MyCallEnvironment(myFlow.length);
		// initializing dfa
		for(int i = 0; i < myFlow.length; i++)
		{
			info.add(i, myDfa.initial());
		}

		final boolean[] visited = new boolean[myFlow.length];

		final boolean forward = myDfa.isForward();
		int[] order = RControlFlowUtil.postorder(myFlow);
		for(int i = forward ? 0 : myFlow.length - 1; forward ? i < myFlow.length : i >= 0; )
		{
			final Instruction instr = myFlow[order[i]];

			if(!visited[instr.num()])
			{
				final Queue<Instruction> worklist = new LinkedList<Instruction>();

				worklist.add(instr);
				visited[instr.num()] = true;

				// Adding loop limit for prevent dfa mechanizm to work infinitely
				// It`s hard to even check if  this process converges stricktly
				// Moreover I suppose this is false.
				int loopNumber = 0;
				final int limit = myFlow.length * MAX_LOOP;
				while(!worklist.isEmpty() && loopNumber++ < limit)
				{
					final Instruction curr = worklist.remove();
					final int num = curr.num();
					final E oldE = info.get(num);
					final E newE = join(curr, info, env);
					myDfa.fun(newE, curr);
					if(!mySemilattice.eq(newE, oldE))
					{
						info.set(num, newE);
						for(Instruction next : getNext(curr, env))
						{
							worklist.add(next);
							visited[next.num()] = true;
						}
					}
				}
			}

			// Change loop index
			if(forward)
			{
				i++;
			}
			else
			{
				i--;
			}
		}

		return info;
	}

	private E join(Instruction instruction, ArrayList<E> info, CallEnvironment env)
	{
		final Iterable<? extends Instruction> prev = myDfa.isForward() ? instruction.pred(env) : instruction.succ(env);
		ArrayList<E> prevInfos = new ArrayList<E>();
		for(Instruction i : prev)
		{
			prevInfos.add(info.get(i.num()));
		}
		return mySemilattice.join(prevInfos);
	}

	private Iterable<? extends Instruction> getNext(Instruction curr, CallEnvironment env)
	{
		return myDfa.isForward() ? curr.succ(env) : curr.pred(env);
	}
}
