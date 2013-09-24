package org.jetbrains.plugins.ruby.ruby.codeInsight.usages.impl;

import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.SelfAssignAccess;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RAssignmentExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;

/**
 * @author: oleg
 * @date: Jul 23, 2008
 */
public class SelfAssignAccessImpl extends AssignAccessImpl implements SelfAssignAccess{
    public SelfAssignAccessImpl(final RAssignmentExpression assign, final RPsiElement usage) {
        super(assign, usage);
    }
}
