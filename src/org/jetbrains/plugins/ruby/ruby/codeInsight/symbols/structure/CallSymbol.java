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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data.Prototypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 28, 2008
 */

public class CallSymbol extends Symbol{
    protected Prototypes myPrototypes;
    protected RPsiElement myElement;

    public CallSymbol(@Nullable String name, final Type type, @NotNull final RPsiElement element){
        super(element.getProject(), name, type, null, null);
        myElement = element;
        myPrototypes = new Prototypes(null);
        myPrototypes.add(myElement);
    }

    @Override
	public RVirtualElement getLastVirtualPrototype(@Nullable final FileSymbol fileSymbol) {
        return myElement;
    }

    @Override
	@NotNull
    public Prototypes getVirtualPrototypes(@Nullable final FileSymbol fileSymbol) {
        return myPrototypes;
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CallSymbol)) return false;

        final CallSymbol that = (CallSymbol) o;
        if (myType!=that.getType()) return false;
        if (myName==null&&that.getName()!=null || !myName.equals(that.getName())) return false;
        return true;
    }

    public int hashCode() {
        int result;
        result = myType.hashCode();
        result = result * 31 + (myName!=null ? myName.hashCode() : 0);
        result = result * 31 + myElement.hashCode();
        return result;
    }
}