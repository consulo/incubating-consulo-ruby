package org.jetbrains.plugins.ruby.ruby.codeInsight.usages;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;

/**
 * @author oleg
 */
public interface ConstantAccess extends ReferenceAccess
{
	@NotNull
	RConstant getConstant();
}
