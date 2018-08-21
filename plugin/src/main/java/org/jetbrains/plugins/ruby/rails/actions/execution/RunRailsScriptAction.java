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

package org.jetbrains.plugins.ruby.rails.actions.execution;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsUtil;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.AnActionUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.run.RailsScriptRunner;
import org.jetbrains.plugins.ruby.rails.run.filters.GeneratorsLinksFilter;
import org.jetbrains.plugins.ruby.ruby.actions.DataContextUtil;
import org.jetbrains.plugins.ruby.ruby.run.RubyScriptRunnerArgumentsProvider;
import org.jetbrains.plugins.ruby.ruby.run.filters.FileLinksFilterUtil;
import org.jetbrains.plugins.ruby.ruby.run.filters.RFileLinksFilter;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.RawCommandLineEditor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jan 17, 2008
 */
public class RunRailsScriptAction extends AnAction
{
	private static Logger LOG = Logger.getInstance(RunRailsScriptAction.class.getName());

	@Override
	public void actionPerformed(@NotNull final AnActionEvent event)
	{
		final Module module = DataContextUtil.getModule(event.getDataContext());
		assert module != null;

		//Save all opened documents
		FileDocumentManager.getInstance().saveAllDocuments();

		//Show dialog
		final VirtualFile scriptsFolder = RailsUtil.getScriptsRoot(module);
		LOG.assertTrue(checkScriptsDir(module), "./script directory doesn't exist!");

		//noinspection ConstantConditions
		final RunRailsScriptDialog dialog = new RunRailsScriptDialog(module.getProject(), module.getName(), scriptsFolder);
		dialog.setTitle(RBundle.message("rails.actions.execution.run.rails.script.title"));
		dialog.show();
		if(dialog.isOK())
		{
			final String scriptName = dialog.getScriptName();
			final String arguments = dialog.getArguments();

			if(!checkScriptsDir(module))
			{
				final String errorMsg = RBundle.message("rails.actions.execution.run.rails.script.error.no.scripts.folder", module.getName());
				final String errorTitle = RBundle.message("rails.actions.execution.run.rails.script.error.title");
				Messages.showErrorDialog(module.getProject(), errorMsg, errorTitle);
			}

			final String scriptFolderPath = scriptsFolder.getPath();
			File scriptFile = FileLinksFilterUtil.getFileByRubyLink(scriptFolderPath + File.separator + scriptName);
			if(scriptFile == null)
			{
				scriptFile = FileLinksFilterUtil.getFileByRubyLink(scriptName);
			}
			if(scriptFile == null)
			{
				final String errorMsg = RBundle.message("rails.actions.execution.run.rails.script.error.script.want.found", scriptName);
				final String errorTitle = RBundle.message("rails.actions.execution.run.rails.script.error.title");
				Messages.showErrorDialog(module.getProject(), errorMsg, errorTitle);
				return;
			}

			runRailsScript(module, scriptFile.getPath(), arguments);
		}
	}

	private void runRailsScript(final Module module, final String scriptName, final String arguments)
	{
		final String title = RBundle.message("rails.actions.execution.run.rails.script.title");
		final Filter[] filters = {
				new RFileLinksFilter(module),
				new GeneratorsLinksFilter(module)
		};

		final String[] params = getRailsScriptRunParams(scriptName, arguments);
		final RubyScriptRunnerArgumentsProvider provider = new RubyScriptRunnerArgumentsProvider(params, null, null);

		final ProcessAdapter processListener = new ProcessAdapter()
		{
			@Override
			public void processTerminated(ProcessEvent event)
			{
				RailsFacetUtil.refreshRailsAppHomeContent(module);
			}
		};

		RailsScriptRunner.runRailsScriptInCosole(module, processListener, filters, null, true, title, provider, null);
	}

	@Override
	public void update(@NotNull final AnActionEvent event)
	{
		final Module module = DataContextUtil.getModule(event.getDataContext());

		// show only on RailsModuleType and valid Ruby SDK with rails installed
		final boolean isVisible = module != null && RailsFacetUtil.hasRailsSupport(module);
		final boolean isEnabled = isVisible && checkScriptsDir(module);

		AnActionUtil.updatePresentation(event.getPresentation(), isVisible, isEnabled);
	}

	private boolean checkScriptsDir(final Module module)
	{
		final VirtualFile scriptsRoot = RailsUtil.getScriptsRoot(module);
		return scriptsRoot != null && scriptsRoot.isValid();
	}

	private String[] getRailsScriptRunParams(final String scriptName, final String arguments)
	{
		final String[] params;
		final List<String> parameters = new LinkedList<String>();

		parameters.add(scriptName);
		RubyScriptRunnerArgumentsProvider.collectArguments(arguments, parameters);
		params = parameters.toArray(new String[parameters.size()]);
		return params;
	}

	private static class RunRailsScriptDialog extends DialogWrapper
	{
		private final Project myProject;
		private final String myModuleName;
		private final VirtualFile myModuleScriptFolder;
		private TextFieldWithBrowseButton myScriptNameComponent;
		private RawCommandLineEditor myArgumentsComponent;

		protected RunRailsScriptDialog(final Project project, @NotNull final String moduleName, @NotNull final VirtualFile moduleScriptFolder)
		{
			super(project, true);
			myProject = project;
			myModuleName = moduleName;
			myModuleScriptFolder = moduleScriptFolder;
			init();
		}

		@NotNull
		public String getArguments()
		{
			assert myArgumentsComponent != null;
			return myArgumentsComponent.getText().trim();
		}

		@NotNull
		public String getScriptName()
		{
			assert myScriptNameComponent != null;
			return myScriptNameComponent.getText().trim();
		}

		@Override
		@Nullable
		protected JComponent createCenterPanel()
		{
			final RunRailsScriptForm myRunRailsScriptForm = new RunRailsScriptForm(myProject, myModuleName, myModuleScriptFolder);
			myScriptNameComponent = myRunRailsScriptForm.getScriptNameComponent();
			myArgumentsComponent = myRunRailsScriptForm.getArgumentsComponent();

			return myRunRailsScriptForm.getContentPane();
		}

		@Override
		protected Action[] createActions()
		{
			setOKButtonText(RBundle.message("rails.actions.execution.run.rails.script.dialog.button.ok.caption"));
			return new Action[]{
					getOKAction(),
					getCancelAction()
			};
		}
	}
}
