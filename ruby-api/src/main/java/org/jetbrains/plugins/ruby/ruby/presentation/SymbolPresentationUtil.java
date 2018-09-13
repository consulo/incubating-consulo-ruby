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

package org.jetbrains.plugins.ruby.ruby.presentation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import consulo.ui.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualAlias;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualImportJavaClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualIncludeJavaClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualConstant;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualFieldAttr;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualGlobalVar;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.JavaLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubyLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubyPsiLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubySimpleLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.LastSymbolStorage;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.JavaSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.lang.documentation.RubyHelpUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.RCommandArgumentListImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.global.RGlobalVariable;
import com.intellij.codeInsight.lookup.LookupValueWithPriority;
import consulo.awt.TargetAWT;
import consulo.ide.IconDescriptorUpdaters;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaPackage;
import com.intellij.psi.PsiMethod;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 20, 2007
 */
public class SymbolPresentationUtil
{

	@Nullable
	public static RubyLookupItem createRubyLookupItem(@NotNull final Symbol symbol, @NotNull String name, boolean bold, final boolean multiMessage)
	{
		final Type type = symbol.getType();
		if(type == Type.FILE)
		{
			return null;
		}

		// Local variable access
		if(type == Type.FIELD_WRITE_ACCESS)
		{
			return new RubySimpleLookupItem(name, RBundle.message("field.write"), LookupValueWithPriority.HIGH, true, RubyIcons.RUBY_FIELD_NODE);
		}
		if(type == Type.CONSTANT_ACCESS)
		{
			return new RubySimpleLookupItem(name, RBundle.message("constant"), LookupValueWithPriority.HIGH, true, RubyIcons.RUBY_NOT_DEFINED_NODE);
		}
		if(type == Type.CALL_ACCESS)
		{
			return new RubySimpleLookupItem(name, RBundle.message("call"), LookupValueWithPriority.HIGH, true, RubyIcons.RUBY_METHOD_NODE);
		}
		if(type == Type.ATTRIBUTE)
		{
			return new RubySimpleLookupItem(name, null, LookupValueWithPriority.HIGHER, true, RubyIcons.RUBY_ATTR_NODE);
		}

		// JRuby symbols
		if(type == Type.JAVA_CLASS)
		{
			final JavaSymbol javaSymbol = (JavaSymbol) symbol;
			final PsiElement element = javaSymbol.getPsiElement();
			assert element instanceof PsiClass;
			return new JavaLookupItem(name, element);
		}

		if(type == Type.JAVA_PACKAGE)
		{
			final JavaSymbol javaSymbol = (JavaSymbol) symbol;
			final PsiElement element = javaSymbol.getPsiElement();
			assert element instanceof PsiJavaPackage;
			return new JavaLookupItem(name, element);
		}

		if(type == Type.JAVA_METHOD)
		{
			final JavaSymbol javaSymbol = (JavaSymbol) symbol;
			final PsiElement element = javaSymbol.getPsiElement();
			assert element instanceof PsiMethod;
			return new JavaLookupItem(name, element);
		}

		if(type == Type.JAVA_FIELD)
		{
			final JavaSymbol javaSymbol = (JavaSymbol) symbol;
			final PsiElement element = javaSymbol.getPsiElement();
			assert element instanceof PsiField;
			return new JavaLookupItem(name, element);
		}

		// Ruby symbols
		final Project project = symbol.getProject();
		final FileSymbol fileSymbol = LastSymbolStorage.getInstance(project).getSymbol();
		final RVirtualElement lastPrototype = symbol.getLastVirtualPrototype(fileSymbol);

		String tailText = null;
		if(lastPrototype instanceof RVirtualMethod)
		{
			final RVirtualMethod method = (RVirtualMethod) lastPrototype;
			tailText = "(" + RCommandArgumentListImpl.getPresentableName(method.getArgumentInfos()) + ")";
		}

		int priority = bold ? LookupValueWithPriority.HIGH : LookupValueWithPriority.NORMAL;
		// Setting icon for lookupItem
		Image icon = null;
		if(lastPrototype instanceof RVirtualContainer)
		{
			// We should set high priority only for methods
			if(type != Type.INSTANCE_METHOD && type != Type.CLASS_METHOD && type != Type.ALIAS)
			{
				priority = LookupValueWithPriority.HIGHER;
			}
			icon = IconDescriptorUpdaters.getIcon(((PsiElement) lastPrototype), Iconable.ICON_FLAG_VISIBILITY);
		}
		else if(lastPrototype instanceof RVirtualField)
		{
			final RVirtualField field = (RVirtualField) lastPrototype;
			name = field.getText();
			icon = RFieldPresentationUtil.getIcon(field);
		}
		else if(lastPrototype instanceof RVirtualConstant)
		{
			icon = RConstantPresentationUtil.getIcon();
		}
		else if(lastPrototype instanceof RVirtualGlobalVar)
		{
			priority = LookupValueWithPriority.NORMAL;
			bold = true;
			icon = RGlobalVariablePresentationUtil.getIcon();
		}
		else if(lastPrototype instanceof RVirtualAlias)
		{
			icon = TargetAWT.from(((RVirtualAlias) lastPrototype).getIcon(Iconable.ICON_FLAG_OPEN));
		}
		else if(lastPrototype instanceof RVirtualFieldAttr)
		{
			icon = RFieldAttrPresentationUtil.getAttrIcon(((RVirtualFieldAttr) lastPrototype).getFieldAttrType());
		}
		else if(lastPrototype instanceof RVirtualImportJavaClass)
		{
			bold = true;
			icon = JavaClassPackagePresentationUtil.getIncludeIcon();
		}
		else if(lastPrototype instanceof RVirtualIncludeJavaClass)
		{
			bold = true;
			icon = JavaClassPackagePresentationUtil.getIncludeIcon();
		}
		else if(type == Type.NOT_DEFINED)
		{
			icon = RContainerPresentationUtil.RUBY_NOT_DEFINED_NODE;
		}

		final Symbol parent = symbol.getParentSymbol();
		assert parent != null;
		String typeText = parent.getType() != Type.FILE ? SymbolUtil.getPresentablePath(parent) : "";
		// See RUBY-1302. Show help for global variables
		if(lastPrototype instanceof RVirtualGlobalVar)
		{
			final RPsiElement elem = RVirtualPsiUtil.findPsiByVirtualElement(lastPrototype, symbol.getProject());
			if(elem instanceof RGlobalVariable)
			{
				typeText = RubyHelpUtil.getPsiHelp(elem);
				if(typeText != null)
				{
					typeText = typeText.substring(1);
					int i = typeText.indexOf('.');
					if(i != -1)
					{
						typeText = typeText.substring(0, i);
					}
					i = typeText.indexOf('\n');
					if(i != -1)
					{
						typeText = typeText.substring(0, i);
					}
					typeText = typeText.trim();
				}
			}
		}
		if(multiMessage)
		{
			typeText = "...";
		}
		return lastPrototype != null ? new RubyPsiLookupItem(project, name, tailText, typeText, lastPrototype, priority, bold, icon) : new RubySimpleLookupItem(name, typeText, priority, bold, icon);
	}

