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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 30, 2007
 */
public abstract class RVirtualElementBase implements RVirtualElement {
    @NonNls
    protected static final String INDENT_STRING = "    ";
    @NonNls
    protected static final String NEW_LINE = "\n";

    // used in debug purposes only
    private static long currentID = 0;
    private long myId = 0;

    protected RVirtualElementBase() {
        myId = currentID++;
    }

    public static void resetIdCounter(){
        currentID = 0;
    }

    public abstract String toString();

    public String dump(){
        final StringBuilder buffer = new StringBuilder();
        dump(buffer, 0);
        return buffer.toString();
    }

    public void dump(@NotNull final StringBuilder buffer, final int indent){
        for (int i = 0; i < indent; i++) {
            buffer.append(INDENT_STRING);
        }
        buffer.append("[").append(myId).append("] ");
        buffer.append(toString());
    }

    public long getId() {
        return myId;
    }
}
