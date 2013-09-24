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

package org.jetbrains.plugins.ruby.rails.facet.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdk;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 18, 2008
 */
public interface BaseRailsFacetConfigurationLowLevel extends BaseRailsFacetConfiguration {

    public void loadGenerators(final boolean forceRegenerate, @Nullable final ProjectJdk sdk);
    public void loadRakeTasks(final boolean forceRegenerate, final ProjectJdk sdk);

    public void setRailsApplicationRootPath(@NotNull final String rootPath);
    public void setModule(@NotNull final Module uncommitedModule);

    @Nullable
    public String getNullableRailsApplicationRootPath();

    public void setSdk(@Nullable final ProjectJdk sdk);

    @Nullable
    public ProjectJdk getSdk();
    @Nullable
    public Module getModule();

    public void setInitialized();
}
