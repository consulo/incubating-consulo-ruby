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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.module;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacet;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.CachedSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.FileSymbolType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.SymbolsCache;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.AbstractLayeredCachedSymbol;
import org.jetbrains.plugins.ruby.settings.RSupportPerModuleSettings;
import org.jetbrains.plugins.ruby.support.ui.checkableDir.CheckableDirectoriesContainer;
import org.jetbrains.plugins.ruby.support.ui.checkableDir.CheckableDirectoryItem;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 25, 2008
 */
public class ModuleLayer extends AbstractLayeredCachedSymbol
{

	public ModuleLayer(@NotNull final Project project, @Nullable final Module module, @Nullable final Sdk sdk, final boolean jRubyEnabled)
	{
		super(project, module, sdk, jRubyEnabled);
	}

	@Override
	@Nullable
	protected CachedSymbol getBaseSymbol()
	{
		final SymbolsCache cache = SymbolsCache.getInstance(myProject);
		return cache.getBuiltInCachedSymbol(FileSymbolType.BUILT_IN, mySdk, isJRubyEnabled);
	}

	@Override
	protected void addAdditionalData()
	{
		// Extend loadpath with user directories
		if(myModule != null)
		{
			CheckableDirectoriesContainer checkDirectories = null;
			if(isJRubyEnabled)
			{
				final JRubyFacet facet = JRubyFacet.getInstance(myModule);
				if(facet != null)
				{
					checkDirectories = facet.getConfiguration().getLoadPathDirs();
				}
			}
			else
			{
				RSupportPerModuleSettings settings = RModuleUtil.getRubySupportSettings(myModule);
				if(settings != null)
				{
					checkDirectories = settings.getLoadPathDirs();

				}
			}

			if(checkDirectories != null)
			{
				for(CheckableDirectoryItem item : checkDirectories.getCheckableDirectories())
				{
					if(item.isChecked())
					{
						myFileSymbol.addLoadPathUrl(VirtualFileUtil.constructLocalUrl(item.getDirectoryPath()));
					}
				}
			}
		}
	}

	@Override
	public void fileAdded(@NotNull final String url)
	{
		// do nothing
	}
}
