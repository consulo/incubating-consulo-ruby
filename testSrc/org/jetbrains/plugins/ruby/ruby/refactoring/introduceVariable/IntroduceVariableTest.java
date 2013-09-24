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

package org.jetbrains.plugins.ruby.ruby.refactoring.introduceVariable;

import com.intellij.refactoring.RefactoringActionHandler;
import junit.framework.Test;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.actions.BaseEditorActionTestCase;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import org.jetbrains.plugins.ruby.support.TestUtil;
import rb.refactoring.introduceVariable.RubyIntroduceVariableHandler;
import rb.refactoring.introduceVariable.RubyIntroduceVariableHandlerWrapper;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 23, 2007
 */
@SuppressWarnings({"ConstantConditions"})
public class IntroduceVariableTest extends BaseEditorActionTestCase{
    @NonNls
    private static final String DATA_PATH = PathUtil.getDataPath(IntroduceVariableTest.class);

    public IntroduceVariableTest() {
        super(DATA_PATH);
    }

    protected void setUp() {
        super.setUp();
        TestUtil.loadJRubySupport();
    }

    protected void performAction() {
        performIntroduceVariableAction();
    }

    private void performIntroduceVariableAction() {
        final RefactoringActionHandler handlerWrapper =
                RubyLanguage.RUBY.getRefactoringSupportProvider().getIntroduceVariableHandler();
        final RubyIntroduceVariableHandler jrubyHandler = ((RubyIntroduceVariableHandlerWrapper) handlerWrapper).getJRubyHandler();
        jrubyHandler.introduceVariable(myProject, myEditor, myFile, new RubyDataContext(), myData.get(1), "replace_all".equals(myData.get(2).trim()));
    }

    public static Test suite(){
        return new IntroduceVariableTest();
    }

    protected void insertEditorInfo() {
        // we don`t want to insert any editor info here
    }
}