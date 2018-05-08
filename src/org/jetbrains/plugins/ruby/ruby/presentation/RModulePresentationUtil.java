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

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import consulo.awt.TargetAWT;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 29.10.2006
 */
public class RModulePresentationUtil implements RPresentationConstants
{

	public static Image getIcon()
	{
		return RubyIcons.RUBY_MODULE_NODE;
	}

	/**
	 * Computes icon for RVirtualModule.
	 * Be careful, if flags contains information about visibility, method uses
	 * RIconsUtils.getIconWithModifiers()
	 *
	 * @param rModule RVirtualModule
	 * @param flags   com.intellij.openapi.util.Iconable flags
	 * @return Icon
	 */
	public static Icon getIcon(final RVirtualModule rModule, final int flags)
	{
		if((flags & Iconable.ICON_FLAG_VISIBILITY) == Iconable.ICON_FLAG_VISIBILITY)
		{
			return RContainerPresentationUtil.getIconWithModifiers(rModule);
		}
		return TargetAWT.to(getIcon());
	}

	@NotNull
	public static ItemPresentation getPresentation(final RVirtualModule rModule)
	{
		final Icon icon = getIcon(rModule, Iconable.ICON_FLAG_VISIBILITY);
		return new PresentationData(formatName(rModule, SHOW_NAME), TextUtil.wrapInParens(getLocation(rModule)), icon, null);
	}

	public static String getLocation(final RVirtualModule rModule)
	{
		return RContainerPresentationUtil.getLocation(rModule);
	}

	public static String formatName(@NotNull final RVirtualModule rModule, final int options)
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
			buffer.append(rModule.getFullName());
		}
		else if((options & SHOW_NAME) != 0)
		{
			buffer.append(rModule.getName());
		}

		return buffer.toString();
	}
}
