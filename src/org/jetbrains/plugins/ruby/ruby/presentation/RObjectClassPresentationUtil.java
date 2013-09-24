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

package org.jetbrains.plugins.ruby.ruby.presentation;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualObjectClass;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 13, 2007
 */

/**
 * For ".self" etc
 */
public class RObjectClassPresentationUtil {

    public static Icon getIcon() {
        return RubyIcons.RUBY_OBJECT_CLASS_NODE;
    }

    @NotNull
    public static ItemPresentation getPresentation(@NotNull final RVirtualObjectClass objectClass) {
        final Icon icon = getIcon();
        return new PresentationData(objectClass.getPresentableName(),
                TextUtil.wrapInParens(getLocation(objectClass)),
                icon, icon, null);
    }

    private static String getLocation(RVirtualObjectClass objectClass) {
        return RContainerPresentationUtil.getLocation(objectClass);
    }

}
