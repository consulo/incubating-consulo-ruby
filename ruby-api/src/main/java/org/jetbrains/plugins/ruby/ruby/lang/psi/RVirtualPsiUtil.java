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

package org.jetbrains.plugins.ruby.ruby.lang.psi;

import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;

import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;

import java.util.LinkedList;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import consulo.language.psi.PsiManager;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.ConstantDefinitions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.FieldDefinition;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.GlobalVarDefinition;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RConstantHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RFieldHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RGlobalVarHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RField;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.global.RGlobalVariable;
import consulo.virtualFileSystem.VirtualFileManager;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.scope.GlobalSearchScope;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman.Chernyatchik, oleg
 * @date: Feb 10, 2007
 */
public class RVirtualPsiUtil
{

	/**
	 * Returns PsiElement by file URL
	 *
	 * @param fileUrl Url
	 * @param project Current project
	 * @return PsiElement - PsiFile if found, null otherwise
	 */
	@Nullable
	public static PsiFile getPsiFile(@Nonnull final String fileUrl, @Nonnull final Project project)
	{
		final VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(fileUrl);
		if(file == null)
		{
			return null;
		}
		return PsiManager.getInstance(project).findFile(file);
	}

	/**
	 * Returns psiElement by RVirtualStrucuturalElement
	 *
	 * @param element virtual element to find
	 * @param project Current project
	 * @return PsiElement - Real psi element, corresponding virtual element, or null, if nothing found
	 */
	@Nullable
	public static RPsiElement findPsiByVirtualElement(@Nonnull final RPsiElement element, @Nonnull final Project project)
	{
		if(element instanceof RPsiElement)
		{
			return (RPsiElement) element;
		}

		if(element instanceof RStructuralElement)
		{
			return findInPsi(project, (RStructuralElement) element);
		}
		if(element instanceof RConstant)
		{
			final RConstant constant = (RConstant) element;
			return findRConstant(constant, project);
		}
		if(element instanceof RField)
		{
			final RField field = (RField) element;
			return findRField(field, project);
		}
		if(element instanceof RGlobalVariable)
		{
			final RGlobalVariable var = (RGlobalVariable) element;
			return findRGlobalVar(var, project);
		}
		return null;
	}


	@Nullable
	public static RStructuralElement findInPsi(@Nonnull final Project project, @Nonnull final RStructuralElement element)
	{
		if(element instanceof RStructuralElement)
		{
			return (RStructuralElement) element;
		}

		String url = null;
		if(element instanceof RContainer)
		{
			url = ((RContainer) element).getContainingFileUrl();
		}
		else
		{
			final RContainer parentContainer = element.getVirtualParentContainer();
			if(parentContainer != null)
			{
				url = parentContainer.getContainingFileUrl();
			}
		}
		if(url == null)
		{
			return null;
		}

		final PsiFile file = getPsiFile(url, project);
		if(!(file instanceof RFile))
		{
			return null;
		}

		final RStructuralElement item = findByPath((RContainer) file, createStructurePath(element));
		return item instanceof RStructuralElement ? (RStructuralElement) item : (RStructuralElement) file;
	}

	@Nullable
	private static RContainer findVirtualContainer(@Nonnull final RContainer container, @Nonnull final RFile file)
	{
		final RStructuralElement item = findByPath(file, createStructurePath(container));
		return item instanceof RContainer ? (RContainer) item : file;
	}

	@Nullable
	public static RConstant findRConstant(@Nonnull final RConstant constant, @Nonnull final Project project)
	{
		final RConstantHolder holder = constant.getHolder();
		final RStructuralElement element = findInPsi(project, holder);
		if(element instanceof RConstantHolder)
		{
			final RConstantHolder constantHolder = (RConstantHolder) element;
			final ConstantDefinitions def = constantHolder.getDefinition(constant);
			return def != null ? def.getFirstDefinition() : null;
		}
		return null;
	}

	@Nullable
	public static RField findRField(@Nonnull final RField field, @Nonnull final Project project)
	{
		final RFieldHolder vHolder = field.getHolder();
		final RStructuralElement element = findInPsi(project, vHolder);
		if(element instanceof RFieldHolder)
		{
			final RFieldHolder fieldHolder = (RFieldHolder) element;
			final FieldDefinition usages = fieldHolder.getDefinition(field);
			return usages != null ? usages.getFirstUsage() : null;
		}
		return null;
	}

	@Nullable
	public static RGlobalVariable findRGlobalVar(@Nonnull final RGlobalVariable var, @Nonnull final Project project)
	{
		final RGlobalVarHolder holder = var.getHolder();
		final RStructuralElement element = findInPsi(project, holder);
		if(element instanceof RGlobalVarHolder)
		{
			final RGlobalVarHolder globalVarHolder = (RGlobalVarHolder) element;
			final GlobalVarDefinition def = globalVarHolder.getDefinition(var);
			return def != null ? def.getFirstDefinition() : null;
		}
		return null;
	}


	public static LinkedList<Integer> createStructurePath(@Nonnull final RStructuralElement anchor)
	{
		final LinkedList<Integer> path = new LinkedList<Integer>();
		RStructuralElement current = anchor;
		do
		{
			final RContainer parent = current.getVirtualParentContainer();
			if(parent != null)
			{
				path.addFirst(parent.getIndexOf(current));
			}
			current = parent;
		}
		while(current != null);
		return path;
	}

