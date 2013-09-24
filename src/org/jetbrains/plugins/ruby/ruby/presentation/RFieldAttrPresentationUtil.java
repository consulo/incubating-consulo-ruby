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
import com.intellij.ui.LayeredIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.FieldAttrType;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.methodCall.RCallBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Aug 29, 2007
 */
public class RFieldAttrPresentationUtil implements RubyIcons {
    public static Icon getAttrIcon(@NotNull final FieldAttrType fieldAttrType) {
        LayeredIcon icon = new LayeredIcon(2);
        if (fieldAttrType!=FieldAttrType.ATTR_INTERNAL && fieldAttrType!=FieldAttrType.CATTR_ACCESSOR){
            icon.setIcon(RUBY_FIELD_NODE, 0);
        } else {
            icon.setIcon(RUBY_ATTR_NODE, 0);
        }
        icon.setIcon(getFieldAttrIcon(fieldAttrType), 1);
        return icon;
    }

    private static Icon getFieldAttrIcon(@NotNull final FieldAttrType fieldAttrType) {
        if (fieldAttrType == FieldAttrType.ATTR_ACCESSOR){
            final LayeredIcon icon = new LayeredIcon(2);
            icon.setIcon(RUBY_ATTR_READER, 0);
            icon.setIcon(RUBY_ATTR_WRITER, 1);
            return icon;
        }
        if (fieldAttrType == FieldAttrType.ATTR_WRITER){
            return RUBY_ATTR_WRITER;
        }
        return RUBY_ATTR_READER;
    }

    @NotNull
    public static String getFieldAttrText(@NotNull final FieldAttrType fieldAttrType){
        if (fieldAttrType == FieldAttrType.ATTR_ACCESSOR){
            return RCall.ATTR_ACCESSOR_COMMAND;
        }
        if (fieldAttrType == FieldAttrType.ATTR_INTERNAL){
            return RCall.ATTR_INTERNAL;
        }
        if (fieldAttrType == FieldAttrType.CATTR_ACCESSOR){
            return RCall.CATTR_ACCESSOR;
        }
        if (fieldAttrType == FieldAttrType.ATTR_READER){
            return RCall.ATTR_READER_COMMAND;
        }
        return RCall.ATTR_WRITER_COMMAND;
    }

    @NotNull
    public static ItemPresentation getPresentation(@NotNull final RCallBase rCall) {
        assert rCall.getCallType().isAttributeCall();
        final Icon icon = getAttrIcon(rCall.getFieldAttrType());
        return new PresentationData(rCall.getText(),
                TextUtil.wrapInParens(getLocation(rCall)),
                icon, icon, null);
    }

    public static String getLocation(@NotNull final RCall call){
        return RContainerPresentationUtil.getLocation(call);
    }
}
