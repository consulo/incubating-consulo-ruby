package org.jetbrains.plugins.ruby.ruby.codeInsight.usages.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.ConstantAccess;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 07.04.2008
 */
public class ConstantAccessImpl extends AbstractReferenceAccess implements ConstantAccess
{
	private final RPsiElement myValue;

	public ConstantAccessImpl(final RPsiElement value, final RReference reference, final RPsiElement usage)
	{
		super(usage, reference);
		myValue = value;
	}

	@Override
	@NotNull
	public RConstant getConstant()
	{
		return (RConstant) myValue;
	}
}
