/*
 * Copyright 2000-2008 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.ruby.ruby.codeInsight.completion;

import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 22, 2007
 */
public class RubyPsiLookupItem implements RubyLookupItem,
        PresentableLookupValue,
        DeferredUserLookupValue,
        LookupValueWithUIHint,
        LookupValueWithUIHint2,
        LookupValueWithPsiElement,
        LookupValueWithTail,
        LookupValueWithPriority,
        Iconable {


    private RVirtualElement myPrototype;
    private String myLookupString;
    private String myTypeText;
    private boolean isBold;
    private Icon myIcon;
    private Project myProject;
    private String myTailText;
    private PsiElement myCachedPsiElement;
    private int myPriority;

    @NotNull
    public String getName() {
        return myLookupString;
    }

    public RubyPsiLookupItem(@NotNull final Project project,
                             @NotNull final String lookupString,
                             @Nullable final String tailText,
                             @Nullable final String typeText,
                             @NotNull final RVirtualElement prototype,
                             final int priority,
                             final boolean bold,
                             @Nullable final Icon icon) {
        myProject = project;
        myLookupString = lookupString;
        myTailText = tailText;
        myTypeText = typeText;
        myPrototype = prototype;
        myPriority = priority;
        isBold = bold;
        myIcon = icon;
    }

    public boolean isStrikeout() {
        return false;
    }

    public String getPresentation() {
        return myLookupString;
    }

    public boolean handleUserSelection(LookupItem item, Project project) {
        return true;
    }

    public String getTypeHint() {
        return myTypeText;
    }

    public Color getColorHint() {
        return null;
    }

    public boolean isBold() {
        return isBold;
    }

    public int getPriority() {
        return myPriority;
    }

    public PsiElement getElement() {
        if (myPrototype == null){
            return null;
        }
        if (myCachedPsiElement == null){
// The slowest operation!
            myCachedPsiElement = RVirtualPsiUtil.findPsiByVirtualElement(myPrototype, myProject);
        }
        return myCachedPsiElement;
    }

    public Icon getIcon(int flags) {
        return myIcon;
    }

    public String getTailText() {
        return myTailText!=null ? myTailText : "";
    }
}
