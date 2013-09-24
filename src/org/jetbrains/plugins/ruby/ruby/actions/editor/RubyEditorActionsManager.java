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

import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import org.jetbrains.plugins.ruby.ruby.actions.editor.handlers.RubyTypedHandler;
import org.jetbrains.plugins.ruby.ruby.actions.editor.handlers.editorHandlers.RubyBackspaceHandler;
import org.jetbrains.plugins.ruby.ruby.actions.editor.handlers.editorHandlers.RubyEnterHandler;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 09.08.2006
 */
public class RubyEditorActionsManager {
    /**
     * Register ruby editor actions handlers.
     */
    public static void registerRubyEditorActions(){
        EditorActionManager manager = EditorActionManager.getInstance();

        registerRubyTypedActionHandler(manager);

        registerRubyBackspaceActionHandler(manager);
        registerRubyEnterActionHandler(manager);
    }

    /**
     * Registers new <code>RubyEnterHandler</code> in manager
     * @param manager IDEA`s editor action manager
     * @see org.jetbrains.plugins.ruby.ruby.actions.editor.handlers.editorHandlers.RubyEnterHandler RubyEnterHandler for more details
     * @return Ruby EnterHandler
     */
    public static EditorActionHandler registerRubyEnterActionHandler(EditorActionManager manager) {
        EditorActionHandler myHandler =
                new RubyEnterHandler(manager.getActionHandler(IdeActions.ACTION_EDITOR_ENTER));
        manager.setActionHandler(IdeActions.ACTION_EDITOR_ENTER, myHandler);
        assert (myHandler == manager.getActionHandler(IdeActions.ACTION_EDITOR_ENTER));
        return myHandler;
    }

    /**
     * Registers new <code>RubyBackspaceHandler</code> in manager
     * @param manager IDEA`s editor action manager
     * @see RubyBackspaceHandler RubyBackspaceHandler for more details
     * @return RubyBackspace Handler
     */
    public static EditorActionHandler registerRubyBackspaceActionHandler(EditorActionManager manager) {
        EditorActionHandler myHandler =
                new RubyBackspaceHandler(manager.getActionHandler(IdeActions.ACTION_EDITOR_BACKSPACE));
        manager.setActionHandler(IdeActions.ACTION_EDITOR_BACKSPACE, myHandler);
        assert (myHandler == manager.getActionHandler(IdeActions.ACTION_EDITOR_BACKSPACE));
        return myHandler;
    }

    /**
     * Registers new <code>RubyTypedHandler</code> in manager
     * @param manager IDEA`s editor action manager
     * @see RubyTypedHandler RubyTypedHandler for more details
     * @return RubyTypedAction Handler
     */
    public static TypedActionHandler registerRubyTypedActionHandler(EditorActionManager manager){
        TypedAction originalTypedAction = manager.getTypedAction();
        TypedActionHandler myHandler =
                new RubyTypedHandler(originalTypedAction.getHandler());
        originalTypedAction.setupHandler(myHandler);
        return myHandler;
    }
}
