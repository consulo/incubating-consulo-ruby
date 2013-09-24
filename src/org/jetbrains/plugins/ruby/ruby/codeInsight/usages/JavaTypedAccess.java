package org.jetbrains.plugins.ruby.ruby.codeInsight.usages;

import com.intellij.psi.PsiType;
import org.jetbrains.annotations.Nullable;

/**
 * @author: oleg
 * @date: Jun 27, 2008
 */
public interface JavaTypedAccess extends ImplicitTypeAccess {
    @Nullable
    public PsiType getType();
}
