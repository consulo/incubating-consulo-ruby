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

package org.jetbrains.plugins.ruby.ruby.pom.impl;

import com.intellij.pom.PomModel;
import com.intellij.pom.PomModelAspect;
import com.intellij.pom.event.PomChangeSet;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.pom.RubyChange;
import org.jetbrains.plugins.ruby.ruby.pom.RubyChangeSet;
import org.jetbrains.plugins.ruby.ruby.pom.RubyPomAspect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 03.10.2006
 */
public class RubyChangeSetImpl implements RubyChangeSet {
    private final PomModel myModel;
    private List<RubyChange> myChanges = new ArrayList<RubyChange>();
    private final RFile mySubjectToChange;


    public RubyChangeSetImpl(PomModel model, RFile fileChanged) {
      myModel = model;
      mySubjectToChange = fileChanged;
    }

    public List<RubyChange> getChanges() {
        return Collections.unmodifiableList(myChanges);
    }

    public void add(RubyChange rubyChange) {
        myChanges.add(rubyChange);
    }

    public void clear() {
        myChanges.clear();
    }

    public RFile getChangedFile() {
        return mySubjectToChange;
    }

    public PomModelAspect getAspect() {
        return myModel.getModelAspect(RubyPomAspect.class);
    }

    public void merge(PomChangeSet blocked) {
        final List<RubyChange> changes = ((RubyChangeSetImpl)blocked).myChanges;
        for (RubyChange rubyChange : changes) {
          add(rubyChange);
        }
    }

    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        final Iterator<RubyChange> iterator = myChanges.iterator();
        while (iterator.hasNext()) {
            RubyChange rubyChange = iterator.next();
            buffer.append("(");
            buffer.append(rubyChange.toString());
            buffer.append(")");
            if (iterator.hasNext()) buffer.append(", ");
        }
        return buffer.toString();
    }
}
