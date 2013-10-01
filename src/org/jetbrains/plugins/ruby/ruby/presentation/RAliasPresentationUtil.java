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
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualAlias;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.navigation.ItemPresentation;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Aug 29, 2007
 */
public class RAliasPresentationUtil
{

	public static Icon getIcon()
	{
		return RubyIcons.RUBY_ALIAS_NODE;
	}

	public static ItemPresentation getPresentation(RVirtualAlias alias)
	{
		final Icon icon = getIcon();
		return new PresentationData(alias.getNewName(), TextUtil.wrapInParens(getLocation(alias)), icon, icon, null);
	}

	public static String getLocation(@NotNull final RVirtualAlias alias)
	{
		return RContainerPresentationUtil.getLocation(alias);
	}
}
