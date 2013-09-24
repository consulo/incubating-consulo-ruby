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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.builtin;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.nameConventions.MailersConventions;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.FileSymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.InterpretationMode;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.RailsRequireUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.StubsUrls;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.SymbolCacheUtil;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkType;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Oct 8, 2007
 */
public class RailsBuiltInCachedSymbol extends BuiltInCachedSymbol {
    private static final Logger LOG = Logger.getInstance(RailsBuiltInCachedSymbol.class.getName());

    public RailsBuiltInCachedSymbol(@NotNull final Project project,
                                    @NotNull final String url,
                                    @Nullable final Sdk sdk) {
        super(project, url, sdk);
    }

    public void fileAdded(@NotNull final String url) {
        super.fileAdded(url);
        if (mySdk == null){
            return;
        }
        // We recreate cache if something added to gems
        final List<String> gemsRootUrls = RubySdkType.getGemsRootUrls(mySdk);
        if (gemsRootUrls != null) {
            for (String gemsRootUrl : gemsRootUrls) {
                if (url.startsWith(gemsRootUrl)){
                    myFileSymbol = null;
                    return;
                }
            }
        }
    }

    protected void addAdditionalData() {
        super.addAdditionalData();

// Core extensions
        RailsRequireUtil.loadCoreExtentions(myFileSymbol, InterpretationMode.FULL);

// Active record
        RailsRequireUtil.loadDBAdapters(myFileSymbol, InterpretationMode.FULL);

// Action View
        RailsRequireUtil.loadBuiltInHelpers(myFileSymbol, InterpretationMode.FULL);

// Action Mailer
        MailersConventions.loadBuiltInGems(myFileSymbol, InterpretationMode.FULL);

// Load addon file for web service
        final String url = SymbolCacheUtil.getStubUrl(mySdk, StubsUrls.ACTIVE_SUPPORT_ACTION_MAILER_ADDON_RB);
        LOG.assertTrue(url!=null);
        FileSymbolUtil.process(myFileSymbol, url, InterpretationMode.FULL, false);
    }
}
