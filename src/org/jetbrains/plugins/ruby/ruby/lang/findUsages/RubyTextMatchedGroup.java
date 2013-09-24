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

package org.jetbrains.plugins.ruby.ruby.lang.findUsages;

import com.intellij.openapi.vcs.FileStatus;
import com.intellij.usages.UsageGroup;
import com.intellij.usages.UsageView;
import org.jetbrains.plugins.ruby.RBundle;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 2, 2007
 */
class RubyTextMatchedGroup implements UsageGroup {

    public Icon getIcon(boolean isOpen) {
        return null;
    }

    public String getText(UsageView view) {
        return RBundle.message("find.usages.text.matched.group");
    }

    public FileStatus getFileStatus() {
        return null;
    }

    public boolean isValid() {
        return true;
    }

    public void update() {
    }

    public int compareTo(UsageGroup usageGroup) {
        return getText(null).compareTo(usageGroup.getText(null));
    }

    public void navigate(boolean b) {
    }

    public boolean canNavigate() {
        return false;
    }

    public boolean canNavigateToSource() {
        return false;
    }
}
