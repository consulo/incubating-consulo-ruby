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
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualConstant;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.ConstantDefinitions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 08.08.2006
 */
public class ConstantDefinitionsImpl implements ConstantDefinitions {
    private RConstant myFirstDefinition;

    public ConstantDefinitionsImpl(@NotNull final RConstant constant){
        myFirstDefinition  = constant;
    }

    @Override
	@NotNull
    public RConstant getFirstDefinition() {
        return myFirstDefinition;
    }

    @Override
	public void process(@NotNull final RConstant constant) {
        // do nothing
    }

    @Override
	@NotNull
    public String getName() {
        return myFirstDefinition.getName();
    }

    @Override
	public boolean isFor(@NotNull final RVirtualConstant constant) {
        return getName().equals(constant.getName());
    }
}
