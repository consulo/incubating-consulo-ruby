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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.ModifiableCachedSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.builtin.BuiltInCachedSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.builtin.RailsBuiltInCachedSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.module.ModuleLayer;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.module.RailsModuleLayer;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.railslayers.ControllersAndHelpersLayer;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.railslayers.LibsLayer;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.railslayers.MailersLayer;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.railslayers.ModelsLayer;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.railslayers.VendorLayer;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.railslayers.WebServicesLayer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Oct 8, 2007
 */
public class CachedSymbolFactory
{
	private static final Logger LOG = Logger.getInstance(CachedSymbolFactory.class.getName());

	@Nullable
	public static CachedSymbol createCachedSymbol(@Nonnull final FileSymbolType type, @Nullable final String url, @Nonnull final Project project, @Nullable final Module module, @Nullable final Sdk sdk, final boolean isJRubyEnabled)
	{
		// Modifiable
		if(type == FileSymbolType.MODIFIABLE)
		{
			LOG.assertTrue(url != null);
			return new ModifiableCachedSymbol(project, url, module, sdk, isJRubyEnabled);
		}

		// Builtin types handling
		if(type == FileSymbolType.BUILT_IN)
		{
			LOG.assertTrue(url != null);
			return new BuiltInCachedSymbol(project, url, sdk);
		}
		if(type == FileSymbolType.RAILS_BUILT_IN)
		{
			LOG.assertTrue(url != null);
			return new RailsBuiltInCachedSymbol(project, url, sdk);
		}

		// Module layers
		if(type == FileSymbolType.MODULE_LAYER)
		{
			return new ModuleLayer(project, module, sdk, isJRubyEnabled);
		}

		if(type == FileSymbolType.RAILS_MODULE_LAYER)
		{
			return new RailsModuleLayer(project, module, sdk, isJRubyEnabled);
		}

		// Rails specific layers
		if(type == FileSymbolType.CONTROLLERS_AND_HELPERS_LAYER)
		{
			return new ControllersAndHelpersLayer(project, module, sdk, isJRubyEnabled);
		}
		if(type == FileSymbolType.MODELS_LAYER)
		{
			return new ModelsLayer(project, module, sdk, isJRubyEnabled);
		}
		if(type == FileSymbolType.LIBS_LAYER)
		{
			return new LibsLayer(project, module, sdk, isJRubyEnabled);
		}
		if(type == FileSymbolType.VENDOR_LAYER)
		{
			return new VendorLayer(project, module, sdk, isJRubyEnabled);
		}
		if(type == FileSymbolType.MAILERS_LAYER)
		{
			return new MailersLayer(project, module, sdk, isJRubyEnabled);
		}
		if(type == FileSymbolType.WEBSERVICES_LAYER)
		{
			return new WebServicesLayer(project, module, sdk, isJRubyEnabled);
		}
		return null;
	}
}
