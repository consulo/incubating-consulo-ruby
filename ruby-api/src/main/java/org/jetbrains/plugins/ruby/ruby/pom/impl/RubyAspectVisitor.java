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

package org.jetbrains.plugins.ruby.ruby.pom.impl;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualConstantHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualFieldHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualGlobalVarHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RAliasStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RObjectClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RFunctionArgumentList;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RSingletonMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RConstantHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RFieldHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RGlobalVarHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.arguments.RArgumentNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RClassVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RInstanceVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.global.RGlobalVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubySystemCallVisitor;
import org.jetbrains.plugins.ruby.ruby.pom.impl.events.RStructureChange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Jul 20, 2007
 */
class RubyAspectVisitor extends RubySystemCallVisitor
{
	private RubyChangeSetImpl myRubyChangeSet;
	private boolean changeFound;

	public RubyAspectVisitor(@Nonnull final RubyChangeSetImpl rubyChangeSet)
	{
		myRubyChangeSet = rubyChangeSet;
	}

	@Override
	public void visitElement(@Nonnull final PsiElement element)
	{
		// if in name
		final RName rName = PsiTreeUtil.getParentOfType(element, RName.class);
		if(rName != null)
		{
			visitRName(rName);
			return;
		}
		// if in alias
		final RAliasStatement alias = PsiTreeUtil.getParentOfType(element, RAliasStatement.class);
		if(alias != null)
		{
			visitRAliasStatement(alias);
			return;
		}
		// if in important call
		final RCall call = PsiTreeUtil.getParentOfType(element, RCall.class);
		if(call != null)
		{
			visitRCall(call);
			return;
		}

		// check if parent container hasn`t changed
		checkContainingContainer(element);
	}

	@Override
	public void visitRName(@Nonnull final RName name)
	{
		createStructureChange("Changes withing the name");
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////// Class or instance fields
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void visitRClassVariable(@Nonnull final RClassVariable rClassVariable)
	{
		checkForFieldsChanges(rClassVariable);
	}

	@Override
	public void visitRInstanceVariable(@Nonnull final RInstanceVariable rInstanceVariable)
	{
		checkForFieldsChanges(rInstanceVariable);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////  Constant or identifier
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void visitRConstant(@Nonnull final RConstant rConstant)
	{
		if(checkForConstantsChanges(rConstant))
		{
			return;
		}
		visitElement(rConstant);
	}

	@Override
	public void visitRGlobalVariable(@Nonnull final RGlobalVariable rGlobalVariable)
	{
		if(checkForGlobalVariablesChanges(rGlobalVariable))
		{
			return;
		}

		visitElement(rGlobalVariable);
	}

	@Override
	public void visitRIdentifier(@Nonnull final RIdentifier rIdentifier)
	{
		if(RArgumentNavigator.getByRIdentifier(rIdentifier) != null)
		{
			createStructureChange("parameter name changed");
			return;
		}

		visitElement(rIdentifier);
	}

	@Override
	public void visitRFunctionArgumentList(@Nonnull final RFunctionArgumentList list)
	{
		createStructureChange("argument list changed");
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///// Classes / modules / methods
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void checkContainingContainer(@Nonnull final PsiElement element)
	{
		final RContainer container = PsiTreeUtil.getParentOfType(element, RContainer.class);
		if(container != null)
		{
			visitRContainer(container);
		}
	}

	private void visitRContainer(@Nonnull final RContainer container)
	{
		if(!(container instanceof RFile) && container.getParent() == null)
		{
			createStructureChange("container was deleted!!!");
			return;
		}
		RVirtualContainer vContainer = RVirtualPsiUtil.findVirtualContainer(container);
		if(vContainer == null || !container.equalsToVirtual(vContainer))
		{
			createStructureChange("container structural elements changed!!!");
		}

		if(checkForConstantsChanges(container))
		{
			return;
		}

		if(checkForGlobalVariablesChanges(container))
		{
			return;
		}

		checkForFieldsChanges(container);
	}

	private void checkForFieldsChanges(@Nonnull final RPsiElement element)
	{
		final RFieldHolder fHolder = element instanceof RFieldHolder ? (RFieldHolder) element : PsiTreeUtil.getParentOfType(element, RFieldHolder.class);
		if(fHolder != null)
		{
			final RVirtualContainer vContainer = RVirtualPsiUtil.findVirtualContainer(fHolder);
			if(!(vContainer instanceof RVirtualFieldHolder && RVirtualPsiUtil.areFieldHoldersEqual(fHolder, (RVirtualFieldHolder) vContainer)))
			{
				createStructureChange("fields changed!!!");
			}
		}
	}

	private boolean checkForConstantsChanges(@Nonnull final RPsiElement element)
	{
		final RConstantHolder cHolder = element instanceof RConstantHolder ? (RConstantHolder) element : PsiTreeUtil.getParentOfType(element, RConstantHolder.class);
		if(cHolder != null)
		{
			final RVirtualContainer vContainer = RVirtualPsiUtil.findVirtualContainer(cHolder);
			if(!(vContainer instanceof RVirtualConstantHolder && RVirtualPsiUtil.areConstantHoldersEqual(cHolder, (RVirtualConstantHolder) vContainer)))
			{
				createStructureChange("constants changed!!!");
				return true;
			}
		}
		return false;
	}

	private boolean checkForGlobalVariablesChanges(@Nonnull final RPsiElement element)
	{
		final RGlobalVarHolder vHolder = element instanceof RGlobalVarHolder ? (RGlobalVarHolder) element : PsiTreeUtil.getParentOfType(element, RGlobalVarHolder.class);
		if(vHolder != null)
		{
			final RVirtualContainer vContainer = RVirtualPsiUtil.findVirtualContainer(vHolder);
			if(!(vContainer instanceof RVirtualGlobalVarHolder && RVirtualPsiUtil.areGlobalVariableHoldersEqual(vHolder, (RVirtualGlobalVarHolder) vContainer)))
			{
				createStructureChange("global variables changed!!!");
				return true;
			}
		}
		return false;
	}

	@Override
	public void visitRFile(@Nonnull final RFile rFile)
	{
		visitRContainer(rFile);
	}

	@Override
	public void visitRClass(@Nonnull final RClass rClass)
	{
		visitRContainer(rClass);
	}

	@Override
	public void visitRModule(@Nonnull final RModule module)
	{
		visitRContainer(module);
	}

	@Override
	public void visitRMethod(@Nonnull final RMethod rMethod)
	{
		visitRContainer(rMethod);
	}

	@Override
	public void visitRSingletonMethod(@Nonnull final RSingletonMethod rsMethod)
	{
		visitRContainer(rsMethod);
	}

	@Override
	public void visitRObjectClass(@Nonnull final RObjectClass rsClass)
	{
		visitRContainer(rsClass);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///// Important calls
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void visitRCall(@Nonnull RCall rCall)
	{
		checkContainingContainer(rCall);
	}

	private void createStructureChange(String message)
	{
		//        System.err.println(String.valueOf(number++) + ": " + message);
		myRubyChangeSet.add(new RStructureChange(message));
		changeFound = true;
	}

	@SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
	public boolean isChangeFound()
	{
		return changeFound;
	}
}
