package org.jetbrains.plugins.ruby.ruby.codeInsight.usages;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;

/**
 * @author: oleg
 * @date: 06.05.2007
 */
public interface Access {
  @NotNull
  RPsiElement getElement();
}
