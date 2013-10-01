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

import java.util.List;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import com.intellij.ide.IconDescriptorUpdaters;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.ui.RowIcon;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 29.10.2006
 */
public class RContainerPresentationUtil implements RubyIcons, RailsIcons, RPresentationConstants
{
	/**
	 * Generate location
	 *
	 * @param container Container to get location for
	 * @return location
	 */
	@NotNull
	public static String getContainerNameWithLocation(RVirtualContainer container)
	{
		final StringBuilder buff = new StringBuilder();
		while(container != null)
		{
			if(buff.length() > 0 && !(container instanceof RVirtualFile))
			{
				buff.insert(0, RubyUtil.MODULES_PATH_SEPARATOR);
			}

			if(container instanceof RVirtualFile)
			{
				String location = ((RVirtualFile) container).getPresentableLocation();
				if(location == null)
				{
					location = container.getName();
				}
				buff.append(buff.length() > 0 ? TextUtil.wrapInParens(location) : location);
			}
			else
			{
				buff.insert(0, container.getFullName());
			}
			container = container.getVirtualParentContainer();
		}
		return buff.toString();
	}

	/**
	 * Generate location by parent containers names.
	 *
	 * @param element element
	 * @return location
	 */
	public static String getLocation(@NotNull final RVirtualStructuralElement element)
	{
		final RVirtualContainer parentContainer = element.getVirtualParentContainer();
		final String location = getContainerNameWithLocation(parentContainer);
		if(element instanceof RVirtualContainer)
		{
			final StringBuffer buffer = new StringBuffer();
			final List<String> path = ((RVirtualContainer) element).getFullPath();
			for(int i = 0; i < path.size() - 1; i++)
			{
				final String s = path.get(i);
				buffer.append(s);
				if(i != path.size() - 2 || !(parentContainer instanceof RVirtualFile))
				{
					buffer.append(RubyUtil.MODULES_PATH_SEPARATOR);
				}
			}
			if(parentContainer instanceof RVirtualFile && path.size() > 1)
			{
				buffer.append('(');
				buffer.append(location);
				buffer.append(')');
			}
			else
			{
				buffer.append(location);
			}
			return buffer.toString();
		}
		return location;
	}

	/**
	 * Generates the icon by RContainer
	 *
	 * @param container Container to get icon for
	 * @return Icon of container with AccessModifier
	 */
	public static Icon getIconWithModifiers(final RVirtualContainer container)
	{
		final AccessModifier modifier = container.getAccessModifier();
		final RowIcon icon = new RowIcon(2);
		icon.setIcon(IconDescriptorUpdaters.getIcon((PsiElement) container, Iconable.ICON_FLAG_OPEN), 0);
		icon.setIcon(getIconForAccessModifier(modifier), 1);
		return icon;
	}

	@Nullable
	public static Icon getIconForAccessModifier(final AccessModifier modifier)
	{
		if(modifier == AccessModifier.PRIVATE)
		{
			return RUBY_ATTR_PRIVATE;
		}
		if(modifier == AccessModifier.PUBLIC)
		{
			return RUBY_ATTR_PUBLIC;
		}
		if(modifier == AccessModifier.PROTECTED)
		{
			return RUBY_ATTR_PROTECTED;
		}
		return null;
	}


	/**
	 * Formats container representation according options
	 *
	 * @param rContainer Ruby container(e.g. class, method, file or module)
	 * @param options    Seee RPresentationConstants
	 * @return formated container representation
	 */
	public static String formatName(@NotNull final RVirtualContainer rContainer, final int options)
	{
		final StringBuilder buffer = new StringBuilder();

		if((options & SHOW_FULL_NAME) != 0)
		{
			// it isn't qualified name
			buffer.append(rContainer.getFullName());
		}
		else if((options & SHOW_NAME) != 0)
		{
			buffer.append(rContainer.getName());
		}

		return buffer.toString();
	}
}
