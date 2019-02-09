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

import com.intellij.ide.projectView.PresentationData;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.ui.LayeredIcon;
import consulo.awt.TargetAWT;
import consulo.ui.image.Image;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualObjectClass;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 29.10.2006
 */
public class RClassPresentationUtil implements RPresentationConstants
{

	public static Image getIcon()
	{
		return RubyIcons.RUBY_CLASS_NODE;
	}

	public static Icon getIcon(@Nonnull final RVirtualClass rClass)
	{
		if(rClass instanceof RVirtualObjectClass)
		{
			final LayeredIcon result = new LayeredIcon(2);
			result.setIcon(TargetAWT.to(RubyIcons.RUBY_ATTR_STATIC), 1);
			result.setIcon(TargetAWT.to(RubyIcons.RUBY_CLASS_NODE), 0);
			return result;
		}
		return TargetAWT.to(RubyIcons.RUBY_CLASS_NODE);
	}

	/**
	 * Computes icon for RVirtualClass.
	 * Be careful, if flags contains information about visibility, method uses
	 * RIconsUtils.getIconWithModifiers()
	 *
	 * @param rClass RVirtualClass
	 * @param flags  com.intellij.openapi.util.Iconable flags
	 * @return Icon
	 */
	public static Icon getIcon(@Nonnull final RVirtualClass rClass, final int flags)
	{
		if((flags & Iconable.ICON_FLAG_VISIBILITY) == Iconable.ICON_FLAG_VISIBILITY)
		{
			return TargetAWT.to(RContainerPresentationUtil.getIconWithModifiers(rClass));
		}
		return getIcon(rClass);
	}

	@Nonnull
	public static ItemPresentation getPresentation(@Nonnull final RVirtualClass rClass)
	{
		final Icon icon = getIcon(rClass, Iconable.ICON_FLAG_VISIBILITY);
		return new PresentationData(formatName(rClass, SHOW_NAME), TextUtil.wrapInParens(getLocation(rClass)), icon, icon, null);
	}

	public static String getLocation(final RVirtualClass rClass)
	{
		return RContainerPresentationUtil.getLocation(rClass);
	}

	/**
	 * Formats class representation according options
	 *
	 * @param rClass  Ruby class
	 * @param options Seee RPresentationConstants
	 * @return formated class representation
	 */
	public static String formatName(@Nonnull final RVirtualClass rClass, final int options)
	{
		final StringBuilder buffer = new StringBuilder();

		if((options & SHOW_FULL_NAME) != 0)
		{
			/* it isn't qualified name! only part, that defined by src author
             * Ex.1
             *  module A {
             *    class B {}
             * }
             * Full name for B is "B"
             * Ex.2
             *  module A {
             *    class B::C {}
             * }
             * Full name for B is "B::C"
             */
			buffer.append(rClass.getFullName());
		}
		else if((options & SHOW_NAME) != 0)
		{
			buffer.append(rClass.getName());
		}

		return buffer.toString();
	}

	/**
	 * Qualified name is evaluated by symbol, thus you must be
	 * sure, that given class corresponds to current symbol.
	 *
	 * @param fileSymbol FileSymbol
	 * @param rClass     Ruby class @return return null if ruby class doesn't correspond to last loaded symbol
	 */
	@Nullable
	public static String getRuntimeQualifiedName(@Nonnull final FileSymbol fileSymbol, @Nonnull final RVirtualClass rClass)
	{
		final Symbol symbol = SymbolUtil.getSymbolByContainer(fileSymbol, rClass);
		return symbol != null ? SymbolUtil.getPresentablePath(symbol) : null;
	}

	/**
	 * This is special fast mode for testcase classes
	 *
	 * @param rClass            Ruby class
	 * @param fileSymbolWrapper if null nothing will happen. If wrapper contains
	 *                          not null value, this value will be used for evaluating name, otherwise method
	 *                          will store evaluated ruby mode symbol.
	 * @return return null if not in ruby test mode and ruby class doesn't correspond to last loaded symbol
	 */
	@Nullable
	public static String getRuntimeQualifiedNameInRubyTestMode(@Nonnull final RVirtualClass rClass, @Nullable final Ref<FileSymbol> fileSymbolWrapper)
	{
		final Pair<Symbol, FileSymbol> pair = SymbolUtil.getSymbolByContainerRubyTestMode(rClass, fileSymbolWrapper);
		return pair != null && pair.first != null ? SymbolUtil.getPresentablePath(pair.first) : null;
	}

	/**
	 * @param qualifiedClassName Qualified class name
	 * @return Not qualified name
	 */
	public static String getNameByQualifiedName(@Nonnull final String qualifiedClassName)
	{
		final int i = qualifiedClassName.lastIndexOf(RubyUtil.MODULES_PATH_SEPARATOR);
		if(i < 0)
		{
			return qualifiedClassName;
		}
		return qualifiedClassName.substring(i + RubyUtil.MODULES_PATH_SEPARATOR.length());
	}
}