	@Nullable
	private static RStructuralElement findByPath(@Nonnull final RContainer root, @Nonnull final List<Integer> path)
	{
		RStructuralElement element = root;
		for(Integer index : path)
		{
			if(element instanceof RContainer)
			{
				final List<RStructuralElement> elements = ((RContainer) element).getVirtualStructureElements();
				if(0 <= index && index < elements.size())
				{
					element = elements.get(index);
				}
				else
				{
					return null;
				}
			}
		}
		return element;
	}

	@Nullable
	public static RContainer findVirtualContainer(@Nonnull final RContainer container)
	{
		return container;
	}

	@Nullable
	public static RMethod getMethodWithoutArgumentsByName(@Nonnull final RContainer container, @Nullable final String name)
	{
		for(RStructuralElement element : RContainerUtil.selectVirtualElementsByType(container.getVirtualStructureElements(), StructureType.METHOD))
		{
			assert element instanceof RMethod;
			final RMethod method = (RMethod) element;
			if(method.getName().equals(name) && method.getArgumentInfos().size() == 0)
			{
				return method;
			}
		}
		return null;
	}

	// ANSWER: For optimization in POM Aspect, if method was changed we can compare old
	// virtual method and new psi method. Sometimes method structure(submethods ets.) really wasn't
	// and method body change event was sent because of \n inserting

	public static boolean areMethodsEqual(@Nonnull final RMethod method, @Nonnull final RMethod otherMethod)
	{
		if(method == otherMethod)
		{
			return true;
		}
		if(!method.getName().equals(otherMethod.getName()) || method.getAccessModifier() != otherMethod.getAccessModifier() ||
				!method.getArgumentInfos().equals(otherMethod.getArgumentInfos()))
		{
			return false;
		}
		final List<RStructuralElement> subCandidateMethods = RContainerUtil.selectVirtualElementsByType(otherMethod.getVirtualStructureElements(), StructureType.METHOD);
		final List<RStructuralElement> subMethods = RContainerUtil.selectVirtualElementsByType(method.getVirtualStructureElements(), StructureType.METHOD);

		//noinspection RedundantIfStatement
		if(subCandidateMethods.size() != subMethods.size())
		{
			return false;
		}
		return true;
	}


	public static boolean areSubStructureEqual(@Nonnull final RContainer container, @Nonnull final RContainer vContainer)
	{
		// Substructure check
		final List<RStructuralElement> myElements = container.getStructureElements();
		final List<RStructuralElement> virtualElements = vContainer.getVirtualStructureElements();
		final int size = myElements.size();
		if(size != virtualElements.size())
		{
			return false;
		}
		for(int i = 0; i < size; i++)
		{
			final RStructuralElement structuralElement = myElements.get(i);
			RStructuralElement virtualElement = virtualElements.get(i);
			if(!structuralElement.equalsToVirtual(virtualElement))
			{
				//                System.err.println(structuralElement + " is not equal to " + virtualElement);
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
	public static boolean areConstantHoldersEqual(@Nonnull final RConstantHolder holder1, @Nonnull final RConstantHolder holder2)
	{
		final List<RConstant> constants1 = holder1.getVirtualConstants();
		final List<RConstant> constants2 = holder2.getVirtualConstants();
		final int size = constants1.size();
		if(size != constants2.size())
		{
			return false;
		}
		for(int i = 0; i < size; i++)
		{
			final RConstant constant1 = constants1.get(i);
			final RConstant constant2 = constants2.get(i);
			if(!constant1.getName().equals(constant2.getName()))
			{
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
	public static boolean areGlobalVariableHoldersEqual(@Nonnull final RGlobalVarHolder holder1, @Nonnull final RGlobalVarHolder holder2)
	{
		final List<RGlobalVariable> vars1 = holder1.getVirtualGlobalVars();
		final List<RGlobalVariable> vars2 = holder2.getVirtualGlobalVars();
		final int size = vars1.size();
		if(size != vars2.size())
		{
			return false;
		}
		for(int i = 0; i < size; i++)
		{
			final RGlobalVariable var1 = vars1.get(i);
			final RGlobalVariable var2 = vars2.get(i);
			if(!var1.getText().equals(var2.getText()))
			{
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
	public static boolean areFieldHoldersEqual(@Nonnull final RFieldHolder holder1, @Nonnull final RFieldHolder holder2)
	{
		final List<RField> fields1 = holder1.getVirtualFields();
		final List<RField> fields2 = holder2.getVirtualFields();
		final int size = fields1.size();
		if(size != fields2.size())
		{
			return false;
		}
		for(int i = 0; i < size; i++)
		{
			final RField field1 = fields1.get(i);
			final RField field2 = fields2.get(i);
			if(field1.getType() != field2.getType())
			{
				return false;
			}
			if(!field1.getName().equals(field2.getName()))
			{
				return false;
			}
		}
		return true;
	}

	@Nullable
	public static RClass getContainingRVClass(@Nonnull final RContainer rContainer)
	{
		RContainer current = rContainer.getVirtualParentContainer();
		while(current != null)
		{
			if(current instanceof RClass)
			{
				return (RClass) current;
			}
			current = current.getVirtualParentContainer();
		}
		return null;
	}
}
