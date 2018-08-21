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

package org.jetbrains.plugins.ruby.rails.actions.generators;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsUtil;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.SimpleGeneratorInputValidator;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.special.ActionInputValidator;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.special.ActionPanel;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.special.ControllerInputValidator;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.special.GenerateControllerPanel;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.Output;
import org.jetbrains.plugins.ruby.ruby.run.RubyScriptRunner;
import org.jetbrains.plugins.ruby.ruby.run.Runner;
import org.jetbrains.plugins.ruby.settings.RProjectUtil;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Ref;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 27.11.2006
 */
public class GenerateDialogs
{
	protected static class GenerateActionDialog extends GenerateDialog
	{

		public GenerateActionDialog(final Module module, final String title, final ActionInputValidator validator)
		{
			super(module, title, validator);
		}

		@Override
		protected void initDialog(final String title)
		{
			setTitle(title);
			setButtonsAlignment(SwingUtilities.RIGHT);
			init();
		}

		/**
		 * Creates panel with all necessary ui elements
		 *
		 * @return GeneratorPanel
		 */
		@Override
		@NotNull
		protected GeneratorPanel createGeneratorContent()
		{
			return new ActionPanel(((ActionInputValidator) myValidator).getRelativePath());
		}
	}

	protected static class GenerateControllerDialog extends GenerateDialog
	{

		public GenerateControllerDialog(final Module module, final String title, final ControllerInputValidator validator)
		{
			super(module, title, validator);
		}

		@Override
		protected void initDialog(final String title)
		{
			setTitle(title);
			setButtonsAlignment(SwingUtilities.RIGHT);
			init();
		}

		/**
		 * Creates panel with all necessary ui elements
		 *
		 * @return GeneratorPanel
		 */
		@Override
		@NotNull
		protected GeneratorPanel createGeneratorContent()
		{
			final BaseRailsFacetConfiguration configuration = RailsFacetUtil.getRailsFacetConfiguration(myModule);
			assert configuration != null;

			final String cRoot = configuration.getPaths().getControllerRootURL();
			final String cRootPath = RailsUtil.getPathRelativeToTailsApplicationRoot(cRoot, myModule);
			assert cRootPath != null;

			final String relativePath = ((ControllerInputValidator) myValidator).getRelativePath();
			return new GenerateControllerPanel(cRootPath, relativePath);
		}
	}

	protected static class GenerateDialog extends DialogWrapper
	{
		protected final Module myModule;
		protected final SimpleGeneratorInputValidator myValidator;
		private final Ref<Output> helpData = new Ref<Output>();
		private GeneratorPanel myGenerateDialogContent;

		public GenerateDialog(@NotNull final Module module, final String title, final SimpleGeneratorInputValidator validator)
		{
			super(module.getProject(), false);
			myModule = module;
			myValidator = validator;
			initDialog(title);
		}

		public String getGeneratorArguments()
		{
			if(getExitCode() == 0)
			{
				return getGeneratorArgs();
			}
			else
			{
				return null;
			}
		}

		@Override
		protected Action[] createActions()
		{
			return new Action[]{
					getOKAction(),
					getCancelAction(),
					getHelpAction()
			};
		}

		@Override
		protected JComponent createCenterPanel()
		{
			final GeneratorOptions options = RProjectUtil.getGeneratorsOptions(myModule.getProject());
			myGenerateDialogContent = createGeneratorContent();
			myGenerateDialogContent.initPanel(options);

			return myGenerateDialogContent.getContent();
		}

		/**
		 * Creates panel with all necessary ui elements
		 *
		 * @return GeneratorPanel
		 */
		@NotNull
		protected GeneratorPanel createGeneratorContent()
		{
			return new SimpleGeneratorPanel();
		}

		@Override
		protected void doOKAction()
		{
			final String generatorArgs = getGeneratorArgs();
			final String mainArgument = getMainArgument();

			myGenerateDialogContent.saveSettings(myModule.getProject());

			if(myValidator == null)
			{
				close(OK_EXIT_CODE);
			}
			else
			{
				if(myValidator.checkInput(mainArgument) && myValidator.canClose(generatorArgs))
				{
					close(OK_EXIT_CODE);
					myValidator.invokeAction(generatorArgs, mainArgument);
				}
			}
		}

		@Override
		protected void doHelpAction()
		{
			if(helpData.get() == null)
			{
				readHelpData();
			}
			final String title = RBundle.message("dialog.generate.common.help.title", myValidator.getGeneratorAction().getGeneratorName());

			showGeneratorHelpDialog(myModule.getProject(), title, helpData.get());
		}

