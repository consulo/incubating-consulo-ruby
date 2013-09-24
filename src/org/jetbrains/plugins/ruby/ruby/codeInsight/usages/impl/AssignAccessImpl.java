package org.jetbrains.plugins.ruby.ruby.codeInsight.usages.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.AssignAccess;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RAssignmentExpression;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 07.04.2008
 */
public class AssignAccessImpl implements AssignAccess {
    private final RAssignmentExpression myAssign;
    private final RPsiElement myUsage;

    public AssignAccessImpl(final RAssignmentExpression assign, final RPsiElement usage) {
        myAssign = assign;
        myUsage = usage;
    }

    @Nullable
    public RPsiElement getValue() {
        return myAssign.getValue();
    }

    @NotNull
    public RPsiElement getElement() {
        return myUsage;
    }
}
