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
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.special.GenerateControllerAction;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 25.11.2006
 */
public class RailsGenerateControllerActionTest  extends IdeaTestCase {
    private GenerateControllerAction myGenerateControllerAction;

    protected void setUp() throws Exception {
        myGenerateControllerAction = new GenerateControllerAction();
        super.setUp();
    }
    
    public void testRailsGenerateModelAction() {
        myGenerateControllerAction = new GenerateControllerAction();
        assertEquals(RBundle.message("new.generate.controller.text"),
                     myGenerateControllerAction.getTemplatePresentation().getText());
        assertEquals(RBundle.message("new.generate.controller.description"),
                     myGenerateControllerAction.getTemplatePresentation().getDescription());
        assertEquals(RailsIcons.RAILS_CONTROLLER_NODE,
                     myGenerateControllerAction.getTemplatePresentation().getIcon());
    }

    public void testRailsGenerateModelAction_Text() {
        myGenerateControllerAction = new GenerateControllerAction("test", GenerateControllerAction.GENERATOR_CONTROLLER);
        assertEquals("test",
                     myGenerateControllerAction.getTemplatePresentation().getText());
        assertEquals(RBundle.message("new.generate.controller.description"),
                     myGenerateControllerAction.getTemplatePresentation().getDescription());
        assertEquals(RailsIcons.RAILS_CONTROLLER_NODE,
                     myGenerateControllerAction.getTemplatePresentation().getIcon());
    }

   /* public void testCheckBeforeCreate() throws IncorrectOperationException {
        myRailsGenerateControllerAction.checkBeforeCreate("My", null);
        myRailsGenerateControllerAction.checkBeforeCreate("MyAdmin", null);
        AssertionUtil.assertException(new IncorrectOperationExceptionCase() {
            public void tryToInovke() throws Exception {
                myRailsGenerateControllerAction.checkBeforeCreate("#y", null);
            }
        });
        AssertionUtil.assertException(new IncorrectOperationExceptionCase() {
            public void tryToInovke() throws Exception {
                myRailsGenerateControllerAction.checkBeforeCreate("_y", null);
            }
        });
        AssertionUtil.assertException(new IncorrectOperationExceptionCase() {
            public void tryToInovke() throws Exception {
                myRailsGenerateControllerAction.checkBeforeCreate("my", null);
            }
        });
        AssertionUtil.assertException(new IncorrectOperationExceptionCase() {
            public void tryToInovke() throws Exception {
                myRailsGenerateControllerAction.checkBeforeCreate("my_admin", null);
            }
        });
    }

    public void testGetErrorTitle() {
        assertEquals(RBundle.message("new.generate.controller.error"),
                     myRailsGenerateControllerAction.getErrorTitle());
    }

    public void testGetCommandName() {
        assertEquals(RBundle.message("new.generate.controller.action.command.name"),
                     myRailsGenerateControllerAction.getCommandName());
    }

    public void testGetActionName() {
        assertEquals(RBundle.message("new.generate.controller.action.name",
                                     null, "_newName"),
                     myRailsGenerateControllerAction.getActionName(null, "_newName"));
    }

    public void testCreate() throws Exception {
        final PsiElement[] psiElements =
                myRailsGenerateControllerAction.create("name", null);
        assertNotNull(psiElements);
        assertEquals(0, psiElements.length);
    }

    public void testInvokeDialog() {
        AssertionUtil.assertException(new RuntimeExceptionCase() {
            public void tryToInovke() throws Exception {
                myRailsGenerateControllerAction.invokeDialog(myProject, null);
            }
        }, RBundle.message("new.generate.controller.action.prompt"));
    }*/
}
