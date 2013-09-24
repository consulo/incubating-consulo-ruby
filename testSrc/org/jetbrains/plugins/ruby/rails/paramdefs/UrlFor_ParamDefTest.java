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

package org.jetbrains.plugins.ruby.rails.paramdefs;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jul 2, 2008
 */
public class UrlFor_ParamDefTest extends AbstractParamDefTest {
    public void testUrlForControllerResolve() throws Exception {
        doTestResolveToTarget("urlFor", "app/controllers/source_controller.rb", "app/controllers/target_controller.rb");
    }

    public void testUrlForControllerCompletion() throws Exception {
        doTestCompletion("urlFor", "app/controllers/completion.rb",
                "source", "target", "source_action", "target_action", "child/child");
    }

    public void testUrlForActionResolve() throws Exception {
        doTestResolveToTarget("urlFor", "app/controllers/source_action_controller.rb",
                "app/controllers/target_action_controller.rb");
    }

    public void testUrlForActionCompletion() throws Exception {
        doTestCompletion("urlFor", "app/controllers/action_completion.rb",
                "list", "edit");
    }
}
