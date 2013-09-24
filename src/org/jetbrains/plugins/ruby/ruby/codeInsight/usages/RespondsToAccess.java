package org.jetbrains.plugins.ruby.ruby.codeInsight.usages;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;

/**
 * @author oleg
 */
public interface RespondsToAccess extends ReferenceAccess {
  @NotNull
  RPsiElement getCall();

  @NotNull
  String getName();
}