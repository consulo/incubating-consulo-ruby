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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.FieldType;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import consulo.ui.image.Image;
import consulo.ui.image.ImageEffects;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 16, 2007
 */
public class RFieldPresentationUtil implements RubyIcons
{

	public static Image getIcon(@Nonnull final RVirtualField rVirtualField)
	{
		return getIconByRField(rVirtualField);
	}

	/**
	 * Computes icon for RVirtualClass.
	 * Be careful, if flags contains information about visibility, method uses
	 * RIconsUtils.getIconWithModifiers()
	 *
	 * @param rVirtualField RVirtualClass
	 * @param flags         com.intellij.openapi.util.Iconable flags
	 * @return Icon
	 */
	public static Image getIcon(@Nonnull final RVirtualField rVirtualField, final int flags)
	{
		if((flags & Iconable.ICON_FLAG_VISIBILITY) == Iconable.ICON_FLAG_VISIBILITY)
		{
			return getIconByRField(rVirtualField);
		}
		return RubyIcons.RUBY_FIELD_NODE;
	}

	public static ItemPresentation getPresentation(@Nonnull final RVirtualField rVirtualField)
	{
		final Image icon = getIcon(rVirtualField, Iconable.ICON_FLAG_VISIBILITY);
		return new PresentationData(rVirtualField.getName(), TextUtil.wrapInParens(getLocation(rVirtualField)), icon, null);
	}

	public static String getLocation(@Nonnull final RVirtualField field)
	{
		return RContainerPresentationUtil.getContainerNameWithLocation(field.getHolder());
	}

	/**
	 * Generates the icon by RVirtualField
	 *
	 * @param field Field to get the icon for
	 * @return Field icon
	 */
	public static Image getIconByRField(final RVirtualField field)
	{
		final FieldType type = field.getType();
		final Image attrIcon = getIconType(type);
		if(attrIcon != null)
		{
			return ImageEffects.layered(RUBY_FIELD_NODE, attrIcon);
		}
		return RUBY_FIELD_NODE;
	}

	@Nullable
	private static Image getIconType(final FieldType type)
	{
		if(type == FieldType.CLASS_VARIABLE)
		{
			return RUBY_ATTR_STATIC;
		}
		return null;
	}
}
