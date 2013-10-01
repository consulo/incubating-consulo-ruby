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

import java.util.Collections;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacetType;
import org.jetbrains.plugins.ruby.jruby.inspections.JRubyImplementInterfaceInspection;
import org.jetbrains.plugins.ruby.jruby.inspections.WrongTopLevelPackageInspection;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkType;
import org.jetbrains.plugins.ruby.support.utils.IdeaInternalUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ActionRunner;
import com.intellij.util.PathUtil;

/**
 * It`s a class to load jruby support
 */
public class JRubySupportLoader implements ApplicationComponent, InspectionToolProvider
{
	private static final Logger LOG = Logger.getInstance(JRubySupportLoader.class.getName());

	@Override
	public void initComponent()
	{
		loadJRuby();
	}

	@NonNls
	private static final String JAR = ".jar";

	/**
	 * Loading plugin components, written in jruby
	 */
	private static void loadJRubyPluginComponents()
	{
		ClassLoader oldCtxLoad = Thread.currentThread().getContextClassLoader();
		try
		{
			ClassLoader ctxLoader = JRubySupportLoader.class.getClassLoader();
			Thread.currentThread().setContextClassLoader(ctxLoader);
			Ruby ruby = JavaEmbedUtils.initialize(Collections.emptyList());
			final String jarPath = PathUtil.getJarPathForClass(RubySdkType.class);
			if(jarPath != null && jarPath.endsWith(JAR))
			{
				final VirtualFile jarFile = VirtualFileManager.getInstance().findFileByUrl(VirtualFileUtil.constructLocalUrl(jarPath));

				LOG.assertTrue(jarFile != null, "jar file cannot be null");
				//noinspection ConstantConditions
				final VirtualFile mainFile = jarFile.getParent().getParent().findFileByRelativePath("rb/main.rb");
				LOG.assertTrue(mainFile != null, "main.rb file cannot be null");
				ruby.getLoadService().require(mainFile.getPath());
			}
			else
			{
				ruby.getLoadService().require(PathUtil.getJarPathForClass(JRubySupportLoader.class) + "/rb/main");
				//                ruby.getLoadService().require("/home/oleg/work/ruby/src/rb/main");
			}
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(oldCtxLoad);
		}
	}

	@Override
	public void disposeComponent()
	{
	}

	@Override
	@NotNull
	public String getComponentName()
	{
		return JRubyComponents.JRUBY_SUPPORT_LOADER;
	}

	public static void loadJRuby()
	{
		IdeaInternalUtil.runInsideWriteAction(new ActionRunner.InterruptibleRunnable()
		{
			@Override
			public void run() throws Exception
			{
				// registering ruby facet
				JRubyFacetType.load();

				// load jruby written components
				loadJRubyPluginComponents();
			}
		});
	}

	public static void loadJRubyTestComponents(@NotNull final String path)
	{
		ClassLoader oldCtxLoad = Thread.currentThread().getContextClassLoader();
		try
		{
			ClassLoader ctxLoader = JRubySupportLoader.class.getClassLoader();
			Thread.currentThread().setContextClassLoader(ctxLoader);
			Ruby ruby = JavaEmbedUtils.initialize(Collections.emptyList());
			ruby.getLoadService().require(path);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(oldCtxLoad);
		}
	}

	@Override
	public Class[] getInspectionClasses()
	{
		return new Class[]{
				JRubyImplementInterfaceInspection.class,
				WrongTopLevelPackageInspection.class
		};
	}
}

