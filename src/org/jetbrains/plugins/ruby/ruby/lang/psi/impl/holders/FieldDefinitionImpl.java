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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.FieldDefinition;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.FieldType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RField;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 21.07.2006
 */
public class FieldDefinitionImpl implements FieldDefinition {
    private final RField myFirstUsage;

    public FieldDefinitionImpl(@NotNull final RField field){
// setting no acess as defaults
        myFirstUsage = field;
    }

    @Override
	@NotNull
    public RField getFirstUsage() {
        return myFirstUsage;
    }

    @Override
	@NotNull
    public String getName() {
        //noinspection ConstantConditions
        return myFirstUsage.getName();
    }

    @Override
	public FieldType getType() {
        return getFirstUsage().getType();
    }

    @Override
	public boolean isFor(@Nullable final RVirtualField field) {
        return field!=null && getType() == field.getType() && getName().equals(field.getName());
    }
}
