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

package org.jetbrains.plugins.ruby.ruby.module.wizard.modes;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.support.utils.RubyUIUtil;
import com.intellij.ide.util.newProjectWizard.StepSequence;
import com.intellij.ide.util.newProjectWizard.modes.WizardMode;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 6, 2007
 */
public class CreateRORFromSourcesMode extends WizardMode {
    @NonNls
    private Map<String, ModuleBuilder> myBuildersMap = new HashMap<String, ModuleBuilder>();

    @NotNull
    public String getDisplayName(WizardContext context) {
        return RBundle.message("project.new.wizard.from.existent.sources.title", context.getPresentationName());
    }

    @NotNull
    public String getDescription(final WizardContext context) {
        return RubyUIUtil.wrapToHtmlWithLabelFont(RBundle.message("project.new.wizard.from.existent.sources.description",
                ApplicationNamesInfo.getInstance().getProductName(), context.getPresentationName()));
    }

    @Nullable
    protected StepSequence createSteps(final WizardContext context, final ModulesProvider modulesProvider) {
        final StepSequence myStepSequence = new StepSequence(null);
	  /*
        //TODO Patch this step!!!!!
        myStepSequence.addCommonStep(new ProjectNameWithTypeStep(context, myStepSequence, this));

        final ModuleType[] allModuleTypes = ModuleTypeManager.getInstance().getRegisteredTypes();
        for (ModuleType type : allModuleTypes) {
            if (!RubyUtil.isRubyModuleType(type)) {
                continue;
            }
            final StepSequence sequence = new StepSequence(myStepSequence);
            myStepSequence.addSpecificSteps(type.getId(), sequence);
            final ModuleBuilder builder = type.createModuleBuilder();
            myBuildersMap.put(type.getId(), builder);
            //noinspection unchecked
            final ModuleWizardStep[] steps = type.createWizardSteps(context, builder, modulesProvider);
            for (ModuleWizardStep step : steps) {
                sequence.addCommonStep(step);
            }
            if (FrameworkSupportUtil.hasProviders(type)) {
                sequence.addCommonStep(new SupportForFrameworksStep(builder));
            }
        }   */
        return myStepSequence;
    }

    public boolean isAvailable(WizardContext context) {
        return context.getProject() == null;
    }

    public ModuleBuilder getModuleBuilder() {
        return myBuildersMap.get(getSelectedType());
    }

    @Nullable
    public JComponent getAdditionalSettings() {
        return null;
    }

    public void onChosen(final boolean enabled) {

    }

    public void dispose() {
        super.dispose();
        myBuildersMap.clear();
    }
}
