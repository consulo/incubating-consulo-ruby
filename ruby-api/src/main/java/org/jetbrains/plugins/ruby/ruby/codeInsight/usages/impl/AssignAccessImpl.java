package org.jetbrains.plugins.ruby.ruby.codeInsight.usages.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.AssignAccess;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RAssignmentExpression;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 07.04.2008
 */
public class AssignAccessImpl implements AssignAccess
{
	private final RAssignmentExpression myAssign;
	private final RPsiElement myUsage;

	public AssignAccessImpl(final RAssignmentExpression assign, final RPsiElement usage)
	{
		myAssign = assign;
		myUsage = usage;
	}

	@Override
	@Nullable
	public RPsiElement getValue()
	{
		return myAssign.getValue();
	}

	@Override
	@Nonnull
	public RPsiElement getElement()
	{
		return myUsage;
	}
}
