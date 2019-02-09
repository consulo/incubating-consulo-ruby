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

package org.jetbrains.plugins.ruby.rails.actions.shortcuts;

import java.util.Map;

import javax.swing.Icon;

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.GeneratorsActionGroup;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.SimpleGeneratorAction;
import org.jetbrains.plugins.ruby.rails.actions.rake.RakeUtil;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTask;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfiguration;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.ex.KeymapManagerEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.Messages;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 23.03.2007
 */
@SuppressWarnings({"ComponentNotRegistered"})
public class ShortcutAction extends AnAction
{
	private String myCmdName;
	private ShortcutsTreeState myState;
	public Map<String, SimpleGeneratorAction> name2Action;

	public ShortcutAction(@Nonnull final String name, @Nonnull final String cmd, final Icon icon, final ShortcutsTreeState state)
	{
		super(name, getDescription(state, cmd), icon);
		myCmdName = cmd;
		myState = state;
		name2Action = GeneratorsActionGroup.createSpecialGeneratorActionsMap();
	}

	public static void unregisterInKeyMap(@Nonnull final String actionId)
	{
		final Keymap[] keymaps = (KeymapManagerEx.getInstanceEx()).getAllKeymaps();
		for(Keymap keymap : keymaps)
		{
			keymap.removeAllActionShortcuts(actionId);
		}
		KeymapManagerEx.getInstanceEx().getActiveKeymap().removeAllActionShortcuts(actionId);
		ActionManager.getInstance().unregisterAction(actionId);
	}

	@Override
	public void actionPerformed(final AnActionEvent e)
	{
		final DataContext dataContext = e.getDataContext();

		final Module module = dataContext.getData(CommonDataKeys.MODULE);
		if(module == null || !RailsFacetUtil.hasRailsSupport(module))
		{
			return;
		}

		final BaseRailsFacetConfiguration facetConfiguration = RailsFacetUtil.getRailsFacetConfiguration(module);
		assert facetConfiguration != null; //Not null for modules with Rails Support
		String msg = null;
		switch(myState)
		{
			case GENERATORS_SUBTREE:
				final String[] generators = facetConfiguration.getGenerators();
				if(executeGenerateAction(generators, e))
				{
					return;
				}
				msg = RBundle.message("action.registered.shortcut.execute.disabled.generators.msg", myCmdName, module.getName());
				break;
			case RAKE_SUBTREE:
				final RakeTask rootRakeTask = facetConfiguration.getRakeTasks();
				final RakeTask task = rootRakeTask != null ? RakeUtil.findTaksByFullCmd(rootRakeTask, myCmdName) : null;
				//If our task is valid for current module
				if(task != null)
				{
					RakeUtil.runRakeTask(dataContext, task);
					return;
				}
				msg = RBundle.message("action.registered.shortcut.execute.disabled.raketasks.msg", myCmdName, module.getName());
				break;
		}
		if(msg != null)
		{
			Messages.showErrorDialog(msg, RBundle.message("action.registered.shortcut.execute.disabled.title"));
		}
	}

	public void registerInKeyMap(@Nonnull final String actionId)
	{
		final PluginId id = PluginId.getId(RComponents.PLUGIN_ID);
		ActionManager.getInstance().registerAction(actionId, this, id);
	}

	private static String getDescription(final ShortcutsTreeState state, final String cmdName)
	{
		switch(state)
		{
			case GENERATORS_SUBTREE:
				return RBundle.message("dialog.register.shortcut.action.description.generators", cmdName);
			case RAKE_SUBTREE:
				return RBundle.message("dialog.register.shortcut.action.description.raketasks", cmdName);
			default:
				return null;
		}
	}

	private boolean executeGenerateAction(final String[] generators, final AnActionEvent e)
	{
		if(generators == null)
		{
			return false;
		}

		for(String generator : generators)
		{
			//If our generator is valid for current module
			if(myCmdName.equals(generator))
			{
				final SimpleGeneratorAction action = GeneratorsActionGroup.createGeneratorAction(name2Action, myCmdName);
				//Check if generate action is enabled.
				action.update(e);
				final Presentation presentation = action.getTemplatePresentation();
				if(presentation.isEnabled() && presentation.isVisible())
				{
					action.actionPerformed(e);
					return true;
				}
				break;
			}
		}
		return false;
	}
}
