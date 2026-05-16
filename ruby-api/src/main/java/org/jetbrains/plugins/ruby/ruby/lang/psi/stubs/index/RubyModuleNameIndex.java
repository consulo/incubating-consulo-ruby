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

package org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.index;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.stub.StringStubIndexExtension;
import consulo.language.psi.stub.StubIndex;
import consulo.language.psi.stub.StubIndexKey;
import consulo.project.Project;
import consulo.project.content.scope.ProjectScopes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;

import java.util.Collection;

@ExtensionImpl
public class RubyModuleNameIndex extends StringStubIndexExtension<RModule>
{
	public static final StubIndexKey<String, RModule> KEY = StubIndexKey.createIndexKey("Ruby.module.shortName");

	@Override
	public StubIndexKey<String, RModule> getKey()
	{
		return KEY;
	}

	public static Collection<RModule> find(String name, Project project, GlobalSearchScope scope)
	{
		return StubIndex.getInstance().get(KEY, name, project, scope);
	}

	public static Collection<RModule> find(String name, Project project)
	{
		return StubIndex.getInstance().get(KEY, name, project, ProjectScopes.getAllScope(project));
	}

	public static Collection<String> allKeys(Project project)
	{
		return StubIndex.getInstance().getAllKeys(KEY, project);
	}
}
