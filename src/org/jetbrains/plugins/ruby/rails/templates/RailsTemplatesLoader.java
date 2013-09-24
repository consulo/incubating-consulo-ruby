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

package org.jetbrains.plugins.ruby.rails.templates;

import java.util.HashMap;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.rails.RailsSupportLoader;
import org.jetbrains.plugins.ruby.rails.langs.RJSFileType;
import org.jetbrains.plugins.ruby.rails.langs.RXMLFileType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileType;
import org.jetbrains.plugins.ruby.ruby.templates.RORTemplatesSettingsUtil;
import org.jetbrains.plugins.ruby.ruby.templates.RubyTemplatesLoader;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 27, 2007
 */
public class RailsTemplatesLoader implements ApplicationComponent {
    private static final Logger LOG = Logger.getInstance(RailsTemplatesLoader.class.getName());

    public static final String RHTML_TEMPLATE_NAME = RBundle.message("template.rhtml.script.name");
    public static final String RHTML_TEMPLATE_ID = "RHTML_TEMPLATE_ID";

    public static final String RXML_TEMPLATE_NAME = RBundle.message("template.rxml.script.name");
    public static final String RJS_TEMPLATE_NAME = RBundle.message("template.rjs.script.name");

    // To be sure that loader and file types were loaded
    @SuppressWarnings({"UnusedDeclaration", "UnusedParameters"})
    public RailsTemplatesLoader(final RailsSupportLoader loader, final RubyTemplatesLoader rubyTLoader) {
        //Do nothing
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return RComponents.RAILS_TEMPLATES_LOADER;
    }

    public void initComponent() {
        final HashMap<String, String> templateId2Text = RORTemplatesSettingsUtil.loadDefaultSettings();
        initFileTemplate(templateId2Text);
    }

    public void disposeComponent() {
        // Do nothing
    }

    private void initFileTemplate(@NotNull final HashMap<String, String> templateId2Text) {
        final FileTemplateManager fileTemplateManager = FileTemplateManager.getInstance();
        if (fileTemplateManager == null){
            LOG.warn("Cannot load rails template");
            return;
        }
        final FileTemplate rhtml = fileTemplateManager.getTemplate(RHTML_TEMPLATE_NAME);
        if (rhtml == null){
            registerRHTMLFileTemplate(fileTemplateManager, templateId2Text);
        }
        final FileTemplate rxml = fileTemplateManager.getTemplate(RXML_TEMPLATE_NAME);
        if (rxml == null) {
            registerRXMLFileTemplates(fileTemplateManager, templateId2Text);
        }
        final FileTemplate rjs = fileTemplateManager.getTemplate(RJS_TEMPLATE_NAME);
        if (rjs == null) {
            registerRJSFileTemplates(fileTemplateManager, templateId2Text);
        }
    }

    private void registerRHTMLFileTemplate(@NotNull final FileTemplateManager fileTemplateManager,
                                           @NotNull final HashMap<String, String> templateId2Text) {
        final FileTemplate rhtmlTemplate =
                fileTemplateManager.addTemplate(RHTML_TEMPLATE_NAME,
                                                RHTMLFileType.RHTML.getERBExtension());
// setting template as internal
       // ((FileTemplateImpl)rhtmlTemplate).setInternal(true);
// setting template text
        rhtmlTemplate.setText(templateId2Text.get(RHTML_TEMPLATE_ID));
    }

    private void registerRXMLFileTemplates(@NotNull final FileTemplateManager fileTemplateManager,
                                           @NotNull final HashMap<String, String> templateId2Text) {
        final FileTemplate rxmlTemplate =
                fileTemplateManager.addTemplate(RXML_TEMPLATE_NAME,
                                                RXMLFileType.RXML.getBuilderExtension());
// setting template as internal
       // ((FileTemplateImpl)rxmlTemplate).setInternal(true);
// setting template text
        rxmlTemplate.setText(templateId2Text.get(RORTemplatesSettingsUtil.FILE_RUBY_ID));
    }

    private void registerRJSFileTemplates(@NotNull final FileTemplateManager fileTemplateManager,
                                          @NotNull final HashMap<String, String> templateId2Text) {
        final FileTemplate rjsTemplate =
                fileTemplateManager.addTemplate(RJS_TEMPLATE_NAME,
                                                RJSFileType.RJS.getDefaultExtension());
// setting template as internal
       // ((FileTemplateImpl)rjsTemplate).setInternal(true);
// setting template text
        rjsTemplate.setText(templateId2Text.get(RORTemplatesSettingsUtil.FILE_RUBY_ID));
    }
}
