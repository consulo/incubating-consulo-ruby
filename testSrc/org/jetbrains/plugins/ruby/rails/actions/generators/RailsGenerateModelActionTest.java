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

package org.jetbrains.plugins.ruby.rails.actions.generators;

import com.intellij.testFramework.IdeaTestCase;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.special.GenerateModelAction;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 25.11.2006
 */
public class  RailsGenerateModelActionTest  extends IdeaTestCase {
    private GenerateModelAction myGenerateModelAction;

    protected void setUp() throws Exception {
        myGenerateModelAction = new GenerateModelAction();
        super.setUp();
    }

    public void  testRailsGenerateModelAction() {
        myGenerateModelAction = new GenerateModelAction();
        assertEquals(RBundle.message("new.generate.model.text"),
                     myGenerateModelAction.getTemplatePresentation().getText());
        assertEquals(RBundle.message("new.generate.model.description"),
                     myGenerateModelAction.getTemplatePresentation().getDescription());
        assertEquals(RailsIcons.RAILS_MODEL_NODE,
                     myGenerateModelAction.getTemplatePresentation().getIcon());
    }

    /*public void testRailsGenerateModelAction_Text() {
        myRailsGenerateModelAction = new RailsGenerateModelAction("test");
        assertEquals("test",
                     myRailsGenerateModelAction.getTemplatePresentation().getText());
        assertEquals(RBundle.message("new.menu.generate.model.description"),
                     myRailsGenerateModelAction.getTemplatePresentation().getDefinition());
        assertEquals(RIconsUtils.RAILS_MODEL_NODE,
                     myRailsGenerateModelAction.getTemplatePresentation().getIcon());
    }

    public void testCheckBeforeCreate() throws IncorrectOperationException {
        myRailsGenerateModelAction.checkBeforeCreate("My", null);
        myRailsGenerateModelAction.checkBeforeCreate("MyAdmin", null);
        AssertionUtil.assertException(new IncorrectOperationExceptionCase() {
            public void tryToInovke() throws Exception {
                myRailsGenerateModelAction.checkBeforeCreate("#y", null);
            }
        });
        AssertionUtil.assertException(new IncorrectOperationExceptionCase() {
            public void tryToInovke() throws Exception {
                myRailsGenerateModelAction.checkBeforeCreate("_y", null);
            }
        });
        AssertionUtil.assertException(new IncorrectOperationExceptionCase() {
            public void tryToInovke() throws Exception {
                myRailsGenerateModelAction.checkBeforeCreate("my", null);
            }
        });
        AssertionUtil.assertException(new IncorrectOperationExceptionCase() {
            public void tryToInovke() throws Exception {
                myRailsGenerateModelAction.checkBeforeCreate("my_admin", null);
            }
        });
    }

    public void testGetErrorTitle() {
        assertEquals(RBundle.message("new.generate.model.error"),
                     myRailsGenerateModelAction.getErrorTitle());
    }
*/
/*
    public void testGetCommandName() {
        assertEquals(RBundle.message("new.generate.model.action.command.name"),
                     myRailsGenerateModelAction.getCommandName());
    }
*/

  /*  public void testGetActionName() {
        assertEquals(RBundle.message("new.generate.model.action.name",
                                     null, "_newName"),
                     myRailsGenerateModelAction.getActionName(null, "_newName"));
    }*/

   /* public void testCreate() throws Exception {
        final PsiElement[] psiElements =
                myRailsGenerateModelAction.create("name", null);
        assertNotNull(psiElements);
        assertEquals(0, psiElements.length);
    }*/

   /* public void testInvokeDialog() {
        AssertionUtil.assertException(new RuntimeExceptionCase() {
            public void tryToInovke() throws Exception {
                myRailsGenerateModelAction.invokeDialog(myProject, null);
            }
        }, RBundle.message("new.generate.model.action.prompt"));
    }*/
}
