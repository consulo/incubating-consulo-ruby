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

package org.jetbrains.plugins.ruby.ruby.cache.psi.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFieldContantContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualConstant;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 13, 2007
 */
public abstract class RVirtualFieldContantContainerImpl extends RVirtualContainerBase
        implements RVirtualFieldContantContainer {
    private List<RVirtualConstant> myConstants;
    private List<RVirtualField> myFields;

    // RVirtualConstantsHolder methods
    public void setVirtualConstants(@NotNull final List<RVirtualConstant> containerConstants) {
        myConstants = containerConstants;
    }

    @Override
	@NotNull
    public List<RVirtualConstant> getVirtualConstants() {
        return myConstants;
    }

    // RVirtualFieldsHolder methods
    public void setVirtualFields(List<RVirtualField> fields) {
        myFields = fields;
    }

    @Override
	@NotNull
    public List<RVirtualField> getVirtualFields() {
        return myFields;
    }

    protected RVirtualFieldContantContainerImpl(@Nullable RVirtualContainer container,
                                                @NotNull RVirtualName name,
                                                AccessModifier accessModifier,
                                                @NotNull RFileInfo containingFileInfo) {
        super(container, name, accessModifier, containingFileInfo);
    }

    @Override
	public void dump(@NotNull StringBuilder buffer, final int indent) {
        super.dump(buffer, indent);
        for (RVirtualConstant constant : myConstants) {
            buffer.append(NEW_LINE);
            ((RVirtualElementBase) constant).dump(buffer, indent+1);
        }
        for (RVirtualField myField : myFields) {
            buffer.append(NEW_LINE);
            ((RVirtualElementBase) myField).dump(buffer, indent+1);
        }
    }
}
