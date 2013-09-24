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

import com.intellij.ide.impl.convert.ConverterFactory;
import com.intellij.ide.impl.convert.ProjectConverter;
import com.intellij.ide.impl.convert.AllowedFeaturesFilter;
import com.intellij.ide.impl.convert.ModuleConverter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NonNls;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jun 9, 2008
 */
public class RailsModule_ConverterFactory implements ConverterFactory {
    @NonNls
    private static final String FACTORY_ID = "ror-modules";

    @NotNull
    @NonNls
    public String getId() {
        return FACTORY_ID;
    }

    @NotNull
    public ProjectConverter createConverter(final String projectFilePath) {
        return new RailsModule_ProjectConverter(projectFilePath);
    }

    @NotNull
    public AllowedFeaturesFilter getUnconvertedProjectFeaturesFilter() {
        return new RailsModule_AllowedFeaturesFilter();
    }

    @Nullable
    public ModuleConverter createModuleConverter() {
        return new RailsModule_ModuleConverter();
    }
}
