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

package org.jetbrains.plugins.ruby.ruby.actions.editor.typedAction;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.ex.EditorEx;
import junit.framework.Test;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.actions.BaseEditorActionTestCase;
import org.jetbrains.plugins.ruby.ruby.actions.editor.RubyEditorActionsManager;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 21, 2006
 */
public class TypedActionTest extends BaseEditorActionTestCase {
    @NonNls private static final String DATA_PATH = PathUtil.getDataPath(TypedActionTest.class);

    public TypedActionTest() {
        super(DATA_PATH);
    }

    protected void performAction() {
        final char charToPrint = myData.get(1).charAt(0);
        // RUBY-1697
        if (myData.size()==3){
            if ("overwrite".equals(myData.get(2).trim())){
                ((EditorEx) myEditor).setInsertMode(false);
            }
        }
        final DataContext dataContext = new RubyDataContext();
        final TypedActionHandler handler =
                RubyEditorActionsManager.registerRubyTypedActionHandler(EditorActionManager.getInstance());

        performAction(new Runnable() {
            public void run() {
                handler.execute(myEditor, charToPrint, dataContext);
            }
        });
    }

    public static Test suite(){
        return new TypedActionTest();
    }

//    public String getSearchPattern(){
//        return ".*/wstring5\\.txt";
//    }

}