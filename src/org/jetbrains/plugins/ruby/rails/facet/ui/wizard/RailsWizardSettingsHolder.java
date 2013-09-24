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

package org.jetbrains.plugins.ruby.rails.facet.ui.wizard;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Mar 15, 2008
 */
public interface RailsWizardSettingsHolder extends RubyWizardSettingsHolder {

    @Nullable
    public String getRailsApplicationHomeDirRelativePath();
    public void setRailsApplicationHomeDirRelativePath(@Nullable final String relativePath);

    public Generate getAppGenerateWay();
    public void setAppGenerateWay(@NotNull final Generate generateWay);

    public void setRSpecConf(@NotNull final RSpecConfiguration RSpecConf);
    @NotNull
    public RSpecConfiguration getRSpecConf();

    /**
     * @return Name of DB to preconfigure or null to use default configuration
     */
    @Nullable
    public String getDBNameToPreconfigure();

    /**
     * @param dbName Name of DB to preconfigure or null to use default configuration
     */
    public void setDBNameToPreconfigure(@Nullable final String dbName);

    enum Generate {
        NEW, NOT
    }

    interface RSpecConfiguration {
        public boolean enableRSpecSupport();
        public boolean enableRSpecRailsSupport();

        public boolean shouldInstallRSpecPlugin();
        public boolean shouldInstallRSpecRailsPlugin();

        @NotNull
        public String getRSpecArgs();

        @NotNull
        public String getRSpecRailsArgs();

        @Nullable
        public String getSvnPath();
    }
}
