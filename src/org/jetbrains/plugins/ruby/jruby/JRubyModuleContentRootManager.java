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

package org.jetbrains.plugins.ruby.jruby;

import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacet;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacetConfiguration;
import org.jetbrains.plugins.ruby.ruby.roots.RModuleContentRootsListener;
import org.jetbrains.plugins.ruby.ruby.roots.RubyModuleContentRootManagerImpl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg, Roman Chernyatchik
 * Date: Sep 12, 2007
 */
@SuppressWarnings({"ComponentNotRegistered"})
public class JRubyModuleContentRootManager extends RubyModuleContentRootManagerImpl {
    private JRubyFacet myFacet;
    private final LinkedList<RModuleContentRootsListener> myListeners = new LinkedList<RModuleContentRootsListener>();
    private final JRubyFacetConfiguration myConfiguration;

    public static JRubyModuleContentRootManager getInstanceEx(@NotNull final Module module) {
        return module.getComponent(JRubyModuleContentRootManager.class);
    }
    
    public JRubyModuleContentRootManager(final JRubyFacet facet) {
        super();

        myFacet = facet;
        myConfiguration = myFacet.getConfiguration();

        loadData();
    }

    private void loadData() {
        setUnitTestsRootUrl(myConfiguration.getUnitTestsRootUrl());
    }

    public static JRubyModuleContentRootManager getInstance(@NotNull final Module module) {
        //noinspection ConstantConditions
        return JRubyFacet.getInstance(module).getRModuleContentManager();
    }

    @Nullable
    public String getUnitTestsRootUrl() {
        return myConfiguration.getUnitTestsRootUrl();
    }

    public void setUnitTestsRootUrl(@Nullable final String url) {
        //TODO replace this UGLY stub
        final ArrayList<String> urlsList = new ArrayList<String>(2);
        if (url != null) {
            urlsList.add(url);
        }
        setTestUnitFolderUrls(urlsList);
    }

    public void setTestUnitFolderUrls(@NotNull final List<String> urls) {
        assert urls.size() <= 1;

        myConfiguration.setUnitTestsRootUrl(urls.isEmpty() ? null : urls.get(0));
        super.setTestUnitFolderUrls(urls);
    }    
}
