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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Message;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.MessageWithVariousArgsNumber;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 25, 2007
 */
public class MessageWithVariousArgsNumberImpl extends MessageImpl implements MessageWithVariousArgsNumber {
    private int myMinNumber;
    private int myMaxNumber;

    public MessageWithVariousArgsNumberImpl(@NotNull String name, int minNumber, int maxNumber, final boolean important, @Nullable final Symbol symbol) {
        super(name, 0, important, symbol);
        myMinNumber = minNumber;
        myMaxNumber = maxNumber;
    }

    public int getMinArgsNumber() {
        return myMinNumber;
    }

    public int getMaxArgsNumber() {
        return myMaxNumber;
    }
    public boolean matchesMessage(@NotNull final Message patternMessage) {
        if (!getName().equals(patternMessage.getName())){
            return false;
        }
        final int min = getMinArgsNumber();
        final int max = getMaxArgsNumber();

        if (patternMessage instanceof MessageWithVariousArgsNumber){
            final MessageWithVariousArgsNumber message = (MessageWithVariousArgsNumber) patternMessage;
            return min <= message.getMinArgsNumber() && (max==-1 || message.getMaxArgsNumber()<=max);
        }

        final int number = patternMessage.getArgumentsNumber();
        return min<=number && (max==-1 || number<=max);
    }
}
