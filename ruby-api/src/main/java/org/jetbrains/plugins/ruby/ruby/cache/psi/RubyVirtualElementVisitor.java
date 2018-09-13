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

package org.jetbrains.plugins.ruby.ruby.cache.psi;

import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualObjectClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualSingletonMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualFieldAttr;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 15, 2007
 */
public abstract class RubyVirtualElementVisitor
{

	public void visitElement(RVirtualElement virtualElement)
	{
		// do nothing
	}

	public void visitRVirtualClass(RVirtualClass virtualClass)
	{
		visitElement(virtualClass);
	}

	public void visitRVirtualObjectClass(RVirtualObjectClass virtualObjectClass)
	{
		visitElement(virtualObjectClass);
	}

	public void visitRVirtualMethod(RVirtualMethod virtualMethod)
	{
		visitElement(virtualMethod);
	}

	public void visitRVirtualSingletonMethod(RVirtualSingletonMethod virtualSingletonMethod)
	{
		visitElement(virtualSingletonMethod);
	}

	public void visitRVirtualModule(RVirtualModule virtualModule)
	{
		visitElement(virtualModule);
	}

	public void visitRVirtualFile(RVirtualFile virtualFile)
	{
		visitElement(virtualFile);
	}

	public void visitRVirtualRequire(RVirtualRequire virtualRequire)
	{
		visitElement(virtualRequire);
	}

	public void visitRVirtualLoad(RVirtualLoad rVirtualLoad)
	{
		visitElement(rVirtualLoad);
	}

	public void visitRVirtualAlias(RVirtualAlias rVirtualAlias)
	{
		visitElement(rVirtualAlias);
	}

	public void visitRVirtualInclude(RVirtualInclude rVirtualInclude)
	{
		visitElement(rVirtualInclude);
	}

	public void visitRVirtualExtend(RVirtualExtend rVirtualExtend)
	{
		visitElement(rVirtualExtend);
	}

	public void visitRVirtualFieldAttr(RVirtualFieldAttr rVirtualFieldAttr)
	{
		visitElement(rVirtualFieldAttr);
	}

	public void visitRVirtualImportJavaClass(RVirtualImportJavaClass rVirtualImportJavaClass)
	{
		visitElement(rVirtualImportJavaClass);
	}

	public void visitRVirtualIncludeJavaClass(RVirtualIncludeJavaClass rVirtualIncludeJavaClass)
	{
		visitElement(rVirtualIncludeJavaClass);
	}
}
