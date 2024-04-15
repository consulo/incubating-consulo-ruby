package org.jetbrains.plugins.ruby.ruby.codeInsight.usages;

import jakarta.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;

/**
 * @author oleg
 */
public interface RescueBlockAccess extends ImplicitTypeAccess
{
	@Nullable
	RPsiElement getTypeElement();
}
