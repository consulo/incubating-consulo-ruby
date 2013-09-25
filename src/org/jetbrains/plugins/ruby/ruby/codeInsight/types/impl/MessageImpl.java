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

package org.jetbrains.plugins.ruby.ruby.codeInsight.types.impl;

import com.intellij.openapi.util.Comparing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Message;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.MessageWithVariousArgsNumber;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 23, 2007
 */
public class MessageImpl implements Message {
    protected String myName;
    protected int myNumber;
    protected Symbol mySymbol;
    protected boolean isImportant;

    public MessageImpl(@NotNull final String name, final int number, final boolean important, @Nullable final Symbol symbol) {
        myName = name;
        myNumber = number;
        mySymbol = symbol;
        isImportant = important;
    }

    @Override
	@NotNull
    public String getName() {
        return myName;
    }

    @Override
	public int getArgumentsNumber() {
        return myNumber;
    }

    @Override
	@Nullable
    public Symbol getSymbol() {
        return mySymbol;
    }

    @Override
	public boolean matchesMessage(@NotNull final Message patternMessage) {
        if (patternMessage instanceof MessageWithVariousArgsNumber){
            return false;
        }
        //noinspection SimplifiableIfStatement
        if (!getName().equals(patternMessage.getName())){
            return false;
        }
        return getArgumentsNumber() == patternMessage.getArgumentsNumber();
    }

    @Override
	public boolean isImportant() {
        return isImportant;
    }

    public String toString() {
        return myName + ": " + mySymbol + "; " + isImportant;
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageImpl)) return false;
        final MessageImpl message = (MessageImpl) o;
        if (!Comparing.equal(getName(), message.getName())) return false;
        return getSymbol() == message.getSymbol();
    }
}
