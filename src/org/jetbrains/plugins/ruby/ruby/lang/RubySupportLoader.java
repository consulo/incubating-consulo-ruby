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

package org.jetbrains.plugins.ruby.ruby.lang;

import com.intellij.codeInsight.hint.EditorFragmentComponent;
import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.ActionRunner;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.ruby.actions.editor.RubyEditorActionsManager;
import org.jetbrains.plugins.ruby.ruby.inspections.ducktype.RubyDuckTypeInspection;
import org.jetbrains.plugins.ruby.ruby.inspections.resolve.RubyResolveInspection;
import org.jetbrains.plugins.ruby.ruby.inspections.scopes.RubyScopesInspection;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RArgumentList;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RClassName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RMethodName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RModuleName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RSuperClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.support.utils.IdeaInternalUtil;


public class RubySupportLoader implements ApplicationComponent, InspectionToolProvider {

    public static void loadRuby() {
        IdeaInternalUtil.runInsideWriteAction(new ActionRunner.InterruptibleRunnable() {
            public void run() throws Exception {
// Registering Ruby editor actions
                RubyEditorActionsManager.registerRubyEditorActions();

// Register context info handler
                registerContextInfoHandler();
            }
        });
    }

    private static void registerContextInfoHandler() {
        EditorFragmentComponent.setDeclarationHandler(
                RContainer.class, new EditorFragmentComponent.DeclarationRangeHandler() {

            @NotNull
            public TextRange getDeclarationRange(@NotNull final PsiElement container) {
                final TextRange containerRange = container.getTextRange();
                int start = containerRange.getStartOffset();
                int end = start;
                TextRange range = null;

                if (container instanceof RClass) {

                    final RSuperClass superClass = ((RClass) container).getPsiSuperClass();
                    if (superClass != null) {
                        end = superClass.getTextRange().getEndOffset();
                    } else {
                        final RClassName className = ((RClass) container).getClassName();
                        if (className != null) {
                            end = className.getTextRange().getEndOffset();
                        }
                    }
                    return new TextRange(start, end);
                } else if (container instanceof RMethod) {
                    final RArgumentList argList = ((RMethod) container).getArgumentList();
                    if (argList != null) {
                        end = argList.getTextRange().getEndOffset();
                    } else {
                        final RMethodName methodName = ((RMethod) container).getMethodName();
                        if (methodName != null) {
                            end = methodName.getTextRange().getEndOffset();
                        }
                    }
                    return new TextRange(start, end);
                } else if (container instanceof RModule) {
                    final RModuleName moduleName = ((RModule) container).getModuleName();
                    if (moduleName != null) {
                        range = moduleName.getTextRange();
                    }
                }

                if (range == null) {
                    range = containerRange;
                }

                return new TextRange(range.getStartOffset(), range.getEndOffset());
            }
        }
        );
    }

    @NotNull
    @NonNls
    public String getComponentName() {
        return RComponents.RUBY_SUPPORT_LOADER;
    }

    public void initComponent() {
        loadRuby();
    }


    public void disposeComponent() {
        // do nothing
    }

    public Class[] getInspectionClasses() {
        return new Class[]{
                RubyDuckTypeInspection.class,
                RubyResolveInspection.class,
                RubyScopesInspection.class
        };
    }
}
