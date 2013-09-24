package org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.impl;

import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.AfterCallInstruction;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.CallEnvironment;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.Instruction;

import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
*
* @author: oleg
* @date: 07.04.2008
*/
class PostCallInstructionImpl extends InstructionImpl implements AfterCallInstruction {
  private CallInstructionImpl myCall;
  private RetInstruction myReturnInsn;

  public String toString() {
    return super.toString() + "AFTER CALL " + myCall.num();
  }

    public Iterable<? extends Instruction> pred(CallEnvironment env) {
      getStack(env, myReturnInsn).push(myCall);
      return Collections.singletonList(myReturnInsn);
    }

  public Iterable<? extends Instruction> allPred() {
    return Collections.singletonList(myReturnInsn);
  }

  protected String getElementPresentation() { return "";
  }

  PostCallInstructionImpl(int num, CallInstructionImpl call) {
    super(null, num);
    myCall = call;
  }

  public void setReturnInstruction(RetInstruction retInstruction) {
    myReturnInsn = retInstruction;
  }
}
