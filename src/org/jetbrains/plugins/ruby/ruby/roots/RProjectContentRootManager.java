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

package org.jetbrains.plugins.ruby.ruby.roots;

import com.intellij.ProjectTopics;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.ruby.module.RubyModuleListenerAdapter;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 19, 2007
 */
public class RProjectContentRootManager implements ProjectComponent, Disposable {
    final private List<Pair<String, Module>> myTestUniUrlAndModuleList = Collections.synchronizedList(new ArrayList<Pair<String, Module>>());

    public RProjectContentRootManager(final Project project) {
        // Listener for files in RubyFileCache
        // Investigate memory leak on project close.
        project.getMessageBus().connect(this).subscribe(ProjectTopics.MODULES, new RubyModuleListenerAdapter() {
            public void moduleAdded(final Project project, final Module module) {
                if (!RModuleUtil.hasRubySupport(module)) {
                    return;
                }
                processModuleAdded(module);
            }

            public void moduleRemoved(Project project, Module module) {
                if (!RModuleUtil.hasRubySupport(module)) {
                    return;
                }
                removeAllTestUnitFolderForModule(module);
            }
        });
    }

    public static RProjectContentRootManager getInstance(@NotNull final Project project) {
        return project.getComponent(RProjectContentRootManager.class);
    }

    public void projectOpened() {
        // Do nothing
    }

    public void projectClosed() {
        myTestUniUrlAndModuleList.clear();
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return RComponents.RPROJECT_ROOT_MANAGER;
    }

    public void initComponent() {
    }

    public void disposeComponent() {
        Disposer.dispose(this);
    }

    public void dispose() {
    }

    private void addTestUnitRoots(@NotNull final Module module, @NotNull final List<String> urls) {
        for (String url : urls) {
            myTestUniUrlAndModuleList.add(new Pair<String, Module>(url, module));
        }
    }

    /**
     * @param url Url
     * @return True if url is test root or belongs to test root content.
     */
    public boolean isUnderTestUnitRoot(@NotNull final String url) {
        for (Pair<String, Module> pair : myTestUniUrlAndModuleList) {
            if (url.startsWith(pair.first)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param url Url
     * @return True if url is test root.
     */
    public boolean isTestUnitRoot(@NotNull final String url) {
        for (Pair<String, Module> pair : myTestUniUrlAndModuleList) {
            if (url.equals(pair.first)) {
                return true;
            }
        }
        return false;
    }

    // Implementation ins't fast, because it isn't necessary.
    // Add/remove TestFolder executes in GUI dialog. Also every
    // project has few modules and test roots.
    private void removeAllTestUnitFolderForModule(@NotNull final Module module) {
        final List<Pair<String, Module>> toRemove = new ArrayList<Pair<String, Module>>();

        for (Pair<String, Module> pair : myTestUniUrlAndModuleList) {
            if (pair.second == module) {
                toRemove.add(pair);
            }
        }
        myTestUniUrlAndModuleList.removeAll(toRemove);
    }

    protected void processModuleAdded(@NotNull final Module module) {
        final RModuleContentRootManager manager = RModuleUtil.getModuleContentManager(module);

        addTestUnitRoots(module, new ArrayList<String>(manager.getTestUnitFolderUrls()));
        manager.addContentRootsListener(new RModuleContentRootsListener() {
            public void testUntiFoldersAdded(@NotNull List<String> urls) {
                addTestUnitRoots(module, urls);
            }

            public void removeTestUnitFolders() {
                removeAllTestUnitFolderForModule(module);
            }
        }, this);
    }
}
