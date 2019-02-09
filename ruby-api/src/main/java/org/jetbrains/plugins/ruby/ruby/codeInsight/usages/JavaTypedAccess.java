package org.jetbrains.plugins.ruby.ruby.codeInsight.usages;

import javax.annotation.Nullable;

import com.intellij.psi.PsiType;

/**
 * @author: oleg
 * @date: Jun 27, 2008
 */
public interface JavaTypedAccess extends ImplicitTypeAccess
{
	@Nullable
	public PsiType getType();
}
