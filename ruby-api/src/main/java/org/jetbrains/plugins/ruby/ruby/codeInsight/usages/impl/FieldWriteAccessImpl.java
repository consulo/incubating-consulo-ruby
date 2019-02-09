package org.jetbrains.plugins.ruby.ruby.codeInsight.usages.impl;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.FieldWriteAccess;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 07.04.2008
 */
public class FieldWriteAccessImpl extends AbstractReferenceAccess implements FieldWriteAccess
{
	private final RPsiElement myValue;

	public FieldWriteAccessImpl(final RPsiElement value, final RReference reference, final RPsiElement usage)
	{
		super(usage, reference);
		myValue = value;
	}

	@Override
	@Nonnull
	public RPsiElement getField()
	{
		return myValue;
	}
}
