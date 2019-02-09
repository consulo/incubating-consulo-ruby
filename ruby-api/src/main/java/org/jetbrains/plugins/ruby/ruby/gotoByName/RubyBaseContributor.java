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

package org.jetbrains.plugins.ruby.ruby.gotoByName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 2, 2007
 */
abstract class RubyBaseContributor implements ChooseByNameContributor
{

	public static List<NavigationItem> getItems(@Nonnull final List<RVirtualElement> elements, @Nonnull final Project project)
	{
		final ArrayList<NavigationItem> items = new ArrayList<NavigationItem>();
		for(RVirtualElement prototype : elements)
		{
			final RPsiElement psiElement = RVirtualPsiUtil.findPsiByVirtualElement(prototype, project);

			if(psiElement instanceof NavigationItem)
			{
				items.add(((NavigationItem) psiElement));
			}
		}
		return items;
	}
}
