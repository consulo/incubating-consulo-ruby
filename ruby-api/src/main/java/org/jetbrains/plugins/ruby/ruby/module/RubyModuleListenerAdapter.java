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

package org.jetbrains.plugins.ruby.ruby.module;

import java.util.List;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 26.09.2006
 */
public abstract class RubyModuleListenerAdapter implements RubyModuleListener
{

	@Override
	public void moduleAdded(Project project, Module module)
	{
	}

	@Override
	public void beforeModuleRemoved(Project project, Module module)
	{
	}

	@Override
	public void moduleRemoved(Project project, Module module)
	{
	}

	@Override
	public void modulesRenamed(Project project, List<Module> modules)
	{
	}

	@Override
	public void moduleChanged()
	{
	}
}
