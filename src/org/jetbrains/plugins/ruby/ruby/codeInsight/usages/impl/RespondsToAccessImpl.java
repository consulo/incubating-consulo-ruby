package org.jetbrains.plugins.ruby.ruby.codeInsight.usages.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.RespondsToAccess;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;

/**
 * @author: oleg
 * @date: Jun 27, 2008
 */
public class RespondsToAccessImpl extends AbstractReferenceAccess implements RespondsToAccess{
  private final RPsiElement myCall;
  private final String myName;

  public RespondsToAccessImpl(final RPsiElement usage, final RReference reference, final RPsiElement call, final String name) {
    super(usage, reference);
    myCall = call;
    myName = name;
  }

  @Override
  @NotNull
  public RPsiElement getCall() {
    return myCall;
  }

  @Override
  @NotNull
  public String getName() {
    return myName;
  }
}