	@Nullable
	/**
	 * Creates presentable name for symbol with location
	 */
	public static String getPresentableNameWithLocation(@Nullable final FileSymbol fileSymbol, @NotNull final Symbol symbol)
	{
		String name = symbol.getName();
		if(name == null)
		{
			return null;
		}
		// Handling Java symbols
		if(Types.JAVA.contains(symbol.getType()))
		{
			final PsiElement element = ((JavaSymbol) symbol).getPsiElement();
			if(element instanceof PsiClass)
			{
				return ((PsiClass) element).getQualifiedName();
			}
			if(element instanceof PsiMethod)
			{
				return name + " " + RBundle.message("in") + " " + ((PsiMethod) element).getContainingClass().getQualifiedName();
			}
			if(element instanceof PsiField)
			{
				return name + " " + RBundle.message("in") + " " + ((PsiField) element).getContainingClass().getQualifiedName();
			}
			return null;
		}
		final RVirtualElement lastPrototype = symbol.getLastVirtualPrototype(fileSymbol);
		if(lastPrototype instanceof RVirtualMethod)
		{
			final RVirtualMethod method = (RVirtualMethod) lastPrototype;
			name += "(" + RCommandArgumentListImpl.getPresentableName(method.getArgumentInfos()) + ")";
		}

		final Symbol parent = symbol.getParentSymbol();
		assert parent != null;
		return parent.getType() != Type.FILE ? name + " " + RBundle.message("in") + " " + SymbolUtil.getPresentablePath(parent) : name;
	}


	@NotNull
	public static List<RVirtualElement> getPrototypesToShow(@Nullable final FileSymbol fileSymbol, @NotNull final Symbol symbol)
	{
		final List<RVirtualElement> list = new ArrayList<RVirtualElement>();
		// We show only last prototype for method!!!
		if(Types.METHODS.contains(symbol.getType()))
		{
			final RVirtualElement element = symbol.getLastVirtualPrototype(fileSymbol);
			if(element != null)
			{
				list.add(element);
			}
		}
		else
		{
			list.addAll(symbol.getVirtualPrototypes(fileSymbol).getAll());
		}
		return list;
	}

}
