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

package org.jetbrains.plugins.ruby.ruby.pom;

import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 07.10.2006
 */
public interface RubyChangeVisitor
{
	@SuppressWarnings({"UnusedParameters"})
	public void anythingChanged(final PsiElement element);

	//    public void rSuperChanged(final RSuperClass rSuperClass);

	//    public void rClassAdded(final RClass rClass);
	//    public void rClassReplaced(final RClass rClass);
	//    public void rClassDeleted(final RClassName myRClassName);
	//    public void rClassRemoved(final RClass rClass);
	//    public void rClassRenamed(final RClassName rClassName, RClass rClass);
	//    public void rClassContentChanged(final RClass rClass);

	//    public void rMethodAdded(final RMethod rMethod);
	//    public void rMethodReplaced(final RMethod rMethod);
	//    public void rMethodRemoved(final RMethod rMethod);
	//    public void rMethodDeleted(final RMethodName myRMethodName);
	//    public void rMethodRenamed(final RMethodName rMethodName, RMethod rMethod);
	//    public void rMethodContentChanged(final RMethod rMethod);

	//    public void rModuleAdded(final RModule rModule);
	//    public void rModuleReplaced(final RModule rModule);
	//    public void rModuleRemoved(final RModule rModule);
	//    public void rModuleRenamed(final RModuleName rModuleName, RModule rModule);
	//    public void rModuleDeleted(final RModuleName rModuleName);
	//    public void rModuleContentChanged(final RModule rModule);

	//    public void rBodyAdded(final RBodyStatement myRBodyStatment);
	//    public void rBodyRemoved(final RBodyStatement myRBodyStatment);
	//    public void rBodyReplaced(final RBodyStatement myRBodyStatment);
	//    public void rBodyContentChanged(final RBodyStatement myRBodyStatment);

	public void structureChanged();
}
