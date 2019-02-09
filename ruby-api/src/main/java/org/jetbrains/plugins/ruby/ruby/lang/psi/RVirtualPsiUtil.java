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

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.RCacheUtil;
import org.jetbrains.plugins.ruby.ruby.cache.RubyModuleCachesManager;
import org.jetbrains.plugins.ruby.ruby.cache.RubySdkCachesManager;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyModuleFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualConstantHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualFieldHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualGlobalVarHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualConstant;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualGlobalVar;
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
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman.Chernyatchik, oleg
 * @date: Feb 10, 2007
 */
public class RVirtualPsiUtil
{

	public static List<RubyFilesCache> getCaches(@Nonnull final Project project, @Nullable final Module module, @Nullable final Sdk sdk)
	{
		final List<RubyFilesCache> caches = new LinkedList<RubyFilesCache>();

		final RubyModuleCachesManager manager = (module != null) ? RCacheUtil.getCachesManager(module) : null;

		final RubyModuleFilesCache moduleCache = (manager != null ? manager.getFilesCache() : null);
		if(moduleCache != null)
		{
			caches.add(moduleCache);
		}

		final RubySdkCachesManager sdkCachesManager = RubySdkCachesManager.getInstance(project);
		final RubyFilesCache sdkCache = sdk != null ? sdkCachesManager.getSdkFilesCache(sdk) : null;
		if(sdkCache != null)
		{
			caches.add(sdkCache);
		}
		return caches;
	}

	@Nullable
	public static RubyFilesCache getCacheForFile(@Nonnull final String url, final RubyFilesCache... caches)
	{
		for(RubyFilesCache cache : caches)
		{
			if(cache != null && cache.containsUrl(url))
			{
				return cache;
			}
		}
		return null;
	}


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
	public static RPsiElement findPsiByVirtualElement(@Nonnull final RVirtualElement element, @Nonnull final Project project)
	{
		if(element instanceof RPsiElement)
		{
			return (RPsiElement) element;
		}

		if(element instanceof RVirtualStructuralElement)
		{
			return findInPsi(project, (RVirtualStructuralElement) element);
		}
		if(element instanceof RVirtualConstant)
		{
			final RVirtualConstant constant = (RVirtualConstant) element;
			return findRConstant(constant, project);
		}
		if(element instanceof RVirtualField)
		{
			final RVirtualField field = (RVirtualField) element;
			return findRField(field, project);
		}
		if(element instanceof RVirtualGlobalVar)
		{
			final RVirtualGlobalVar var = (RVirtualGlobalVar) element;
			return findRGlobalVar(var, project);
		}
		return null;
	}


