package org.jetbrains.plugins.ruby.jruby.codeInsight.usages;

import com.intellij.java.language.psi.PsiType;
import jakarta.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.ImplicitTypeAccess;

/**
 * @author: oleg
 * @date: Jun 27, 2008
 */
public interface JavaTypedAccess extends ImplicitTypeAccess
{
	@Nullable
	public PsiType getType();
}
