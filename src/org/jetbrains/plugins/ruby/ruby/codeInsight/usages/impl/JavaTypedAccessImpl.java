package org.jetbrains.plugins.ruby.ruby.codeInsight.usages.impl;

import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.JavaTypedAccess;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;

/**
 * @author: oleg
 * @date: Jun 27, 2008
 */
public class JavaTypedAccessImpl implements JavaTypedAccess {
    private final PsiParameter[] myParams;
    private final int myNumber;
    private final RPsiElement myUsage;

    public JavaTypedAccessImpl(final PsiParameter[] params, final int number, final RPsiElement usage) {
        myParams = params;
        myNumber = number;
        myUsage = usage;
    }

    @Nullable
    public PsiType getType() {
        return myParams[myNumber].getTypeElement().getType();
    }

    @NotNull
    public RPsiElement getElement() {
        return myUsage;
    }
}
