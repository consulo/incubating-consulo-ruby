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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
*
* @author: oleg
* @date: Oct 7, 2007
*/
public class CacheKey {

    private final String url;
    private final Module module;
    private final Sdk sdk;
    private FileSymbolType type;
    private boolean isJRubyEnabled;

    public CacheKey(@NotNull final FileSymbolType type,
                    @Nullable final String url,
                    @Nullable final Module module,
                    @Nullable final Sdk sdk,
                    final boolean isJRubyEnabled) {
        this.type = type;
        this.url = url;
        this.module = module;
        this.sdk = sdk;
        this.isJRubyEnabled = isJRubyEnabled;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Do not modify! Generated automatically by IDEA! //////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings({"RedundantIfStatement"})
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheKey)) return false;

        final CacheKey cacheKey = (CacheKey) o;

        if (isJRubyEnabled != cacheKey.isJRubyEnabled) return false;
        if (module != null ? !module.equals(cacheKey.module) : cacheKey.module != null) return false;
        if (sdk != null ? !sdk.equals(cacheKey.sdk) : cacheKey.sdk != null) return false;
        if (type != cacheKey.type) return false;
        if (url != null ? !url.equals(cacheKey.url) : cacheKey.url != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (url != null ? url.hashCode() : 0);
        result = 31 * result + (module != null ? module.hashCode() : 0);
        result = 31 * result + (sdk != null ? sdk.hashCode() : 0);
        result = 31 * result + type.hashCode();
        result = 31 * result + (isJRubyEnabled ? 1 : 0);
        return result;
    }
}
