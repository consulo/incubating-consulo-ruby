package org.jetbrains.plugins.ruby.ruby.codeInsight.usages;

import jakarta.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 06.05.2007
 */
public interface AssignAccess extends WriteAccess
{
	@Nullable
	RPsiElement getValue();
}