		@Override
		public JComponent getPreferredFocusedComponent()
		{
			return myGenerateDialogContent.getPreferredFocusedComponent();
		}

		protected void initDialog(final String title)
		{
			setTitle(title);
			setButtonsAlignment(SwingUtilities.CENTER);
			init();
		}

		private String getGeneratorArgs()
		{
			return myGenerateDialogContent.getGeneratorArgs();
		}

		/**
		 * @return Data that must be checked by validator
		 */
		protected String getMainArgument()
		{
			return myGenerateDialogContent.getMainArgument();
		}

		private void readHelpData()
		{
			final String title = RBundle.message("progress.dialog.generate.common.help.wait.title");

			final ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
			if(indicator != null)
			{
				indicator.setText(RBundle.message("progress.indicator.title.please.wait"));
			}

			final Sdk sdk = RModuleUtil.getModuleOrJRubyFacetSdk(myModule);
			final String workingDir = RailsFacetUtil.getRailsAppHomeDirPath(myModule);
			assert workingDir != null;
			final String name = myValidator.getGeneratorAction().getGeneratorName();


			helpData.set(RubyScriptRunner.runRubyScript(sdk, myModule.getProject(), GeneratorsUtil.GENERATE_SCRIPT, workingDir, new Runner.ModalProgressMode(title), false, null, name));
		}
	}

	/**
	 * This dialog shows <code>JTextPane</code> and button ok.
	 * <code>JTextPane</code>  contains information from <code>Output</code>.
	 */
	protected static class GeneratorHelpDialog extends DialogWrapper
	{
		protected final Output myOutput;

		public GeneratorHelpDialog(final Project project, final String title, final Output output)
		{
			super(project, false);
			myOutput = output;
			initDialog(title);
		}

		@Override
		protected Action[] createActions()
		{
			return new Action[]{getOKAction()};
		}

		@Override
		protected JComponent createCenterPanel()
		{
			return new GeneratorHelpPanel(myOutput).getContent();
		}

		private void initDialog(String title)
		{
			setTitle(title);
			setButtonsAlignment(SwingUtilities.CENTER);
			init();
		}
	}

	@SuppressWarnings({"UnusedReturnValue"})
	public static int showGenerateActionDialog(final Module module, final String title, final ActionInputValidator validator)
	{
		int result;
		final Application application = ApplicationManager.getApplication();
		if(application.isUnitTestMode() || application.isHeadlessEnvironment())
		{
			//TODO
			//        return ourTestImplementation.show(message);
			result = 0;
		}
		else
		{
			final GenerateActionDialog dialog = new GenerateActionDialog(module, title, validator);
			dialog.show();
			result = dialog.getExitCode();
		}
		return result;
	}

	@SuppressWarnings({"UnusedReturnValue"})
	public static int showGenerateControllerDialog(final Module module, final String title, final ControllerInputValidator validator)
	{
		int result;
		final Application application = ApplicationManager.getApplication();
		if(application.isUnitTestMode() || application.isHeadlessEnvironment())
		{
			//TODO
			//        return ourTestImplementation.show(message);
			result = 0;
		}
		else
		{
			GenerateControllerDialog dialog = new GenerateControllerDialog(module, title, validator);
			dialog.show();
			result = dialog.getExitCode();
		}
		return result;
	}

	@SuppressWarnings({"UnusedReturnValue"})
	public static int showGenerateDialog(final Module module, final String title, final SimpleGeneratorInputValidator validator)
	{
		int result;
		final Application application = ApplicationManager.getApplication();
		if(application.isUnitTestMode() || application.isHeadlessEnvironment())
		{
			//TODO
			//        return ourTestImplementation.show(message);
			result = 0;
		}
		else
		{
			GenerateDialog dialog = new GenerateDialog(module, title, validator);
			dialog.show();
			result = dialog.getExitCode();
		}
		return result;
	}

	@SuppressWarnings({"UnusedReturnValue"})
	public static int showGeneratorHelpDialog(final Project project, final String title, final Output output)
	{
		int result;
		final Application application = ApplicationManager.getApplication();
		if(application.isUnitTestMode() || application.isHeadlessEnvironment())
		{
			//TODO
			//        return ourTestImplementation.show(message);
			result = 0;
		}
		else
		{
			GeneratorHelpDialog dialog = new GeneratorHelpDialog(project, title, output);
			dialog.pack();

			final Container contentPane = dialog.getContentPane();
			final Dimension size = contentPane.getSize();
			contentPane.setPreferredSize(new Dimension(size.width, Math.min(size.height, 500)));
			dialog.show();
			result = dialog.getExitCode();
		}
		return result;
	}
}