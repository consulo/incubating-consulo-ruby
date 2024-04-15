package org.jetbrains.plugins.ruby.ruby.codeInsight.usages;

import jakarta.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;

/**
 * @author oleg
 */
public interface ReferenceAccess extends ReadAccess
{
	@Nonnull
	RPsiElement getFullReference();
}