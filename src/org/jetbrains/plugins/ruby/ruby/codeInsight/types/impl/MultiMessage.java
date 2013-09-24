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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Message;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 25, 2007
 */
public class MultiMessage extends MessageImpl implements Message{
    protected Message[] myMessages;

    public MultiMessage(@NotNull String name, final boolean important, @NotNull final Message ... messages) {
        super(name, 0, important, null);
        myMessages = messages;
    }

    public Message[] getMessages() {
        return myMessages;
    }

    public int getArgumentsNumber() {
        return 0;
    }

    public boolean matchesMessage(@NotNull final Message patternMessage) {
        for (Message message : myMessages) {
            if (!message.matchesMessage(patternMessage)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Symbol getSymbol() {
        for (Message message : myMessages) {
            final Symbol symbol = message.getSymbol();
            if (symbol!=null){
                return symbol;
            }
        }
        return null;
    }

    public Collection<Symbol> getSymbols() {
        final ArrayList<Symbol> list = new ArrayList<Symbol>(myMessages.length);
        for (Message message : myMessages) {
            final Symbol symbol = message.getSymbol();
            if (symbol!=null){
                list.add(symbol);
            }
        }
        return list;
    }
}