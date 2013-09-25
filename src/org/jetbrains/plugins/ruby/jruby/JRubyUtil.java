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

package org.jetbrains.plugins.ruby.jruby;

import org.consulo.jruby.module.extension.JRubyModuleExtension;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 12, 2007
 */
public class JRubyUtil
{
	@NonNls
	public static final String JAVA = "java";

	/**
	 * @param module some module
	 * @return true if module is Java module with JRuby facet
	 */
	public static boolean hasJRubySupport(@NotNull final Module module)
	{
		return ModuleUtilCore.getExtension(module, JRubyModuleExtension.class) != null;
	}

	/**
	 * @param module Module to get JDK for
	 * @return Jdk selected for given module
	 */
	@Nullable
	public static Sdk getJRubyFacetSdk(@NotNull final Module module)
	{
		JRubyModuleExtension extension = ModuleUtilCore.getExtension(module, JRubyModuleExtension.class);
		if(extension == null)
		{
			return null;
		}
		return extension.getSdk();
	}
}
