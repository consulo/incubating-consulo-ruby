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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache;

import com.intellij.openapi.projectRoots.ProjectJdk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Oct 8, 2007
 */
public class SymbolCacheUtil {

    @Nullable
    public static String getStubUrlByType(@NotNull final FileSymbolType type,
                                          @Nullable final ProjectJdk sdk, 
                                          final boolean jrubyEnabled) {
        if (type == FileSymbolType.BUILT_IN){
            return getStubUrl(sdk, jrubyEnabled ? StubsUrls.JRUBY_BUILT_IN_RB : StubsUrls.BUILT_IN_RB);
        } else
        if (type == FileSymbolType.RAILS_BUILT_IN){
            return getStubUrl(sdk, jrubyEnabled ? StubsUrls.JRUBY_FULL_RAILS_RB : StubsUrls.FULL_RAILS_RB);
        }
        return null;
    }

    @Nullable
    public static String getStubUrl(@Nullable final ProjectJdk sdk, @NotNull final String name){
        if (RubySdkUtil.isKindOfRubySDK(sdk)) {
            //noinspection ConstantConditions
            final String rubyStubsDirUrl = RubySdkUtil.getRubyStubsDirUrl(sdk);
            if (rubyStubsDirUrl != null) {
                return rubyStubsDirUrl + name;
            }
        }
        return null;
    }

    @Nullable
    public static FileSymbol getFileSymbol(@Nullable final CachedSymbol cachedSymbol) {
        return cachedSymbol != null ? cachedSymbol.getUp2DateSymbol() : null;
    }

}
