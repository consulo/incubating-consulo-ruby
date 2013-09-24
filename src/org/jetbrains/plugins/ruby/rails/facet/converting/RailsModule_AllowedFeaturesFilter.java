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

package org.jetbrains.plugins.ruby.rails.facet.converting;

import com.intellij.ide.impl.convert.AllowedFeaturesFilter;
import com.intellij.facet.FacetTypeId;
import org.jetbrains.plugins.ruby.rails.facet.versions.BaseRailsFacet;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jun 9, 2008
 */

/**
 * For projects in old format
 */
public class RailsModule_AllowedFeaturesFilter implements AllowedFeaturesFilter {
    public boolean isFacetAdditionEnabled(final FacetTypeId<?> facetType) {
        return !isRails(facetType);
    }

    public boolean isFacetDeletionEnabled(final FacetTypeId<?> facetType) {
        return !isRails(facetType);
    }

    private static boolean isRails(final FacetTypeId<?> facetTypeId) {
        return BaseRailsFacet.getRailsFacetID().equals(facetTypeId);
    }
}
