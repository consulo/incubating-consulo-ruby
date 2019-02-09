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

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import javax.annotation.Nonnull;
import com.intellij.ide.wizard.AbstractWizard;
import com.intellij.ide.wizard.CommitStepException;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Mar 15, 2008
 */
//TODO Remove me after JRails UI will be completed!
public class AddFacetWizard extends AbstractWizard<FacetWizardStep>
{
	protected final Project myCurrentProject;

	public AddFacetWizard(@Nonnull final String title, final Project project, final FacetWizardStep[] steps)
	{
		super(title, project);
		myCurrentProject = project;

		for(FacetWizardStep step : steps)
		{
			addStep(step);
		}

		init();

		while(getCurrentStepObject() != null && !getCurrentStepObject().isStepVisible())
		{
			doNextAction();
		}
	}

	@Override
	protected void updateStep()
	{
		final FacetWizardStep currentStep = getCurrentStepObject();
		currentStep.updateStep();

		super.updateStep();

		updateButtons();

		final JButton nextButton = getNextButton();
		final JButton finishButton = getFinishButton();
		final boolean isLastStep = isLastStep(getCurrentStep());

		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				if(!isShowing())
				{
					return;
				}
				final JComponent preferredFocusedComponent = currentStep.getPreferredFocusedComponent();
				if(preferredFocusedComponent != null)
				{
					preferredFocusedComponent.requestFocus();
				}
				else
				{
					if(isLastStep)
					{
						finishButton.requestFocus();
					}
					else
					{
						nextButton.requestFocus();
					}
				}
				getRootPane().setDefaultButton(isLastStep ? finishButton : nextButton);
			}
		});
	}

	@Override
	protected void dispose()
	{
		for(FacetWizardStep step : mySteps)
		{
			step.disposeUIResources();
		}
		super.dispose();
	}

	@Override
	protected final void doOKAction()
	{
		int idx = getCurrentStep();
		try
		{
			do
			{
				final FacetWizardStep step = mySteps.get(idx);
				if(step != getCurrentStepObject())
				{
					step.updateStep();
				}
				if(!commitStepData(step))
				{
					return;
				}
				step.onStepLeaving();
				try
				{
					step._commit(true);
				}
				catch(CommitStepException e)
				{
					String message = e.getMessage();
					if(message != null)
					{
						Messages.showErrorDialog(getCurrentStepComponent(), message);
					}
					return;
				}
				if(!isLastStep(idx))
				{
					idx = getNextStep(idx);
				}
				else
				{
					break;
				}
			}
			while(true);
		}
		finally
		{
			myCurrentStep = idx;
			updateStep();
		}
		super.doOKAction();
	}

	private boolean commitStepData(final FacetWizardStep step)
	{
		try
		{
			if(!step.validate())
			{
				return false;
			}
		}
		catch(ConfigurationException e)
		{
			Messages.showErrorDialog(myCurrentProject, e.getMessage(), e.getTitle());
			return false;
		}
		step.updateDataModel();
		return true;
	}

	@Override
	protected void doNextAction()
	{
		final FacetWizardStep step = getCurrentStepObject();
		if(!commitStepData(step))
		{
			return;
		}
		step.onStepLeaving();
		super.doNextAction();
	}

	@Override
	protected void doPreviousAction()
	{
		final FacetWizardStep step = getCurrentStepObject();
		step.onStepLeaving();
		super.doPreviousAction();
	}

	@Override
	public void doCancelAction()
	{
		final FacetWizardStep step = getCurrentStepObject();
		step.onStepLeaving();
		super.doCancelAction();
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
		FacetWizardStep step = getCurrentStepObject();
		if(step != null)
		{
			return step.getHelpId();
		}
		return null;
	}

	@Override
	protected final int getNextStep(final int step)
	{
		final int stepCount = mySteps.size();

		int nextStepNumber = step + 1;
		while(nextStepNumber < stepCount && !mySteps.get(nextStepNumber).isStepVisible())
		{
			nextStepNumber++;
		}
		return nextStepNumber >= stepCount ? step : nextStepNumber;
	}

	@Override
	protected final int getPreviousStep(final int step)
	{
		int prevStepNumber = step - 1;
		while(prevStepNumber >= 0 && !mySteps.get(prevStepNumber).isStepVisible())
		{
			prevStepNumber--;
		}
		return prevStepNumber < 0 ? step : prevStepNumber;
	}
}
