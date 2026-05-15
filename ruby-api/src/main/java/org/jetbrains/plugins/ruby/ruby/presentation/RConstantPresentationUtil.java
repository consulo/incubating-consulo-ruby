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

import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;

import consulo.navigation.ItemPresentation;
import consulo.ui.ex.tree.PresentationData;
import jakarta.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 16, 2007
 */
public class RConstantPresentationUtil
{

	public static Image getIcon()
	{
		return RubyIcons.RUBY_CONSTANT_NODE;
	}

	public static ItemPresentation getPresentation(@Nonnull final RConstant constant)
	{
		final Image icon = getIcon();
		return new PresentationData(constant.getName(), TextUtil.wrapInParens(getLocation(constant)), icon, null);
	}

	public static String getLocation(@Nonnull final RConstant constant)
	{
		return RContainerPresentationUtil.getContainerNameWithLocation(constant.getHolder());
	}


}
