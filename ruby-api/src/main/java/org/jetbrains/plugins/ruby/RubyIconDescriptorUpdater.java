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

package org.jetbrains.plugins.ruby;

import org.jetbrains.plugins.ruby.ruby.lang.psi.RStructuralElement;

import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;

import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;

import consulo.annotation.component.ExtensionImpl;
import consulo.component.util.Iconable;
import consulo.language.icon.IconDescriptor;
import consulo.language.icon.IconDescriptorUpdater;
import consulo.language.psi.PsiElement;
import consulo.module.Module;
import consulo.ui.image.Image;
import consulo.util.lang.BitUtil;
import consulo.util.lang.Comparing;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecIcons;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.HelpersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.ModelsConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.NamingConventions;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RAliasStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.RClassPresentationUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.RContainerPresentationUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.RModulePresentationUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 21, 2007
 */
@ExtensionImpl
public class RubyIconDescriptorUpdater implements IconDescriptorUpdater
{
	@Nullable
	public static Image getIcon(@Nonnull PsiElement element, int flags)
	{
		if(element instanceof RFile)
		{
			final RFile rFile = (RFile) element;
			final Module fileModule = rFile.getModule();

			// RSpec check
			if(RSpecUtil.isFileWithRSpecTestFileName(rFile.getVirtualFile()))
			{
				return RSpecIcons.TEST_SCRIPT_ICON;
			}

			final RContainer virtualContainer = RVirtualPsiUtil.findVirtualContainer(rFile);
			if(virtualContainer instanceof RFile)
			{
				final RFile rVirtualFile = (RFile) virtualContainer;
				final List<RClass> classes = RContainerUtil.getTopLevelClasses(virtualContainer);
				final List<RModule> modules = RContainerUtil.getTopLevelModules(virtualContainer);

				// Rails checks

				if(ControllersConventions.isControllerFile(rFile, fileModule, classes))
				{
					return RailsIcons.RAILS_CONTROLLER_NODE;
				}
				if(ModelsConventions.isModelFile(rFile, fileModule, classes))
				{
					return RailsIcons.RAILS_MODEL_NODE;
				}
				if(HelpersConventions.isHelperFile(rFile, fileModule, modules))
				{
					return RailsIcons.RAILS_HELPER_NODE;
				}

				// Default ruby behavour, checking names conventions
				//noinspection ConstantConditions
				final String fileName = rFile.getVirtualFile().getNameWithoutExtension();
				final String mixedFileName = NamingConventions.toMixedCase(fileName);
				for(RStructuralElement structuralElement : rVirtualFile.getVirtualStructureElements())
				{
					final StructureType type = structuralElement.getType();
					if(type == StructureType.CLASS)
					{
						if(Comparing.equal(mixedFileName, ((RClass) structuralElement).getName()))
						{
							return RClassPresentationUtil.getIcon();
						}
					}
					if(type == StructureType.MODULE)
					{
						if(Comparing.equal(mixedFileName, ((RModule) structuralElement).getName()))
						{
							return RModulePresentationUtil.getIcon();
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public void updateIcon(@Nonnull IconDescriptor iconDescriptor, @Nonnull PsiElement element, int i)
	{
		Image icon = getIcon(element, i);
		if(icon != null)
		{
			iconDescriptor.setMainIcon(icon);
		}

		if(element instanceof RClass)
		{
			iconDescriptor.setMainIcon(RubyIcons.RUBY_CLASS_NODE);

			if(BitUtil.isSet(i, Iconable.ICON_FLAG_VISIBILITY))
			{
				iconDescriptor.setRightIcon(RContainerPresentationUtil.getIconForAccessModifier(((RClass) element).getAccessModifier()));
			}
		}

		if(element instanceof RAliasStatement)
		{
			iconDescriptor.setMainIcon(RubyIcons.RUBY_ALIAS_NODE);
		}
	}
}
