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

package org.jetbrains.plugins.ruby.ruby.ri;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 09.09.2006
 */
public class RubyDocManager implements ProjectComponent {
    private static final String NAME = RBundle.message("module.common.rubydoc");

    private ToolWindow myToolWindow = null;
    private RDocPanel myPanel = null;
    private HashSet<Module> rModules = new HashSet<Module>();

    private Project myProject;
    private RDocSettings mySettings;

    public RubyDocManager(@NotNull final Project project, @NotNull final RDocSettings settings) {
        myProject = project;
        mySettings = settings;
    }

    public static RubyDocManager getInstance(@NotNull final Project project){
        return project.getComponent(RubyDocManager.class);
    }

    @Override
	public void projectOpened() {
    }

    private void addIfNeed() {
        if (rModules.isEmpty()) {
            destroyToolWindow();
        } else {
            addToolWindow();
        }
    }

    @Override
	public void projectClosed() {
        destroyToolWindow();
    }


    private void addToolWindow() {
        final Runnable myRunnable = new Runnable() {
            @Override
			public void run() {
                createToolWindow();
            }
        };
        if (myProject.isInitialized()) {
            myRunnable.run();
        } else {
            StartupManager.getInstance(myProject).registerPostStartupActivity(myRunnable);
        }
    }

    @Override
	@NotNull
    public String getComponentName() {
        return RComponents.RUBY_DOC_MANAGER;
    }

    @Override
	public void initComponent() {
    }

    @Override
	public void disposeComponent() {
    }

    private void createToolWindow() {
        if (myToolWindow==null) {
            myPanel = new RDocPanel(myProject, mySettings);
            myToolWindow = ToolWindowManager.getInstance(myProject).registerToolWindow(NAME, myPanel.getPanel(), ToolWindowAnchor.BOTTOM);
            myToolWindow.setIcon(RubyIcons.RI_ICON);
        }
    }

    private void destroyToolWindow() {
        ToolWindowManager manager = ToolWindowManager.getInstance(myProject);
        if (manager.getToolWindow(NAME) != null) {
            manager.unregisterToolWindow(NAME);
            myToolWindow = null;
            myPanel = null;
        }
    }

    public RDocSettings getSettings() {
        return mySettings;
    }

    /**
     * @return Name of registered RDOC window
     */
    @NotNull
    @NonNls
    public static String getName() {
        return NAME;
    }

    /**
     * Shows help on given name.
     *
     * @param name Text to search
     */
    public void showHelp(@NotNull final String name) {
        myPanel.showHelp(name);
    }

    /**
     * @return true if Current project SDK is Ruby SDK and RI can be found.
     */
    public boolean canSearch() {
// if ToolWindow and RDocPanel were not initialized, it means nowhere to show help!
        if (myToolWindow == null){
            return false;
        }

// Check the ability of project jdk to get ri help
        return RIUtil.checkIfRiExists(myPanel.getSdk());
    }

    public void addRModule(@NotNull final Module module){
        rModules.add(module);
        addIfNeed();
    }

    public void removeRModule(@NotNull final Module module){
        if (rModules.contains(module)) {
            rModules.remove(module);
        }
        addIfNeed();
    }
}
