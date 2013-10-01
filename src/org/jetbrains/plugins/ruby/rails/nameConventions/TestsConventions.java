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

package org.jetbrains.plugins.ruby.rails.nameConventions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.StandardRailsPaths;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import com.intellij.openapi.module.Module;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 22.05.2007
 */
public class TestsConventions
{
	public static boolean isTestFrameworkFile(@NotNull final RVirtualFile rFile, @Nullable final Module module)
	{
		if(module == null)
		{
			return false;
		}

		final String fileUrl = rFile.getContainingFileUrl();

		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for modules with Rails Support

		//Test::Unit
		final String unitTestRoot = railsPaths.getTestsStdUnitRootURL();
		if(fileUrl.startsWith(unitTestRoot))
		{
			return true;
		}

		//RSpec
		final String specTestUrl = RSpecUtil.getRailsSpecFolderPathOrUrl(railsPaths.getRailsApplicationRootURL());
		//noinspection RedundantIfStatement
		if(fileUrl.startsWith(specTestUrl))
		{
			return true;
		}
		return false;
	}
}
