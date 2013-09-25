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
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualExtend;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 5, 2007
 */
public class RVirtualExtendImpl extends RVirtualIncludeImpl implements RVirtualExtend {
    public RVirtualExtendImpl(RVirtualContainer container, @NotNull List<RVirtualName> names) {
        super(container, names);
    }
    @Override
	public StructureType getType() {
        return StructureType.CALL_EXTEND;
    }

    public String toString() {
        return RCall.EXTEND_COMMAND;
    }

    @Override
	public void accept(@NotNull RubyVirtualElementVisitor visitor) {
        visitor.visitRVirtualExtend(this);
    }
}
