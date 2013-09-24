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

import com.intellij.openapi.progress.ProgressManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Message;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RJoinType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 13, 2008
 */
public class RJoinTypeImpl extends RTypeBase implements RJoinType {
    private RType myType1;
    private RType myType2;

    public RJoinTypeImpl(final RType type1, final RType type2) {
        myType1 = type1;
        myType2 = type2;
    }

    @SuppressWarnings({"unchecked"})
    @NotNull
    public Collection<Message> getMessages() {
        ProgressManager.getInstance().checkCanceled();

        return RTypeUtil.intersection(myType1.getMessages(), myType2.getMessages());
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Message> getMessagesForName(@Nullable final String name) {
        ProgressManager.getInstance().checkCanceled();

        return RTypeUtil.intersection(myType1.getMessagesForName(name), myType2.getMessagesForName(name));
    }

    public boolean isTyped() {
        return myType1.isTyped() || myType2.isTyped();
    }

    public String toString() {
        return "Join type";
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      final RJoinTypeImpl that = (RJoinTypeImpl)o;
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

    public boolean containsType(final RType type) {
      return myType1.equals(type) || myType2.equals(type);
    }
}
