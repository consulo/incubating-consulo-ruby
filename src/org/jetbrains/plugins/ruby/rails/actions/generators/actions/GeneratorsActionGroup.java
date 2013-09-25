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

package org.jetbrains.plugins.ruby.rails.actions.generators.actions;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.rspec.actions.generators.RSpecSpecialGeneratorsNames;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.special.GenerateControllerAction;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.special.GenerateModelAction;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfiguration;
import org.jetbrains.plugins.ruby.ruby.actions.DataContextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 02.12.2006
 */
public class GeneratorsActionGroup extends ActionGroup {
    protected Map<String, SimpleGeneratorAction> name2Action;

    public GeneratorsActionGroup() {
        super();
        init();
    }

    public GeneratorsActionGroup(String shortName, boolean popup) {
        super(shortName, popup);
        init();
    }

    @Override
	public AnAction[] getChildren(@Nullable final AnActionEvent event) {
        if (event == null){
            return AnActionUtil.NO_ACTIONS;
        }
        final Module module = DataContextUtil.getModule(event.getDataContext());
        if (module == null || !RailsFacetUtil.hasRailsSupport(module)){
            return AnActionUtil.NO_ACTIONS;
        }
        final ArrayList<AnAction> myActions = new ArrayList<AnAction>();

        final BaseRailsFacetConfiguration configuration = RailsFacetUtil.getRailsFacetConfiguration(module);
        assert configuration != null;
        final String[] generators = configuration.getGenerators();
        if (generators == null) {
            return AnActionUtil.NO_ACTIONS;
        }

        for (String generator : generators) {
            if (TextUtil.isEmpty(generator)) {
                continue;
            }
            myActions.add(createGeneratorAction(name2Action, generator));
        }
        myActions.add(Separator.getInstance());

        return myActions.toArray(new AnAction[myActions.size()]);
    }

    @Override
	public void update(@Nullable final AnActionEvent event) {
        if (event == null){ //TODO any sense?
            return;
        }
        final Module module = DataContextUtil.getModule(event.getDataContext());

        // show only on RailsModuleType and valid Ruby SDK with rails installed
        final boolean isVisible = module != null
                                  && RailsFacetUtil.hasRailsSupport(module);
        final boolean isEnabled;
        if (isVisible) {
            final BaseRailsFacetConfiguration configuration = RailsFacetUtil.getRailsFacetConfiguration(module);
            assert configuration != null; //has been already checked above

            isEnabled = (configuration.getGenerators() != null);
        } else {
            isEnabled = false;
        }

        AnActionUtil.updatePresentation(event.getPresentation(),
                                         isVisible, isEnabled);
    }

    protected void init() {
        name2Action = createSpecialGeneratorActionsMap();
    }

    public static Map<String, SimpleGeneratorAction> createSpecialGeneratorActionsMap() {
        final Map<String, SimpleGeneratorAction> name2Action = new HashMap<String, SimpleGeneratorAction>();
        name2Action.put(GenerateControllerAction.GENERATOR_CONTROLLER, new GenerateControllerAction());
        name2Action.put(RSpecSpecialGeneratorsNames.GENERATOR_CONTROLLER, new GenerateControllerAction(RBundle.message("new.generate.rspec.controller.text"), RSpecSpecialGeneratorsNames.GENERATOR_CONTROLLER));
        name2Action.put(GenerateModelAction.GENERATOR_MODEL, new GenerateModelAction());
        name2Action.put(RSpecSpecialGeneratorsNames.GENERATOR_MODEL, new GenerateModelAction(RBundle.message("new.generate.rspec.model.text")));
        return name2Action;
    }

    /**
     * @param name2Action Special registered names and actions
     * @param generator Generator name
     * @return If generator is registered as special then method returns corresponding action otherwise common action.
     */
    public static SimpleGeneratorAction createGeneratorAction(@Nullable final Map<String, SimpleGeneratorAction> name2Action,
                                                              @NotNull final String generator) {
        final SimpleGeneratorAction action = name2Action != null ? name2Action.get(generator)
                                                                 : null;
        if (action != null) {
            return action;
        }
        return new SimpleGeneratorAction(generator);
    }
}
