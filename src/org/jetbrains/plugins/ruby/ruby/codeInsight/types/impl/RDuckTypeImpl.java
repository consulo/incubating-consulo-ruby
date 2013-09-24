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
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.DuckType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Message;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RDuckType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 13, 2008
 */
public class RDuckTypeImpl extends RTypeBase implements RDuckType{
    private DuckType myDuckType;
    private boolean isTyped;

    public RDuckTypeImpl(final DuckType type) {
        this(type, false);
    }

    public RDuckTypeImpl(final DuckType type, final boolean typed) {
        myDuckType = type;
        isTyped = typed;
    }

    public boolean isTyped() {
        return isTyped;
    }

    public DuckType getDuckType() {
        return myDuckType;
    }

    @NotNull
    public Collection<Message> getMessages() {
        return myDuckType.getMessages();
    }

    public Collection<Message> getMessagesForName(@Nullable final String name) {
        return myDuckType.getMessagesForName(name);
    }

    public RType addMessage(@NotNull final Message message) {
        ((DuckTypeImpl) myDuckType).addMessage(message);
        return this;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      final RDuckTypeImpl that = (RDuckTypeImpl)o;
      return CollectionUtils.isEqualCollection(myDuckType.getMessages(), that.myDuckType.getMessages());
    }

    @Override
    public int hashCode() {
      return myDuckType.getMessages().hashCode();
    }
}
