/*
 * Copyright 2000-2007 JetBrains s.r.o.
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
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Message;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RUnionType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 15, 2008
 */
public class RUnionTypeImpl extends RTypeBase implements RUnionType {
    public RType myType1;
    public RType myType2;

    public RUnionTypeImpl(final RType type1, final RType type2) {
        myType1 = type1;
        myType2 = type2;
    }


    @Override
	@SuppressWarnings({"unchecked"})
    @NotNull
    public Collection<Message> getMessages() {
        return RTypeUtil.union(myType1.getMessages(), myType2.getMessages());
    }

    @Override
	@SuppressWarnings({"unchecked"})
    public Collection<Message> getMessagesForName(@Nullable final String name) {
        return RTypeUtil.union(myType1.getMessagesForName(name), myType2.getMessagesForName(name));
    }

    @Override
	public boolean isTyped() {
        return myType1.isTyped() || myType2.isTyped();
    }

    public String toString() {
        return "Union type";
    }

    @Override
	public RType addMessage(@NotNull final Message message) {
        if (myType2 instanceof RDuckTypeImpl) {
            myType2.addMessage(message);
            return this;
        }
        final DuckTypeImpl type = new DuckTypeImpl();
        type.addMessage(message);
        return RTypeUtil.joinAnd(this, new RDuckTypeImpl(type));
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      final RUnionTypeImpl that = (RUnionTypeImpl)o;
      if ((myType1.equals(that.myType1) && myType2.equals(that.myType2)) ||
          (myType2.equals(that.myType1) && myType1.equals(that.myType2))) {
        return true;
      }
      return false;
    }

    @Override
    public int hashCode() {
      return myType1.hashCode() << 16 + myType2.hashCode();
    }
}
