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

package org.jetbrains.plugins.ruby.rails.facet.versions;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfiguration;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Mar 13, 2008
 */

/**
 * Package private. You should use general class - BaseRailsFacet
 */
class RailsFacet extends BaseRailsFacet
{
	@NonNls
	public static final FacetTypeId<RailsFacet> ID = new FacetTypeId<RailsFacet>("RailsFacetType");

	public RailsFacet(@NotNull final FacetType facetType, @NotNull final Module module, final String name, @NotNull final BaseRailsFacetConfiguration configuration, @Nullable final Facet underlyingFacet)
	{
		super(facetType, module, name, configuration, underlyingFacet);
	}

	@Nullable
	public static RailsFacet getInstance(@NotNull final Module module)
	{
		return null;
	}

	@Override
	@NotNull
	public String getDefaultRailsApplicationHomePath(final ModifiableRootModel rootModel)
	{
		final VirtualFile contentRoot = RModuleUtil.getModulesFirstContentRoot(rootModel);
		assert contentRoot != null; //Can't be null for Rails facet in Ruby module

		return contentRoot.getPath();
	}
}
