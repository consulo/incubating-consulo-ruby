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

package org.jetbrains.plugins.ruby.addins.rspec.run.configuration;

import consulo.annotation.component.ExtensionImpl;
import consulo.execution.RunnerAndConfigurationSettings;
import consulo.execution.action.ConfigurationContext;
import consulo.execution.action.Location;
import consulo.execution.action.RuntimeConfigurationProducer;
import consulo.language.psi.PsiDirectory;
import consulo.language.psi.PsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;

import jakarta.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: May 8, 2008
 */
@ExtensionImpl
public class RSpecConfigurationProducer extends RuntimeConfigurationProducer implements Cloneable
{
	private PsiElement mySourceElement;

	public RSpecConfigurationProducer()
	{
		super(RSpecRunConfigurationType.getInstance());
	}

	@Override
	public PsiElement getSourceElement()
	{
		return mySourceElement;
	}

	@Override
	@Nullable
	protected RunnerAndConfigurationSettings createConfigurationByElement(Location location, ConfigurationContext context)
	{
		final PsiElement element = location.getPsiElement();

		if(!(element instanceof PsiDirectory) && !(element instanceof RFile) && !(element.getContainingFile() instanceof RFile))
		{
			return null;
		}
		mySourceElement = element;
		return RSpecRunConfigurationType.getInstance().createConfigurationByLocation(location);
	}

	@Override
	public int compareTo(RuntimeConfigurationProducer o)
	{
		return PREFERED;
	}
}

