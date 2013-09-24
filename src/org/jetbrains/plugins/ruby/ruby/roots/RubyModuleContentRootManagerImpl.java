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

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.roots.impl.storage.ClasspathStorage;
import com.intellij.openapi.util.Disposer;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RComponents;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 19, 2007
 */
@State(
  name = "RModuleContentRootManager",
  storages = {
    @Storage(
      id = ClasspathStorage.DEFAULT_STORAGE,
      file = "$MODULE_FILE$"
    )
  }
)


public class RubyModuleContentRootManagerImpl implements RModuleContentRootManager, ModuleComponent, PersistentStateComponent<Element>, RubyModuleContentRootManager {
    protected static final String URL_ATTR = "url";
    private static final String TEST_URLS = "TEST_URLS";

    protected Set<String> testUnitRootsUrls = Collections.synchronizedSet(new HashSet<String>());
    private final LinkedList<RModuleContentRootsListener> myListeners = new LinkedList<RModuleContentRootsListener>();

    public static RubyModuleContentRootManagerImpl getInstance(@NotNull final Module module) {
        return module.getComponent(RubyModuleContentRootManagerImpl.class);
    }

    public void projectOpened() {
        //Do nothing
    }

    public void projectClosed() {
        myListeners.clear();
    }

    public void moduleAdded() {
        // Do nothing
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return RComponents.RUBY_MODULE_ROOT_MANAGER;
    }

    public void initComponent() {
        //Do nothing
    }

    public void disposeComponent() {
        //Do nothing
    }

    public void readExternal(final Element element) {
        final List list = element.getChildren(TEST_URLS);
        for (Object o : list) {
            testUnitRootsUrls.add(((Element)o).getAttribute(URL_ATTR).getValue());
        }
    }

    public void writeExternal(final Element element) {
        for (String testUrl : testUnitRootsUrls) {
            final Element child = new Element(TEST_URLS);
            child.setAttribute(URL_ATTR, testUrl);

            element.addContent(child);
        }
    }

    public void setTestUnitFolderUrls(@NotNull final List<String> urls) {
        testUnitRootsUrls.clear();
        fireRemoveUnitTestFolders();
        testUnitRootsUrls.addAll(urls);
        fireTestUnitFoldersAdded(urls);
    }

    @NotNull
    public Set<String> getTestUnitFolderUrls() {
        return Collections.unmodifiableSet(testUnitRootsUrls);
    }

    private void fireTestUnitFoldersAdded(final List<String> urls) {
        for (RModuleContentRootsListener listener : myListeners) {
            listener.testUntiFoldersAdded(urls);
        }
    }

    private void fireRemoveUnitTestFolders() {
        for (RModuleContentRootsListener listener : myListeners) {
            listener.removeTestUnitFolders();
        }
    }

    public void addContentRootsListener(@NotNull final RModuleContentRootsListener l,
                                        @NotNull final Disposable parentDisposable) {
        Disposer.register(parentDisposable, new Disposable() {
            public void dispose() {
                myListeners.remove(l);
            }
        });
        myListeners.add(l);
    }

    public Element getState() {
        final Element e = new Element("state");
        writeExternal(e);
        return e;
    }

    public void loadState(final Element element) {
        readExternal(element);
    }
}
