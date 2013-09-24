package org.jetbrains.plugins.ruby.ruby.codeInsight.usages.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.CallAccess;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 07.04.2008
 */
public class CallAccessImpl extends AbstractReferenceAccess implements CallAccess {
  private final RPsiElement myValue;
  private final RCall myCommandCall;

  public CallAccessImpl(final RPsiElement value, final RCall commandCall, final RReference reference, final RPsiElement usage) {
    super(usage, reference);
    myValue = value;
    myCommandCall = commandCall;
  }

  @NotNull
  public RPsiElement getCall() {
    return myValue;
  }

  public int getNumberOfArgs() {
    return myCommandCall != null ? myCommandCall.getArguments().size() : 0;
  }
}
