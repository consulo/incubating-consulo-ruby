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
public class Render_ParamDefTest extends AbstractParamDefTest {
    public void testRenderPartialAbsolute() throws Exception {
        doTestResolveToFile("renderPartial", "app/controllers/absolute.rb", "app/views/item/_part.rhtml");
    }

    public void testRenderPartialRelative() throws Exception {
        doTestResolveToFile("renderPartial", "app/controllers/item_controller.rb", "app/views/item/_part.rhtml");
    }

    public void testRenderPartialRelativeFromView() throws Exception {
        doTestResolveToFile("renderPartial", "app/views/item/full.rhtml", "app/views/item/_part.rhtml");
    }

    public void testRenderPartialCompletion() throws Exception {
        doTestCompletion("renderPartial", "app/controllers/completion_controller.rb",
                "test", "/item/part");
    }

    public void testRenderActionResolve() throws Exception {
        doTestResolveToTarget("renderAction", "app/controllers/test_controller.rb", "app/controllers/test_controller.rb");
    }

    public void testRenderActionCompletion() throws Exception {
        doTestCompletion("renderAction", "app/controllers/completion_controller.rb",
                "edit", "list");
    }
}
