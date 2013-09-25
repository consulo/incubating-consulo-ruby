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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.ruby.actions.editor.RubyEditorActionsManager;
import org.jetbrains.plugins.ruby.ruby.inspections.ducktype.RubyDuckTypeInspection;
import org.jetbrains.plugins.ruby.ruby.inspections.resolve.RubyResolveInspection;
import org.jetbrains.plugins.ruby.ruby.inspections.scopes.RubyScopesInspection;
import org.jetbrains.plugins.ruby.support.utils.IdeaInternalUtil;
import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.util.ActionRunner;


public class RubySupportLoader implements ApplicationComponent, InspectionToolProvider {

    public static void loadRuby() {
        IdeaInternalUtil.runInsideWriteAction(new ActionRunner.InterruptibleRunnable() {
            @Override
			public void run() throws Exception {
// Registering Ruby editor actions
                RubyEditorActionsManager.registerRubyEditorActions();

            }
        });
    }


    @Override
	@NotNull
    @NonNls
    public String getComponentName() {
        return RComponents.RUBY_SUPPORT_LOADER;
    }

    @Override
	public void initComponent() {
        loadRuby();
    }


    @Override
	public void disposeComponent() {
        // do nothing
    }

    @Override
	public Class[] getInspectionClasses() {
        return new Class[]{
                RubyDuckTypeInspection.class,
                RubyResolveInspection.class,
                RubyScopesInspection.class
        };
    }
}
