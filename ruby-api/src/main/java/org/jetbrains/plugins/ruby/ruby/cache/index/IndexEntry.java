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

package org.jetbrains.plugins.ruby.ruby.cache.index;

import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.global.RGlobalVariable;

import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RField;

import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;

import java.util.List;

import jakarta.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualFieldAttr;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RAliasStatement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 13, 2007
 */
public interface IndexEntry
{

	@Nonnull
	public List<RVirtualClass> getClasses();

	@Nonnull
	public List<RVirtualModule> getModules();

	@Nonnull
	public List<RVirtualMethod> getMethods();

	@Nonnull
	public List<RField> getFields();

	@Nonnull
	public List<RConstant> getConstants();

	@Nonnull
	public List<RGlobalVariable> getGlobalVars();

	@Nonnull
	public List<RAliasStatement> getAliases();

	@Nonnull
	public List<RVirtualFieldAttr> getFieldAttrs();

	public boolean isEmpty();

}
