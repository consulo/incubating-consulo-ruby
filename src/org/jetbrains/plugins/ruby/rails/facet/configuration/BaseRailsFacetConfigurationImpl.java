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

package org.jetbrains.plugins.ruby.rails.facet.configuration;

import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorsUtil;
import org.jetbrains.plugins.ruby.rails.actions.rake.RakeUtil;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTask;
import org.jetbrains.plugins.ruby.rails.facet.ui.settings.tabs.general.GeneralSettingsEditorTab;
import org.jetbrains.plugins.ruby.rails.facet.ui.settings.tabs.railsView.RailsViewFoldersEditorTab;
import org.jetbrains.plugins.ruby.rails.facet.versions.BaseRailsFacet;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Mar 13, 2008
 */
public class BaseRailsFacetConfigurationImpl implements BaseRailsFacetConfigurationLowLevel {

// Serializable in BaseRailsFacetConfigurationExternalizer
    public boolean myShouldUseRSpecPlugin;

    private String myRailsRootDirPath;

    private boolean isInitialized;

// Transient, we shouldn't serialize it!
    private String myRailsRootDirPathUrl;

    private StandardRailsPaths myRailsPaths;

    // This object is build on every RakeCommands changes. Represents logical structure of Rake commands.
    private RakeTask myRootRakeTask;

    // Generators
    private String[] myGenerators;
    private Module myModule;

    // Is being used only for RakeTasks, Generators regenerating after SDK changing
    private ProjectJdk mySdk;

    public FacetEditorTab[] createEditorTabs(final FacetEditorContext editorContext,
                                             final FacetValidatorsManager validatorsManager) {

        return new FacetEditorTab[]{
                new GeneralSettingsEditorTab((BaseRailsFacet)editorContext.getFacet()),
                new RailsViewFoldersEditorTab(editorContext.getProject(), editorContext.getModifiableRootModel())
        };
    }

    public boolean shouldUseRSpecPlugin() {
        return myShouldUseRSpecPlugin;
    }

    public void setShouldUseRSpecPlugin(final boolean useRSpecPlugin) {
        myShouldUseRSpecPlugin = useRSpecPlugin;
    }

    @NotNull
    public String getRailsApplicationRootPath() {
        return myRailsRootDirPath;
    }

    @NotNull
    public String getRailsApplicationRootPathUrl() {
        return myRailsRootDirPathUrl;
    }

    public void setRailsApplicationRootPath(@NotNull final String railsRootDirPath) {
        myRailsRootDirPath = railsRootDirPath;
        myRailsRootDirPathUrl = VirtualFileUtil.constructLocalUrl(railsRootDirPath);
        myRailsPaths = new StandardRailsPaths(railsRootDirPath);
    }

    @NotNull
    public StandardRailsPaths getPaths() {
        return myRailsPaths;
    }

    public void loadGenerators(final boolean forceRegenerate, @Nullable final ProjectJdk sdk) {
        assert  myModule != null;
        GeneratorsUtil.loadGeneratorsList(forceRegenerate, myModule.getProject(), sdk, myModule.getName(), this);
    }

    public String[] getGenerators() {
        return myGenerators;
    }


    public void setGenerators(final String[] generators) {
        myGenerators = generators;
    }

    public void loadRakeTasks(final boolean forceRegenerate, final ProjectJdk sdk) {
        RakeUtil.loadRakeTasksTree(forceRegenerate, myModule.getProject(), sdk, myModule.getName(), this);
    }

    public RakeTask getRakeTasks() {
        return myRootRakeTask;
    }

    public void setRakeTasks(final RakeTask rootTask) {
        myRootRakeTask = rootTask;
    }

    public void setModule(@NotNull final Module uncommitedModule) {
        myModule = uncommitedModule;

        final String path = BaseRailsFacetConfigurationExternalizer.getInstance().expandPathIfPossible(this, getNullableRailsApplicationRootPath());
        if (path != null) {
            setRailsApplicationRootPath(path);
        }
    }

    public Module getModule() {
        return myModule;
    }

    public void reloadGenerators() {
        loadGenerators(true, RModuleUtil.getModuleOrJRubyFacetSdk(myModule));
    }

    public void reloadRakeTasks() {
        loadRakeTasks(true, RModuleUtil.getModuleOrJRubyFacetSdk(myModule));
    }

    // Externalizing
    public void readExternal(final Element element) throws InvalidDataException {
        BaseRailsFacetConfigurationExternalizer.getInstance().readExternal(this, element);
    }

    public void writeExternal(final Element element) throws WriteExternalException {
        BaseRailsFacetConfigurationExternalizer.getInstance().writeExternal(this, element);
    }

    @Nullable
    public String getNullableRailsApplicationRootPath() {
        return myRailsRootDirPath;
    }

    /**
     * Only for BaseRailsFacet internal tasks
     * @param sdk Sdk
     */
    public void setSdk(@Nullable final ProjectJdk sdk) {
        mySdk = sdk;
    }

    /**
     * Only for BaseRailsFacet internal tasks
     * @return SDK
     */
    @Nullable
    public ProjectJdk getSdk() {
        return mySdk;
    }

    public void setInitialized() {
        isInitialized = true;
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}
