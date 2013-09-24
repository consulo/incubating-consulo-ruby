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

package org.jetbrains.plugins.ruby.ruby.actions.editor.backspaceAction;

import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import junit.framework.Test;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.actions.editor.EditorActionTestCase;
import org.jetbrains.plugins.ruby.ruby.actions.editor.RubyEditorActionsManager;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 21, 2006
 */
public class BackspaceActionTest extends EditorActionTestCase {
    @NonNls private static final String DATA_PATH = PathUtil.getDataPath(BackspaceActionTest.class);

    public BackspaceActionTest() {
        super(DATA_PATH);
    }

    protected EditorActionHandler createHandler() {
        return RubyEditorActionsManager.registerRubyBackspaceActionHandler(EditorActionManager.getInstance());
    }

    public static Test suite(){
        return new BackspaceActionTest();
    }
}
