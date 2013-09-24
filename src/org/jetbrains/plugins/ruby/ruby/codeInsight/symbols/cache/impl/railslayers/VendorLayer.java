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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.railslayers;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.RailsRequireUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.CachedSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.FileSymbolType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.AbstractCachedSymbol;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Oct 20, 2007
 */

public class VendorLayer extends AbstractRailsLayeredCachedSymbol {

    public VendorLayer(@NotNull final Project project,
                       @Nullable final Module module,
                       @Nullable final Sdk sdk,
                       final boolean isJRubyEnabled) {
        super(FileSymbolType.RAILS_MODULE_LAYER, project, module, sdk, isJRubyEnabled);
    }

    public void fileAdded(@NotNull String url) {
        final CachedSymbol baseCachedSymbol = getBaseSymbol();
        if (baseCachedSymbol != null) {
            ((AbstractCachedSymbol) baseCachedSymbol).fileAdded(url);
            if (!baseCachedSymbol.isUp2Date()) {
                myFileSymbol = null;
            }
        }
    }

    protected void addAdditionalData() {
        RailsRequireUtil.loadAllVendorsLibsWithoutEdgeRails(myFileSymbol, myModule);
    }

}