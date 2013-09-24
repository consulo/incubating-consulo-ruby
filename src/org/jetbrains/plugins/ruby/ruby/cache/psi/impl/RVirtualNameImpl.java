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
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Dec 4, 2006
 */
public class RVirtualNameImpl extends RVirtualElementBase implements RVirtualName, Serializable {
    protected static final String COLON2 = "::";

    protected boolean isGlobal;
    protected List<String> myFullPath;

// We don`t store it when serialize
    private transient String myName;
    protected transient String myFullName;


    public RVirtualNameImpl(@NotNull final List<String> fullPath, final boolean global){
        myFullPath = fullPath;
        isGlobal = global;
    }

    @NotNull
    public String getName() {
        if (myName==null){
            final int size = myFullPath.size();
            myName = size>0 ? myFullPath.get(size-1) : "";
        }
        return myName;
    }

    @NotNull
    public List<String> getPath() {
        return myFullPath;
    }

    @NotNull
    public String getFullName() {
        if (myFullName ==null){
            final StringBuilder buffer = new StringBuilder();
            if (isGlobal){
                buffer.append(COLON2);
            }
            boolean smthAdded = false;
            for (String s : myFullPath) {
                if (smthAdded){
                    buffer.append(COLON2);
                }
                buffer.append(s);
                smthAdded = true;
            }
            myFullName = buffer.toString();
        }
        return myFullName;
    }

    public void accept(@NotNull RubyVirtualElementVisitor visitor) {
        visitor.visitElement(this); 
    }

    public String toString() {
        return getFullName();
    }

    public boolean isGlobal() {
        return isGlobal;
    }

// Do not modify! Generated automatically

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RVirtualNameImpl that = (RVirtualNameImpl) o;

        if (isGlobal != that.isGlobal) return false;
        //noinspection RedundantIfStatement
        if (myFullPath != null ? !myFullPath.equals(that.myFullPath) : that.myFullPath != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (isGlobal ? 1 : 0);
        result = 31 * result + (myFullPath != null ? myFullPath.hashCode() : 0);
        return result;
    }
}
