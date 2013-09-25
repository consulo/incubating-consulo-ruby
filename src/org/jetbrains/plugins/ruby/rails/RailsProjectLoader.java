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

package org.jetbrains.plugins.ruby.rails;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RComponents;
import com.intellij.openapi.components.ProjectComponent;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 30, 2007
 */
//TODO
public class RailsProjectLoader implements ProjectComponent {
   /* public RailsProjectLoader(final CssSupportLoader loader,
                              final ReferenceProvidersRegistry referenceProvidersRegistry) {

        // this registers standart html ref. provider in rhtml files
        final HtmlInRHTMLReferenceProvider rhtmlReferenceProvider = new HtmlInRHTMLReferenceProvider();
        referenceProvidersRegistry.registerXmlAttributeValueReferenceProvider(
                rhtmlReferenceProvider.getAttributeValues(),
                rhtmlReferenceProvider.getFilter(),
                false,
                rhtmlReferenceProvider);

        //this registers CSS support for rhtml files
        loader.registerCssEnabledFileType(RHTMLFileType.RHTML);
    }     */

    @Override
	public void projectOpened() {
        // Do nothing
    }

    @Override
	public void projectClosed() {
        // Do nothing
    }

    @Override
	@NonNls
    @NotNull
    public String getComponentName() {
        return RComponents.RAILS_PROJECT_LOADER;
    }

    @Override
	public void initComponent() {
        // Do nothing
    }

    @Override
	public void disposeComponent() {
        // Do nothing
    }
}
