package org.jetbrains.plugins.ruby.ruby.codeInsight.usages.impl;

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.ReferenceAccess;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 07.04.2008
 */
abstract class AbstractReferenceAccess implements ReferenceAccess
{
	private final RReference myReference;
	private final RPsiElement myUsage;

	public AbstractReferenceAccess(final RPsiElement usage, final RReference reference)
	{
		myReference = reference;
		myUsage = usage;
	}

	@Override
	@Nonnull
	public final RPsiElement getFullReference()
	{
		return myReference;
	}

	@Override
	@Nonnull
	public final RPsiElement getElement()
	{
		return myUsage;
	}
}