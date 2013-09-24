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
public class Stylesheet_ParamDefTest extends AbstractParamDefTest {

    public void testStylesheetLinkTagResolve() throws Exception {
        doTestResolveToFile("stylesheetLinkTag", "app/views/test/list1.rhtml",
                "public/stylesheets/scaffold.css");
        doTestResolveToFile("stylesheetLinkTag", "app/views/test/list2.rhtml",
                "public/css_folder/css1.css");
        doTestResolveToFile("stylesheetLinkTag", "app/views/test/list3.rhtml",
                "public/stylesheets/css_sub_folder/css2.css");
    }

    public void testStylesheetLinkTagResolveNaweWithCssExt() throws Exception {
        doTestResolveToFile("stylesheetLinkTag", "app/views/test/list_name_with_css.rhtml",
                "public/stylesheets/css_sub_folder/css2.css");
    }


    public void testStylesheetLinkTagCompletion() throws Exception {
        doTestCompletion("stylesheetLinkTag", "app/views/test/complete.rhtml",
                "scaffold", "depot", "/css_folder/css1", "css_sub_folder/css2");
    }

    public void testStylesheetLinkTagResolveMulti() throws Exception {
        doTestResolveToFile("stylesheetLinkTag", "app/views/test/list_multi.rhtml",
                "public/stylesheets/depot.css");
    }
}
