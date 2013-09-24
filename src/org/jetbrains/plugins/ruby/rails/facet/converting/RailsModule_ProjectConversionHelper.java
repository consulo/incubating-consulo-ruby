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

package org.jetbrains.plugins.ruby.rails.facet.converting;

import com.intellij.ide.impl.convert.ProjectConversionHelper;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jun 9, 2008
 */
public class RailsModule_ProjectConversionHelper implements ProjectConversionHelper {
    private RailsModule_ConvertingContext myContext;

    public RailsModule_ProjectConversionHelper(final RailsModule_ConvertingContext context) {
        myContext = context;
    }

    public void convertModuleRootToNewFormat(final Element root, final String moduleName) {
        RailsModule_ConvertingUtil.convertRootElement(root, moduleName, myContext);
    }

    public void convertWorkspaceRootToNewFormat(final Element root) {
        RailsModule_RunConfigurationConvertingUtil.convertWorkspace(root, myContext);
    }

    public void convertModuleRootToOldFormat(final Element root, final String moduleName) {
        RailsModule_BackwardConversionUtil.convertRootElement(root, moduleName);
    }

    public void convertWorkspaceRootToOldFormat(final Element root) {
        RailsModule_RunConfigurationConvertingUtil.convertWorkspaceBackward(root);
    }
}