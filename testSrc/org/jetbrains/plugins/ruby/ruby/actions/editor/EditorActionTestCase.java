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

package org.jetbrains.plugins.ruby.ruby.actions.editor;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import junit.framework.Assert;
import org.jetbrains.plugins.ruby.ruby.actions.BaseEditorActionTestCase;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 21, 2006
 */
public abstract class EditorActionTestCase extends BaseEditorActionTestCase {
    public EditorActionTestCase(String path) {
        super(path);
    }

    protected void performAction() {
        final EditorActionHandler handler = createHandler();
        final DataContext dataContext = new RubyDataContext();
        Assert.assertTrue(handler.isEnabled(myEditor, dataContext));

        performAction(new Runnable(){
            public void run() {
                handler.execute(myEditor, dataContext);
            }
        });
    }

    protected abstract EditorActionHandler createHandler();
}
