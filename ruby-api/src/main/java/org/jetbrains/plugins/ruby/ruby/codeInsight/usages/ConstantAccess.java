package org.jetbrains.plugins.ruby.ruby.codeInsight.usages;

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;

/**
 * @author oleg
 */
public interface ConstantAccess extends ReferenceAccess
{
	@Nonnull
	RConstant getConstant();
}
