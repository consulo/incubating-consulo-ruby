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

package org.jetbrains.plugins.ruby.rails.run.configuration;

import com.intellij.execution.junit.RuntimeConfigurationProducer;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiDirectory;
import com.intellij.openapi.module.Module;
import org.jetbrains.plugins.ruby.addins.rspec.run.configuration.RSpecRunConfigurationType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: May 8, 2008
 */
public class RailsRunConfigurationProducer  extends RuntimeConfigurationProducer implements Cloneable {
    private PsiElement mySourceElement;

    public RailsRunConfigurationProducer() {
        super(RailsRunConfigurationType.getInstance());
    }

    @Override
	public PsiElement getSourceElement() {
        return mySourceElement;
    }

    @Override
	@Nullable
    protected RunnerAndConfigurationSettingsImpl createConfigurationByElement(Location location, ConfigurationContext context) {
        final PsiElement element = location.getPsiElement();

        if (!(element instanceof PsiDirectory)
                && !(element instanceof RFile)
                && !(element.getContainingFile() instanceof RFile)) {
            return null;
        }

        final Module module = context.getModule();
        if (module == null || !RailsFacetUtil.hasRailsSupport(module)) {
            return null;
        }
        
        mySourceElement = element;
        return (RunnerAndConfigurationSettingsImpl)RailsRunConfigurationType.getInstance().createConfigurationByLocation(location);
    }

    @Override
	public int compareTo(Object o) {
        return PREFERED;
    }
}
