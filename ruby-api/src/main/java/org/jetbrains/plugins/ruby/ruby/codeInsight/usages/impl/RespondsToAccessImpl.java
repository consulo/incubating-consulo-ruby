package org.jetbrains.plugins.ruby.ruby.codeInsight.usages.impl;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.RespondsToAccess;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;

/**
 * @author: oleg
 * @date: Jun 27, 2008
 */
public class RespondsToAccessImpl extends AbstractReferenceAccess implements RespondsToAccess
{
	private final RPsiElement myCall;
	private final String myName;

	public RespondsToAccessImpl(final RPsiElement usage, final RReference reference, final RPsiElement call, final String name)
	{
		super(usage, reference);
		myCall = call;
		myName = name;
	}

	@Override
	@Nonnull
	public RPsiElement getCall()
	{
		return myCall;
	}

	@Override
	@Nonnull
	public String getName()
	{
		return myName;
	}
}
