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

package org.jetbrains.plugins.ruby.jruby.facet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.jruby.JRubyUtil;
import org.jetbrains.plugins.ruby.ruby.cache.RubySdkCachesManager;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetManagerAdapter;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.util.messages.MessageBusConnection;

public class JRubyFacetListener extends FacetManagerAdapter implements ModuleComponent
{
	private MessageBusConnection myConnection;

	private Module myModule;

	public JRubyFacetListener(@NotNull final Module module)
	{
		myModule = module;
	}

	@Override
	public void initComponent()
	{
		myConnection = myModule.getMessageBus().connect();
		myConnection.subscribe(FacetManager.FACETS_TOPIC, new FacetManagerAdapter()
		{
			public void beforeFacetAdded(@NotNull Facet facet)
			{
				//Is invoked after ROOTS changed.
			}

			public void facetAdded(@NotNull final Facet facet)
			{
				if(!(facet instanceof JRubyFacet))
				{
					// ignores not JRuby Facets
					return;
				}

				//Is invoked after ROOTS changed. (but roots change event doesn't know that module has facet)
				final Project project = myModule.getProject();
				final RubySdkCachesManager sdkCachesManager = RubySdkCachesManager.getInstance(project);
				sdkCachesManager.initAndSetupSkdCaches(project, true);
				final Sdk Sdk = JRubyUtil.getJRubyFacetSdk(myModule);
				//update added facet jdk
				if(Sdk != null)
				{
					final RubyFilesCache cache = sdkCachesManager.getSdkFilesCache(Sdk);
					if(cache != null)
					{
						cache.forceUpdate();
					}
				}
			}

			public void beforeFacetRemoved(@NotNull final Facet facet)
			{
				//Is invoked after ROOTS changed.(but roots change event doesn't know that facet was removed)
				//Module contains JRubyFacet
				if(!(facet instanceof JRubyFacet))
				{
					// ignores not JRuby Facets
					return;
				}

				final JRubyFacet jRubyFacet = (JRubyFacet) facet;

				jRubyFacet.removeSdkLibrary();
				jRubyFacet.projectClosed();
			}

			public void facetRemoved(@NotNull final Facet facet)
			{
				//Is invoked after ROOTS changed.(but roots change event doesn't know that facet was removed)
				//Module doesn't contain JRubyFacet
				if(!(facet instanceof JRubyFacet))
				{
					// ignores not JRuby Facets
					return;
				}

				final Project project = myModule.getProject();
				final RubySdkCachesManager sdkCachesManager = RubySdkCachesManager.getInstance(project);
				sdkCachesManager.initAndSetupSkdCaches(project, true);
			}

			public void facetConfigurationChanged(@NotNull final Facet facet)
			{
				//Is invoked after ROOTS changed.
				if(!(facet instanceof JRubyFacet))
				{
					// ignores not JRuby Facets
					return;
				}

				((JRubyFacet) facet).updateSdkLibrary();
			}
		});
	}


	@Override
	public void disposeComponent()
	{
		myConnection.disconnect();
	}

	@Override
	@NotNull
	public String getComponentName()
	{
		return RComponents.JRUBY_FACET_LISTENER;
	}

	@Override
	public void projectOpened()
	{
		// called when project is opened
	}

	@Override
	public void projectClosed()
	{
		// called when project is being closed
		if(JRubyUtil.hasJRubySupport(myModule))
		{
			final JRubyFacet jRubyFacet = JRubyFacet.getInstance(myModule);
			assert jRubyFacet != null;
			jRubyFacet.projectClosed();
		}
	}

	@Override
	public void moduleAdded()
	{
		// Invoked when the module corresponding to this component instance has been completely
		// loaded and added to the project.
	}
}
