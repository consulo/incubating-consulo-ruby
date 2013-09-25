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

package org.jetbrains.plugins.ruby.addins.rspec;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 12, 2008
 */
@State(
        name = "RSpecApplicationSettings",
        storages = {
        @Storage(
                id = "main",
                file = "$APP_CONFIG$/rubysettings.xml"
        )}
)
public class RSpecApplicationSettings  implements PersistentStateComponent<RSpecApplicationSettings> {
    public boolean wizardRubyShouldUseRSpecFramework = false;
    public boolean wizardRubyShouldUseTestUnitFramework = true;

    public boolean wizardRailsFacetIsRSpecEnabled = false;
    public boolean wizardRailsFacetIsRSpecRailsEnabled = false;
    public SrcType wizardRailsFacetRSpecPluginSrcType = SrcType.LATEST;
    @NotNull
    public String wizardRailsFacetRSpecArgs = "";
    @NotNull
    public String wizardRailsFacetRSpecRailsArgs = "";


    public static RSpecApplicationSettings getInstance() {
        return ServiceManager.getService(RSpecApplicationSettings.class);
    }

    @Override
	public RSpecApplicationSettings getState() {
        return this;
    }

    @Override
	public void loadState(@NotNull final RSpecApplicationSettings settings) {
        //tests
        wizardRubyShouldUseRSpecFramework = settings.wizardRubyShouldUseRSpecFramework;
        wizardRubyShouldUseTestUnitFramework = settings.wizardRubyShouldUseTestUnitFramework;

        wizardRailsFacetIsRSpecEnabled = settings.wizardRailsFacetIsRSpecEnabled;
        wizardRailsFacetIsRSpecRailsEnabled = settings.wizardRailsFacetIsRSpecRailsEnabled;

        wizardRailsFacetRSpecPluginSrcType = settings.wizardRailsFacetRSpecPluginSrcType;

        wizardRailsFacetRSpecArgs = settings.wizardRailsFacetRSpecArgs;
        wizardRailsFacetRSpecRailsArgs = settings.wizardRailsFacetRSpecRailsArgs;
    }

    public enum SrcType {
        TRUNK,
        LATEST,
        SPECIFIC
    }
}
