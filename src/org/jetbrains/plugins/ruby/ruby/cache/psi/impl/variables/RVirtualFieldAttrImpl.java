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

package org.jetbrains.plugins.ruby.ruby.cache.psi.impl.variables;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualStructuralElementBase;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.FieldAttrType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualFieldAttr;
import org.jetbrains.plugins.ruby.ruby.presentation.RFieldAttrPresentationUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Aug 28, 2007
 */
public class RVirtualFieldAttrImpl extends RVirtualStructuralElementBase implements RVirtualFieldAttr, Serializable {
    private FieldAttrType myType;
    private List<String> myNames;
    @NonNls
    private static final String COMMA = ",";
    @NonNls
    private static final String SPACE = " ";

    public RVirtualFieldAttrImpl(final RVirtualContainer container, final FieldAttrType type,
                                 @NotNull List<String> names
                                 ) {
        super(container);
        myType = type;
        myNames = names;
    }

    public void accept(@NotNull RubyVirtualElementVisitor visitor) {
        visitor.visitRVirtualFieldAttr(this);
    }

    public StructureType getType() {
        return StructureType.FIELD_ATTR_CALL;
    }

    @NotNull
    public List<String> getNames() {
        return myNames;
    }

    @NotNull
    public FieldAttrType getFieldAttrType() {
        return myType;
    }

    @NotNull
    public String getPresentableText() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(RFieldAttrPresentationUtil.getFieldAttrText(myType));
        boolean seen = false;
        for (String name : myNames) {
            if (seen){
                buffer.append(COMMA);
            }
            seen = true;
            buffer.append(SPACE).append(name);
        }
        return buffer.toString();
    }

    public String toString() {
        return getPresentableText();
    }

}
