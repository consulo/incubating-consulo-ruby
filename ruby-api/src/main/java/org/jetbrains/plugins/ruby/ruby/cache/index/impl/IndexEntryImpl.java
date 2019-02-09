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

package org.jetbrains.plugins.ruby.ruby.cache.index.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.cache.index.IndexEntry;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualAlias;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualConstant;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualFieldAttr;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualGlobalVar;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 13, 2007
 */
public class IndexEntryImpl implements IndexEntry
{
	private List<RVirtualClass> myClasses = new ArrayList<RVirtualClass>();
	private List<RVirtualModule> myModules = new ArrayList<RVirtualModule>();
	private List<RVirtualMethod> myMethods = new ArrayList<RVirtualMethod>();
	private List<RVirtualField> myFields = new ArrayList<RVirtualField>();
	private List<RVirtualConstant> myConstants = new ArrayList<RVirtualConstant>();
	private List<RVirtualGlobalVar> myGlobalVars = new ArrayList<RVirtualGlobalVar>();
	private List<RVirtualAlias> myAliases = new ArrayList<RVirtualAlias>();
	private List<RVirtualFieldAttr> myFieldAttrs = new ArrayList<RVirtualFieldAttr>();

	@Override
	@Nonnull
	public List<RVirtualClass> getClasses()
	{
		return myClasses;
	}

	@Override
	@Nonnull
	public List<RVirtualModule> getModules()
	{
		return myModules;
	}

	@Override
	@Nonnull
	public List<RVirtualMethod> getMethods()
	{
		return myMethods;
	}

	@Override
	@Nonnull
	public List<RVirtualField> getFields()
	{
		return myFields;
	}

	@Override
	@Nonnull
	public List<RVirtualConstant> getConstants()
	{
		return myConstants;
	}

	@Override
	@Nonnull
	public List<RVirtualGlobalVar> getGlobalVars()
	{
		return myGlobalVars;
	}

	@Override
	@Nonnull
	public List<RVirtualAlias> getAliases()
	{
		return myAliases;
	}

	@Override
	@Nonnull
	public List<RVirtualFieldAttr> getFieldAttrs()
	{
		return myFieldAttrs;
	}

	@Override
	public boolean isEmpty()
	{
		return myClasses.isEmpty() &&
				myModules.isEmpty() &&
				myMethods.isEmpty() &&
				myConstants.isEmpty() &&
				myGlobalVars.isEmpty() &&
				myFields.isEmpty() &&
				myAliases.isEmpty() &&
				myFieldAttrs.isEmpty();
	}


	public void addContainer(@Nonnull final RVirtualContainer container)
	{
		final StructureType type = container.getType();
		if(type.isMethod())
		{
			addMethod((RVirtualMethod) container);
			return;
		}
		if(type == StructureType.CLASS)
		{
			addClass((RVirtualClass) container);
			return;
		}
		if(type == StructureType.MODULE)
		{
			addModule((RVirtualModule) container);
		}
	}

	private void addClass(@Nonnull final RVirtualClass vClass)
	{
		myClasses.add(vClass);
	}

	private void addModule(@Nonnull final RVirtualModule vModule)
	{
		myModules.add(vModule);
	}

	private void addMethod(@Nonnull final RVirtualMethod vMethod)
	{
		myMethods.add(vMethod);
	}

	public void addConstant(@Nonnull final RVirtualConstant constant)
	{
		myConstants.add(constant);
	}

	public void addGlobalVar(@Nonnull final RVirtualGlobalVar globalVar)
	{
		myGlobalVars.add(globalVar);
	}

	public void addAlias(@Nonnull final RVirtualAlias rVirtualAlias)
	{
		myAliases.add(rVirtualAlias);
	}

	public void addFieldAttr(@Nonnull final RVirtualFieldAttr rVirtualFieldAttr)
	{
		myFieldAttrs.add(rVirtualFieldAttr);
	}

	public void addField(@Nonnull final RVirtualField field)
	{
		myFields.add(field);
	}


	public void removeContainer(@Nonnull final RVirtualContainer container)
	{
		final StructureType type = container.getType();
		if(type.isMethod())
		{
			removeMethod((RVirtualMethod) container);
			return;
		}
		if(type == StructureType.CLASS)
		{
			removeClass((RVirtualClass) container);
			return;
		}
		if(type == StructureType.MODULE)
		{
			removeModule((RVirtualModule) container);
		}
	}

	private void removeModule(@Nonnull final RVirtualModule rVirtualModule)
	{
		myModules.remove(rVirtualModule);
	}

	private void removeClass(@Nonnull final RVirtualClass rVirtualClass)
	{
		myClasses.remove(rVirtualClass);
	}

	private void removeMethod(@Nonnull final RVirtualMethod rVirtualMethod)
	{
		myMethods.remove(rVirtualMethod);
	}

	public void removeField(@Nonnull final RVirtualField field)
	{
		myFields.remove(field);
	}

	public void removeConstant(@Nonnull final RVirtualConstant constant)
	{
		myConstants.remove(constant);
	}

	public void removeGlobalVar(@Nonnull final RVirtualGlobalVar globalVar)
	{
		myGlobalVars.remove(globalVar);
	}

	public void removeAlias(@Nonnull final RVirtualAlias rVirtualAlias)
	{
		myAliases.remove(rVirtualAlias);
	}

	public void removeFieldAttr(@Nonnull final RVirtualFieldAttr rVirtualFieldAttr)
	{
		myFieldAttrs.remove(rVirtualFieldAttr);
	}

}
