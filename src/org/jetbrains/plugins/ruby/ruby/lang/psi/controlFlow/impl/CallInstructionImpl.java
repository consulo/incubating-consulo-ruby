package org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.impl;

import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.CallEnvironment;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.CallInstruction;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.Instruction;

import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
*
* @author: oleg
* @date: 07.04.2008
*/
class CallInstructionImpl extends InstructionImpl implements CallInstruction {
  private InstructionImpl myCallee;

  public String toString() {
    return super.toString() + " CALL " + myCallee.num();
  }

    @Override
	public Iterable<? extends Instruction> succ(CallEnvironment env) {
      getStack(env, myCallee).push(this);
      return Collections.singletonList(myCallee);
    }

  @Override
  public Iterable<? extends Instruction> allSucc() {
    return Collections.singletonList(myCallee);
  }

  @Override
  protected String getElementPresentation() { return ""; }

  CallInstructionImpl(int num, InstructionImpl callee) {
    super(null, num);
    myCallee = callee;
  }
}
