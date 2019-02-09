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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.FileSymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.InterpretationMode;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.CachedSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.AbstractLayeredCachedSymbol;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFileManager;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Oct 8, 2007
 */
public class BuiltInCachedSymbol extends AbstractLayeredCachedSymbol
{
	protected String myLoadUrl;
	private static final Logger LOG = Logger.getInstance(BuiltInCachedSymbol.class.getName());

	public BuiltInCachedSymbol(@Nonnull final Project project, @Nonnull final String url, @Nullable final Sdk sdk)
	{
		super(project, null, sdk, false);
		myLoadUrl = url;
	}

	/**
	 * Rebuilds FileSymbol with ProgressBar
	 */
	@Override
	protected final void updateFileSymbol()
	{
		if(myFileSymbol == null)
		{
			final ProgressManager manager = ProgressManager.getInstance();

			final Runnable runnable = new Runnable()
			{
				@Override
				public void run()
				{
					final ProgressIndicator indicator = manager.getProgressIndicator();
					if(indicator != null && mySdk != null)
					{
						indicator.setText(mySdk.getName());
					}
					BuiltInCachedSymbol.super.updateFileSymbol();
				}
			};
			if(manager.getProgressIndicator() != null)
			{
				runnable.run();
			}
			else
			{
				final String title = RBundle.message("cache.symbol.recreating.builtins.title");
				manager.runProcessWithProgressSynchronously(runnable, title, false, myProject);
			}
		}
	}

	@Override
	@Nullable
	protected CachedSymbol getBaseSymbol()
	{
		return null;
	}

	@Override
	public void fileAdded(@Nonnull String url)
	{
		if(mySdk != null)
		{
			if(url.startsWith(RubySdkUtil.getRubyStubsDirUrl(mySdk)))
			{
				myFileSymbol = null;
			}
		}
	}

	private void addSdkLoadPath()
	{
		if(mySdk != null && RubySdkUtil.isKindOfRubySDK(mySdk))
		{
			for(String rootUrl : RubySdkUtil.getSdkRootsWithAllGems(mySdk))
			{
				// TODO: uncomment, when require_gem and gem commands will be supported, load all the gems into loadpath
				//                        // Add rails gems to loadpath
				//                        for (String gemLibUrl : RailsGemsUtil.getRailsGems(mySdk)) {
				//                            myFileSymbol.addLoadPath(gemLibUrl);
				//                        }
				FileSymbolUtil.addLoadPath(myFileSymbol, rootUrl);
			}
		}
	}

	@Override
	protected void addAdditionalData()
	{
		addSdkLoadPath();
		if(!ApplicationManager.getApplication().isUnitTestMode())
		{
			LOG.assertTrue(VirtualFileManager.getInstance().findFileByUrl(myLoadUrl) != null, "Stubs are corrupted. Cannot find file: " + myLoadUrl);
		}
		FileSymbolUtil.process(myFileSymbol, myLoadUrl, InterpretationMode.FULL, true);
	}
}
