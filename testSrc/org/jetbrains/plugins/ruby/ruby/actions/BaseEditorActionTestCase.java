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

package org.jetbrains.plugins.ruby.ruby.actions;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.IntRef;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.eRubyLanguage;
import org.jetbrains.plugins.ruby.ruby.testCases.BaseRubyFileSetTestCase;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.08.2006
 */
public abstract class BaseEditorActionTestCase extends BaseRubyFileSetTestCase {
    @NonNls protected static final String CARET_MARKER = "<caret>";
    @NonNls protected static final String SELECTION_START_MARKER = "<selection>";
    @NonNls protected static final String SELECTION_END_MARKER = "</selection>";

    private static final String TEST_COMMAND = "RubyTestCommand";

    private FileEditorManager fileEditorManager;
    protected Editor myEditor;
    protected PsiFile myFile;
    protected String newDocumentText;
    protected List<String> myData;


    public BaseEditorActionTestCase(String path) {
        super(path);
    }

    private void prepareEditor(String documentText) {
        final IntRef selStartOffset = new IntRef(documentText.indexOf(SELECTION_START_MARKER));
        final IntRef selEndOffset = new IntRef(documentText.indexOf(SELECTION_END_MARKER));
        final IntRef caretOffset = new IntRef(documentText.indexOf(CARET_MARKER));

        if (caretOffset.get()>=0){
            documentText = TestUtil.removeSubstring(documentText, caretOffset.get(), CARET_MARKER.length(), caretOffset, selStartOffset, selEndOffset);
        }

        if (selStartOffset.get()>=0){
            documentText = TestUtil.removeSubstring(documentText, selStartOffset.get(), SELECTION_START_MARKER.length(), caretOffset, selStartOffset, selEndOffset);
        }

        if (selEndOffset.get()>=0){
            documentText = TestUtil.removeSubstring(documentText, selEndOffset.get(), SELECTION_END_MARKER.length(), caretOffset, selStartOffset, selEndOffset);
        }

        assert (selStartOffset.get()<=-1) || selStartOffset.get() < selEndOffset.get();

        myFile = TestUtil.createPseudoPhysicalFile(myProject, documentText);
        fileEditorManager = FileEditorManager.getInstance(myProject);
        myEditor = fileEditorManager.openTextEditor(new OpenFileDescriptor(myProject, myFile.getVirtualFile(), 0), false);

// Setting up editor
        if (caretOffset.get()>=0){
            myEditor.getCaretModel().moveToOffset(caretOffset.get());
        } else {
            myEditor.getCaretModel().moveToOffset(0);
        }
        if (selStartOffset.get()>=0 && selEndOffset.get()>=0){
            myEditor.getSelectionModel().setSelection(selStartOffset.get(), selEndOffset.get());
        }
    }

    private void releaseEditor(){
        //noinspection ConstantConditions
        fileEditorManager.closeFile(myFile.getVirtualFile());
        myEditor=null;
    }

    public String transform(List<String> data) throws Exception {
        try {
            myData=data;
            prepareEditor(myData.get(0));
            performAction();
            postProcessText();
        } finally {
            releaseEditor();
        }
        return newDocumentText;
    }

    protected void postProcessText() {
// insert spaces in line up to carret
        Runnable insertWhitespaces = new Runnable() {
            public void run() {
                final LogicalPosition pos = myEditor.getCaretModel().getLogicalPosition();
                EditorUtil.fillVirtualSpaceUntil(myEditor, 0, pos.line);
                myEditor.getCaretModel().moveToLogicalPosition(pos);
            }
        };
        performAction(myProject, insertWhitespaces);

        newDocumentText = myEditor.getDocument().getText();
        insertEditorInfo();
    }

    protected void insertEditorInfo() {
        final IntRef caretOffset = new IntRef(myEditor.getCaretModel().getOffset());
        final IntRef selStartOffset = new IntRef(myEditor.getSelectionModel().getSelectionEnd());
        final IntRef selEndOffset = new IntRef(myEditor.getSelectionModel().getSelectionStart());

        assert selStartOffset.get()<=selEndOffset.get();

        if (selStartOffset.get() < selEndOffset.get()){
            newDocumentText = TestUtil.insertSubstring(newDocumentText, SELECTION_START_MARKER, selStartOffset.get(), SELECTION_START_MARKER.length(), caretOffset, selStartOffset, selEndOffset);
            newDocumentText = TestUtil.insertSubstring(newDocumentText, SELECTION_END_MARKER, selEndOffset.get(), SELECTION_END_MARKER.length(), caretOffset, selStartOffset, selEndOffset);
        }

        newDocumentText = TestUtil.insertSubstring(newDocumentText, CARET_MARKER, caretOffset.get(), CARET_MARKER.length(), caretOffset, selStartOffset, selEndOffset);
    }

    /**
     * To be implemented!!!
     */
    protected abstract void performAction();

    /**
     * Perfoms an action as write action
     * @param project Project
     * @param action Runnable to be executed
     */
    public static void performAction(final Project project, final Runnable action){
        runAsWriteAction(new Runnable(){
            public void run(){
                CommandProcessor.getInstance().executeCommand(project, action, TEST_COMMAND, null);
            }
        });
    }
    protected void performAction(final Runnable action){
        BaseEditorActionTestCase.performAction(myProject, action);
    }

    /**
     * Runs an action as write action
     * @param runnable action to be executed
     */
    public static void runAsWriteAction(final Runnable runnable) {
        ApplicationManager.getApplication().runWriteAction(runnable);
    }

    public class RubyDataContext implements DataContext {
        @Nullable
        public Object getData(@NonNls String dataId) {
            if (DataKeys.LANGUAGE.getName().equals(dataId)) {
                return myFile.getLanguage();
            }
            if (DataKeys.PROJECT.getName().equals(dataId)) {
                return myFile.getProject();
            }

            throw new IllegalArgumentException("Data not supported: " + dataId);
        }
    }

    public class RHTMLDataContext implements DataContext {
        @Nullable
        public Object getData(@NonNls String dataId) {
            if (DataKeys.LANGUAGE.getName().equals(dataId)) {
                return eRubyLanguage.INSTANCE;
            }
            if (DataKeys.PROJECT.getName().equals(dataId)) {
                return myFile.getProject();
            }

            throw new IllegalArgumentException("Data not supported: " + dataId);
        }
    }
}
