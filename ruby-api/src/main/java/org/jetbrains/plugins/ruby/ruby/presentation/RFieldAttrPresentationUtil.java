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

import com.intellij.ide.projectView.PresentationData;
import com.intellij.navigation.ItemPresentation;
import consulo.awt.TargetAWT;
import consulo.ui.image.Image;
import consulo.ui.image.ImageEffects;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.FieldAttrType;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.methodCall.RCallBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Aug 29, 2007
 */
public class RFieldAttrPresentationUtil implements RubyIcons
{
	public static Image getAttrIcon(@Nonnull final FieldAttrType fieldAttrType)
	{
		Image mainIcon = null;
		if(fieldAttrType != FieldAttrType.ATTR_INTERNAL && fieldAttrType != FieldAttrType.CATTR_ACCESSOR)
		{
			mainIcon = RUBY_FIELD_NODE;
		}
		else
		{
			mainIcon = RUBY_ATTR_NODE;
		}
		return ImageEffects.layered(mainIcon, getFieldAttrIcon(fieldAttrType));
	}

	private static Image getFieldAttrIcon(@Nonnull final FieldAttrType fieldAttrType)
	{
		if(fieldAttrType == FieldAttrType.ATTR_ACCESSOR)
		{
			return ImageEffects.layered(RUBY_ATTR_READER, RUBY_ATTR_WRITER);
		}
		if(fieldAttrType == FieldAttrType.ATTR_WRITER)
		{
			return RUBY_ATTR_WRITER;
		}
		return RUBY_ATTR_READER;
	}

	@Nonnull
	public static String getFieldAttrText(@Nonnull final FieldAttrType fieldAttrType)
	{
		if(fieldAttrType == FieldAttrType.ATTR_ACCESSOR)
		{
			return RCall.ATTR_ACCESSOR_COMMAND;
		}
		if(fieldAttrType == FieldAttrType.ATTR_INTERNAL)
		{
			return RCall.ATTR_INTERNAL;
		}
		if(fieldAttrType == FieldAttrType.CATTR_ACCESSOR)
		{
			return RCall.CATTR_ACCESSOR;
		}
		if(fieldAttrType == FieldAttrType.ATTR_READER)
		{
			return RCall.ATTR_READER_COMMAND;
		}
		return RCall.ATTR_WRITER_COMMAND;
	}

	@Nonnull
	public static ItemPresentation getPresentation(@Nonnull final RCallBase rCall)
	{
		assert rCall.getCallType().isAttributeCall();
		final Image icon = getAttrIcon(rCall.getFieldAttrType());
		return new PresentationData(rCall.getText(), TextUtil.wrapInParens(getLocation(rCall)), TargetAWT.to(icon), null);
	}

	public static String getLocation(@Nonnull final RCall call)
	{
		return RContainerPresentationUtil.getLocation(call);
	}
}