	@Nullable
	public static RStructuralElement findInPsi(@Nonnull final Project project, @Nonnull final RVirtualStructuralElement element)
	{
		if(element instanceof RStructuralElement)
		{
			return (RStructuralElement) element;
		}

		String url = null;
		if(element instanceof RVirtualContainer)
		{
			url = ((RVirtualContainer) element).getContainingFileUrl();
		}
		else
		{
			final RVirtualContainer parentContainer = element.getVirtualParentContainer();
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

		final RVirtualStructuralElement item = findByPath((RContainer) file, createStructurePath(element));
		return item instanceof RStructuralElement ? (RStructuralElement) item : (RStructuralElement) file;
	}

	@Nullable
	private static RVirtualContainer findVirtualContainer(@Nonnull final RContainer container, @Nonnull final RVirtualFile file)
	{
		final RVirtualStructuralElement item = findByPath(file, createStructurePath(container));
		return item instanceof RVirtualContainer ? (RVirtualContainer) item : file;
	}

	@Nullable
	public static RConstant findRConstant(@Nonnull final RVirtualConstant constant, @Nonnull final Project project)
	{
		final RVirtualConstantHolder holder = constant.getHolder();
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
	public static RField findRField(@Nonnull final RVirtualField field, @Nonnull final Project project)
	{
		final RVirtualFieldHolder vHolder = field.getHolder();
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
	public static RGlobalVariable findRGlobalVar(@Nonnull final RVirtualGlobalVar var, @Nonnull final Project project)
	{
		final RVirtualGlobalVarHolder holder = var.getHolder();
		final RStructuralElement element = findInPsi(project, holder);
		if(element instanceof RGlobalVarHolder)
		{
			final RGlobalVarHolder globalVarHolder = (RGlobalVarHolder) element;
			final GlobalVarDefinition def = globalVarHolder.getDefinition(var);
			return def != null ? def.getFirstDefinition() : null;
		}
		return null;
	}


	public static LinkedList<Integer> createStructurePath(@Nonnull final RVirtualStructuralElement anchor)
	{
		final LinkedList<Integer> path = new LinkedList<Integer>();
		RVirtualStructuralElement current = anchor;
		do
		{
			final RVirtualContainer parent = current.getVirtualParentContainer();
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
	private static RVirtualStructuralElement findByPath(@Nonnull final RVirtualContainer root, @Nonnull final List<Integer> path)
	{
		RVirtualStructuralElement element = root;
		for(Integer index : path)
		{
			if(element instanceof RVirtualContainer)
			{
				final List<RVirtualStructuralElement> elements = ((RVirtualContainer) element).getVirtualStructureElements();
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
	public static RVirtualContainer findVirtualContainer(@Nonnull final RContainer container)
	{
		final RFileInfo info = getInfoByPsiElement(container);
		return info != null ? findVirtualContainer(container, info.getRVirtualFile()) : null;
	}

	@Nullable
	public static RVirtualMethod getMethodWithoutArgumentsByName(@Nonnull final RVirtualContainer container, @Nullable final String name)
	{
		for(RVirtualStructuralElement element : RContainerUtil.selectVirtualElementsByType(container.getVirtualStructureElements(), StructureType.METHOD))
		{
			assert element instanceof RVirtualMethod;
			final RVirtualMethod method = (RVirtualMethod) element;
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

	public static boolean areMethodsEqual(@Nonnull final RVirtualMethod method, @Nonnull final RVirtualMethod otherMethod)
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
		final List<RVirtualStructuralElement> subCandidateMethods = RContainerUtil.selectVirtualElementsByType(otherMethod.getVirtualStructureElements(), StructureType.METHOD);
		final List<RVirtualStructuralElement> subMethods = RContainerUtil.selectVirtualElementsByType(method.getVirtualStructureElements(), StructureType.METHOD);

		//noinspection RedundantIfStatement
		if(subCandidateMethods.size() != subMethods.size())
		{
			return false;
		}
		return true;
	}


	@Nullable
	public static RFileInfo getInfoByPsiElement(@Nonnull final PsiElement element)
	{
		final PsiFile psiFile = element.getContainingFile();
		return psiFile != null ? getInfoByPsiFile(psiFile) : null;
	}

	public static RFileInfo getInfoByPsiFile(@Nonnull final PsiFile psiFile)
	{
		if(!(psiFile instanceof RFile))
		{
			return null;
		}
		RFile rFile = (RFile) psiFile;
		final VirtualFile file = psiFile.getVirtualFile();
		if(file == null)
		{
			return null;
		}
		final Project project = rFile.getProject();
		final String url = file.getUrl();
		// Getting caches
		RubyFilesCache cache = getCacheForFile(url, RCacheUtil.getCachesByFile(file, GlobalSearchScope.allScope(project), project));
		return cache != null ? cache.getUp2DateFileInfo(file) : null;
	}

	public static boolean areSubStructureEqual(@Nonnull final RContainer container, @Nonnull final RVirtualContainer vContainer)
	{
		// Substructure check
		final List<RStructuralElement> myElements = container.getStructureElements();
		final List<RVirtualStructuralElement> virtualElements = vContainer.getVirtualStructureElements();
		final int size = myElements.size();
		if(size != virtualElements.size())
		{
			return false;
		}
		for(int i = 0; i < size; i++)
		{
			final RStructuralElement structuralElement = myElements.get(i);
			RVirtualStructuralElement virtualElement = virtualElements.get(i);
			if(!structuralElement.equalsToVirtual(virtualElement))
			{
				//                System.err.println(structuralElement + " is not equal to " + virtualElement);
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
	public static boolean areConstantHoldersEqual(@Nonnull final RVirtualConstantHolder holder1, @Nonnull final RVirtualConstantHolder holder2)
	{
		final List<RVirtualConstant> constants1 = holder1.getVirtualConstants();
		final List<RVirtualConstant> constants2 = holder2.getVirtualConstants();
		final int size = constants1.size();
		if(size != constants2.size())
		{
			return false;
		}
		for(int i = 0; i < size; i++)
		{
			final RVirtualConstant constant1 = constants1.get(i);
			final RVirtualConstant constant2 = constants2.get(i);
			if(!constant1.getName().equals(constant2.getName()))
			{
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
	public static boolean areGlobalVariableHoldersEqual(@Nonnull final RVirtualGlobalVarHolder holder1, @Nonnull final RVirtualGlobalVarHolder holder2)
	{
		final List<RVirtualGlobalVar> vars1 = holder1.getVirtualGlobalVars();
		final List<RVirtualGlobalVar> vars2 = holder2.getVirtualGlobalVars();
		final int size = vars1.size();
		if(size != vars2.size())
		{
			return false;
		}
		for(int i = 0; i < size; i++)
		{
			final RVirtualGlobalVar var1 = vars1.get(i);
			final RVirtualGlobalVar var2 = vars2.get(i);
			if(!var1.getText().equals(var2.getText()))
			{
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
	public static boolean areFieldHoldersEqual(@Nonnull final RVirtualFieldHolder holder1, @Nonnull final RVirtualFieldHolder holder2)
	{
		final List<RVirtualField> fields1 = holder1.getVirtualFields();
		final List<RVirtualField> fields2 = holder2.getVirtualFields();
		final int size = fields1.size();
		if(size != fields2.size())
		{
			return false;
		}
		for(int i = 0; i < size; i++)
		{
			final RVirtualField field1 = fields1.get(i);
			final RVirtualField field2 = fields2.get(i);
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
	public static RVirtualClass getContainingRVClass(@Nonnull final RVirtualContainer rContainer)
	{
		RVirtualContainer current = rContainer.getVirtualParentContainer();
		while(current != null)
		{
			if(current instanceof RVirtualClass)
			{
				return (RVirtualClass) current;
			}
			current = current.getVirtualParentContainer();
		}
		return null;
	}
}
