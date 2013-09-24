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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache;

import org.jetbrains.annotations.NonNls;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 15, 2008
 */
public class StubsUrls {
    @NonNls
    public static final String BUILT_IN_RB = "/builtin.rb";
    @NonNls
    public static final String JRUBY_BUILT_IN_RB = "/jruby_builtin.rb";
    @NonNls
    public static final String FULL_RAILS_RB = "/full_rails.rb";
    @NonNls
    public static final String JRUBY_FULL_RAILS_RB = "/jruby_full_rails.rb";

    @NonNls
    public static final String ACTIVE_SUPPORT_ACTION_CONTROLLER_RB = "/active_support_action_controller.rb";
    @NonNls
    public static final String ACTIVE_SUPPORT_ACTION_VIEW_RB = "/active_support_action_view.rb";
    @NonNls
    public static final String ACTIVE_SUPPORT_ACTIVE_RECORD_RB = "/active_support_active_record.rb";
    @NonNls
    public static final String ACTIVE_SUPPORT_ACTION_MAILER_RB = "/active_support_action_mailer.rb";
    @NonNls
    public static final String ACTIVE_SUPPORT_ACTION_MAILER_ADDON_RB = "/my_action_mailer_addon.rb";
    @NonNls
    public static final String ACTIVE_SUPPORT_ACTION_WEB_SERVICE_RB = "/active_support_action_web_service.rb";
}
