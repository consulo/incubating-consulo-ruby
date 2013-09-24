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

import org.jetbrains.annotations.NonNls;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jun 9, 2008
 */
public class RailsModule_JDomConstants {
    @NonNls
    public static final String COMPONENT_ELEMENT = "component";
    @NonNls
    public static final String NAME_ATTRUBUTE = "name";
    @NonNls
    public static final String MODULE_TYPE_ATTRIBUTE = "type";

    // Rails
    @NonNls
    public static final String RUBY_MODULE_TYPE = "RUBY_MODULE";
    public static final String RAILS_MODULE_TYPE = "RAILS_MODULE";

    // RModuleSettingsStorage
    @NonNls
    public static final String RMODULE_SETTINGS_STORAGE = "RModuleSettingsStorage";

    // RailsModuleContentRootManager
    public static final String RAILS_MODULE_CONTENT_ROOT_MANAGER = "RailsModuleContentRootManager";
    @NonNls
    public static final String RMCRM_TAG_TEST_URLS = "TEST_URLS";
    @NonNls
    public static final String RMCRM_TAG_USER_URLS = "USER_URLS";
    @NonNls
    static final String RUN_MANAGER_COMPONENT_NAME = "RunManager";
    
    // Run configurations
    @NonNls
    static final String CONFIGURATION_ELEMENT = "configuration";
    @NonNls
    static final String PASS_PAREN_ENVS = "myPassParentEnvs";
    static final String ENVS_TAG = "envs";
    @NonNls
    static final String OLD_SERVER_DEFAULT_NAME = "DEFAULT";
    @NonNls
    static final String OLD_SERVER_TYPE = "SERVER_TYPE";
}
