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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl;

import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualRequire;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.FileSymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.InterpretationMode;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.RailsRequireUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.CachedSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.FileSymbolType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.SymbolCacheUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.SymbolsCache;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 7, 2007
 */
public class ModifiableCachedSymbol extends AbstractCachedSymbol
{
	// Requires from main file
	private List<RVirtualRequire> myRequires;

	// All the list of required files
	private Set<String> myAllExternalUrls;

	private FileSymbol myOuterSymbol;
	private String myUrl;
	private boolean isJRubyEnabled;

	public ModifiableCachedSymbol(@NotNull final Project project, @NotNull final String url, @Nullable final Module module, @Nullable final Sdk sdk, final boolean jRubyEnabled)
	{
		super(project, module, sdk);
		myUrl = url;
		isJRubyEnabled = jRubyEnabled;
	}

	@Override
	public void fileAdded(@NotNull final String url)
	{
		// In common case, we clear cache if file is added
		myOuterSymbol = null;
		myFileSymbol = null;
	}

	@Override
	protected void fileChanged(@NotNull final String url)
	{
		if(myOuterSymbol == null)
		{
			return;
		}

		final RFileInfo fileInfo = FileSymbolUtil.getRFileInfo(myUrl, myCaches);
		final RVirtualFile file = fileInfo != null ? fileInfo.getRVirtualFile() : null;
		if(file == null)
		{
			return;
		}

		if(myUrl.equals(url))
		{
			myFileSymbol = null;
			if(myRequires == null || !myRequires.equals(file.getRequires()))
			{
				myOuterSymbol = null;
			}
			return;
		}

		if(myAllExternalUrls == null || myAllExternalUrls.contains(url))
		{
			myFileSymbol = null;
			myOuterSymbol = null;
		}
	}

	private void updateOuterSymbol(@NotNull final RVirtualFile file)
	{
		if(myOuterSymbol != null)
		{
			return;
		}
		myOuterSymbol = new FileSymbol(SymbolCacheUtil.getFileSymbol(getBaseSymbol()), myProject, isJRubyEnabled, myCaches);
		FileSymbolUtil.process(myOuterSymbol, myUrl, InterpretationMode.EXTERNAL, false);

		// Adding rails specified symbol if needed
		if(myModule != null && RailsFacetUtil.hasRailsSupport(myModule))
		{
			RailsRequireUtil.requireRailsFiles(myOuterSymbol, file, myUrl, myModule);
		}
	}

	@Override
	protected void updateFileSymbol()
	{
		final RFileInfo fileInfo = FileSymbolUtil.getRFileInfo(myUrl, myCaches);
		final RVirtualFile file = fileInfo != null ? fileInfo.getRVirtualFile() : null;
		if(file == null)
		{
			myFileSymbol = null;
			myOuterSymbol = null;
			return;
		}

		updateOuterSymbol(file);

		if(myFileSymbol == null)
		{
			myFileSymbol = new FileSymbol(myOuterSymbol, myProject, isJRubyEnabled, myCaches);
			FileSymbolUtil.process(myFileSymbol, myUrl, InterpretationMode.IGNORE_EXTERNAL, true);
			myAllExternalUrls = FileSymbolUtil.getUrls(myFileSymbol);
			myRequires = file.getRequires();
		}
	}

	@Nullable
	private CachedSymbol getBaseSymbol()
	{
		final RFileInfo fileInfo = FileSymbolUtil.getRFileInfo(myUrl, myCaches);
		final RVirtualFile file = fileInfo != null ? fileInfo.getRVirtualFile() : null;
		if(file == null)
		{
			return null;
		}
		if(RubySdkUtil.isKindOfRubySDK(mySdk))
		{
			final String stubsDir = RubySdkUtil.getRubyStubsDirUrl(mySdk);
			if(stubsDir == null)
			{
				return null;
			}

			final SymbolsCache cache = SymbolsCache.getInstance(myProject);

			// Check if we`re inside rails module
			if(myModule != null && RailsFacetUtil.hasRailsSupport(myModule))
			{
				// Try to get level
				final FileSymbolType railsLayerType = RailsRequireUtil.getRailsLayerType(file, myModule);
				if(railsLayerType != null)
				{
					return cache.getCachedSymbol(railsLayerType, myModule, mySdk, isJRubyEnabled);
				}
				// return rails module layer
				return cache.getCachedSymbol(FileSymbolType.RAILS_MODULE_LAYER, myModule, mySdk, isJRubyEnabled);
			}
			// Else we return just module layer
			return cache.getCachedSymbol(FileSymbolType.MODULE_LAYER, myModule, mySdk, isJRubyEnabled);
		}
		return null;
	}

}
