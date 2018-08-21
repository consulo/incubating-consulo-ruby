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

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualObjectClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualConstantHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualFieldHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualGlobalVarHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualClassImpl;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualFileImpl;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualModuleImpl;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualObjectClassImpl;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.variables.RVirtualConstantImpl;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.variables.RVirtualFieldImpl;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.variables.RVirtualGlobalVarImpl;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualConstant;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualGlobalVar;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.ConstantDefinitions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.FieldDefinition;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.GlobalVarDefinition;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RConstantHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RFieldHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RGlobalVarHolder;
import com.intellij.openapi.diagnostic.Logger;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: Oct 2, 2006
 */
public class RVirtualUtil
{
	private static final Logger LOG = Logger.getInstance(RVirtualUtil.class.getName());

	/**
	 * Copies realFile to it virtual copy
	 *
	 * @param realFile Container to be copied
	 * @param fileInfo RFileInfo for realFile
	 * @return RVirtualContainer instance
	 */
	@NotNull
	public static RVirtualFile createBy(@NotNull final RFile realFile, @NotNull final RFileInfo fileInfo)
	{
		return realFile.createVirtualCopy(null, fileInfo);
	}

	public static void addVirtualFields(RVirtualContainer container, RFieldHolder holder)
	{
		final List<RVirtualField> fields = getVirtualFields(container, holder);

		// setting RVirtualFields
		if(container instanceof RVirtualFile)
		{
			((RVirtualFileImpl) container).setVirtualFields(fields);
		}
		else
		{
			if(container instanceof RVirtualModule)
			{
				((RVirtualModuleImpl) container).setVirtualFields(fields);
			}
			else if(container instanceof RVirtualClass)
			{
				((RVirtualClassImpl) container).setVirtualFields(fields);
			}
			else if(container instanceof RVirtualObjectClass)
			{
				((RVirtualObjectClassImpl) container).setVirtualFields(fields);
			}
			else
			{
				LOG.error("Cannot set virtual fields for " + container);
			}
		}
	}

	public static List<RVirtualField> getVirtualFields(@NotNull final RVirtualContainer container, @NotNull final RFieldHolder holder)
	{
		List<RVirtualField> fields = new ArrayList<RVirtualField>();
		for(FieldDefinition fieldUsages : holder.getFieldsDefinitions())
		{
			fields.add(new RVirtualFieldImpl(fieldUsages.getName(), (RVirtualFieldHolder) container, fieldUsages.getType()));
		}
		return fields;
	}

	public static void addVirtualConstants(@NotNull final RVirtualContainer container, @NotNull final RConstantHolder holder)
	{
		final List<RVirtualConstant> constants = getVirtualConstants(container, holder);

		// setting RVirtualConstants
		if(container instanceof RVirtualFile)
		{
			((RVirtualFileImpl) container).setVirtualConstants(constants);
		}
		else if(container instanceof RVirtualModule)
		{
			((RVirtualModuleImpl) container).setVirtualConstants(constants);
		}
		else if(container instanceof RVirtualClass)
		{
			((RVirtualClassImpl) container).setVirtualConstants(constants);
		}
		else if(container instanceof RVirtualObjectClass)
		{
			((RVirtualObjectClassImpl) container).setVirtualConstants(constants);
		}
		else
		{
			LOG.error("Cannot set virtual constants for " + container);
		}
	}

	public static List<RVirtualConstant> getVirtualConstants(RVirtualContainer container, RConstantHolder holder)
	{
		List<RVirtualConstant> constants = new ArrayList<RVirtualConstant>();
		for(ConstantDefinitions constantDefinitions : holder.getConstantDefinitions())
		{
			constants.add(new RVirtualConstantImpl(constantDefinitions.getName(), (RVirtualConstantHolder) container));
		}
		return constants;
	}


	public static void addVirtualGlobalVars(@NotNull final RVirtualContainer container, @NotNull final RGlobalVarHolder holder)
	{
		final List<RVirtualGlobalVar> vars = getVirtualGlobalVars(container, holder);

		// setting RVirtualGlobalVar
		if(container instanceof RVirtualFile)
		{
			((RVirtualFileImpl) container).setVirtualGlobalVars(vars);
		}
		else
		{
			LOG.error("Cannot set virtual constants for " + container);
		}
	}

	public static List<RVirtualGlobalVar> getVirtualGlobalVars(RVirtualContainer container, RGlobalVarHolder holder)
	{
		List<RVirtualGlobalVar> vars = new ArrayList<RVirtualGlobalVar>();
		for(GlobalVarDefinition definition : holder.getGlobalVarDefinitions())
		{
			vars.add(new RVirtualGlobalVarImpl(definition.getText(), (RVirtualGlobalVarHolder) container));
		}
		return vars;
	}

	/**
	 * Gathers all the  virtual elements under container
	 *
	 * @param container Root container
	 * @return List of RVirtualStructuralElements
	 */
	public static List<RVirtualStructuralElement> gatherAllStructuralElement(@NotNull final RVirtualContainer container)
	{
		final ArrayList<RVirtualStructuralElement> list = new ArrayList<RVirtualStructuralElement>();
		gatherAllStructuralElementRec(container, list);
		return list;
	}

	private static void gatherAllStructuralElementRec(@NotNull final RVirtualStructuralElement element, @NotNull final ArrayList<RVirtualStructuralElement> list)
	{
		list.add(element);
		if(element instanceof RVirtualContainer)
		{
			for(RVirtualStructuralElement child : ((RVirtualContainer) element).getVirtualStructureElements())
			{
				gatherAllStructuralElementRec(child, list);
			}
		}
	}
}

