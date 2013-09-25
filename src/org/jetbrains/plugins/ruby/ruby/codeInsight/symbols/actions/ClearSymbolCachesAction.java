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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.SymbolsCache;

/**
 * Created by IntelliJ IDEA.
 * User: Roman Chernyatchik
 * Date: Oct 22, 2007
 */
@SuppressWarnings({"ComponentNotRegistered"})
public class ClearSymbolCachesAction extends AnAction {

    @Override
	public void actionPerformed(AnActionEvent e) {
        final Project project = e.getData(DataKeys.PROJECT);
        if (project == null){
            return;
        }
        SymbolsCache.getInstance(project).clearCaches();
        Messages.showInfoMessage(project, "Symbol caches have been cleared", "Debug Tools");
    }
}