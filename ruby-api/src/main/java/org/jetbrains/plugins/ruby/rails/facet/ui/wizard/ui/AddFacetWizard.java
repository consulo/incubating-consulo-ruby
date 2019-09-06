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

package org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui;

import com.intellij.ide.wizard.AbstractWizard;
import com.intellij.openapi.project.Project;

import javax.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Mar 15, 2008
 */
//TODO Remove me after JRails UI will be completed!
public class AddFacetWizard extends AbstractWizard
{
	protected Project myCurrentProject;

	public AddFacetWizard(@Nonnull final String title, final Project project, final FacetWizardStep[] steps)
	{
		super(title, project);
		myCurrentProject = project;
	}

	@Override
	protected void updateStep()
	{

	}

	@Override
	protected void dispose()
	{

	}

	@Override
	protected final void doOKAction()
	{

	}

	private boolean commitStepData(final FacetWizardStep step)
	{
		return true;
	}

	@Override
	protected void doNextAction()
	{

		super.doNextAction();
	}

	@Override
	protected void doPreviousAction()
	{

	}

	@Override
	public void doCancelAction()
	{

	}

   /* private void updateButtons() {
		final boolean isLastStep = isLastStep(getCurrentStep());
        getNextButton().setEnabled(!isLastStep);
        getFinishButton().setEnabled(isLastStep);
        getRootPane().setDefaultButton(isLastStep ? getFinishButton() : getNextButton());
    }      */

	private boolean isLastStep(int step)
	{
		return getNextStep(step) == step;
	}


	@Override
	protected String getHelpID()
	{
		return null;
	}
}
