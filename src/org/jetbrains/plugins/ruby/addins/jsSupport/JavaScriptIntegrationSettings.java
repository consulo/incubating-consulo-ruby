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

package org.jetbrains.plugins.ruby.addins.jsSupport;

import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.settings.RApplicationSettings;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Nov 6, 2007
 */
@SuppressWarnings({"ComponentNotRegistered"})
public class JavaScriptIntegrationSettings implements ApplicationComponent {

    //We need dependency
    @SuppressWarnings({"UnusedDeclaration", "UnusedParameters"})
    public JavaScriptIntegrationSettings(final RApplicationSettings applicationSettings) {
    }

    @Override
	public void initComponent() {
        RApplicationSettings.getInstance().setJsSupportEnabled(true);
    }

    @Override
	public void disposeComponent() {
        //Do nothing
    }

    @Override
	@NotNull
    public String getComponentName() {
        return "RubyJavaScriptIntegrationSettings";
    }
}
