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

package org.jetbrains.plugins.ruby.ruby.lang.psi.visitors;

import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RObjectClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RSingletonMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 31.07.2006
 */
public abstract class RubyStructureVisitor extends RubyElementVisitor
{

	@Override
	public void visitElement(PsiElement psiElement)
	{
		psiElement.acceptChildren(this);
	}

	@Override
	public void visitRFile(RFile file)
	{
		visitContainer(file);
	}

	@Override
	public void visitRModule(RModule rModule)
	{
		visitContainer(rModule);
	}

	@Override
	public void visitRClass(RClass rClass)
	{
		visitContainer(rClass);
	}

	@Override
	public void visitRObjectClass(RObjectClass rMetaClass)
	{
		visitContainer(rMetaClass);
	}

	@Override
	public void visitRMethod(RMethod rMethod)
	{
		visitContainer(rMethod);
	}

	@Override
	public void visitRSingletonMethod(RSingletonMethod rSingletonMethod)
	{
		visitContainer(rSingletonMethod);
	}

	/**
	 * Method to override
	 *
	 * @param rContainer container to visit
	 */
	public abstract void visitContainer(RContainer rContainer);
}
