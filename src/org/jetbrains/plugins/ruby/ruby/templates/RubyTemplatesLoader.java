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

package org.jetbrains.plugins.ruby.ruby.templates;

import java.io.InputStream;
import java.util.HashMap;

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.TemplateContext;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;

/**
 * Created by IntelliJ IDEA.
 * User: oleg, Roman Chernyatchik
 * Date: 26.07.2006
 */
public class RubyTemplatesLoader implements ApplicationComponent {
    private static final Logger LOG = Logger.getInstance(RubyTemplatesLoader.class.getName());
    @NonNls public static final String FILE_RUBY_PATTERN_NAME = "Ruby File Header"; //Change only with template_settings.xml.xml
    @NonNls public static final String FILE_RUBY_PATTERN_ID = "FILE_RUBY_PATTERN_ID";

    @NonNls public static final String FILE_RSPEC_TEMPLATE_NAME = RBundle.message("template.rspec.script.name");
    @NonNls public static final String FILE_RSPEC_TEMPLATE_ID = "FILE_RSPEC_TEMPLATE_ID";

    @NonNls public static final String FILE_TEST_UNIT_TEMPLATE_NAME = RBundle.message("template.testunit.script.name");
    @NonNls public static final String FILE_TEST_UNIT_TEMPLATE_ID = "FILE_TEST_UNIT_TEMPLATE_ID";

    @NonNls public static final String FILE_RUBY_TEMPLATE_NAME = RBundle.message("template.ruby.script.name");// *.rb file genral temlate
    
    @NonNls
    private static final String NAME = "name";
    @NonNls
    private static final String VALUE = "value";
    @NonNls
    private static final String DESCRIPTION = "description";
    @NonNls
    private static final String TO_REFORMAT = "toReformat";
    @NonNls
    private static final String TO_SHORTEN_FQ_NAMES = "toShortenFQNames";
    @NonNls
    private static final String VARIABLE = "variable";
    @NonNls
    private static final String EXPRESSION = "expression";
    @NonNls
    private static final String DEFAULT_VALUE = "defaultValue";
    @NonNls
    private static final String ALWAYS_STOP_AT = "alwaysStopAt";

    private static final String CONTEXT = "context";
    private static final String RUBY_LIVE_TEMPLATE_NAME = "ruby";
    private static final String RSPEC_LIVE_TEMPLATE_NAME = "ruby rspec";

    @NonNls
    private static final String RUBY_TEMPLATES_FILE = "ruby.xml";
    @NonNls
    private static final String RSPEC_TEMPLATES_FILE = "ruby_rspec.xml";

    private FileTemplateManager myFileTemplateManager;
    private TemplateSettings myTemplateSettings;

    public RubyTemplatesLoader(final FileTemplateManager fileTemplateManager,
                               final TemplateSettings templateSettings) {
        myFileTemplateManager = fileTemplateManager;
        myTemplateSettings = templateSettings;
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return RComponents.RUBY_SCRIPT_TEMPLATE_LOADER;
    }


    private void registerRubyFileTemplateIfNotRegistered(final String name, final String text) {
        if (myFileTemplateManager.getTemplate(name) != null) {
            return;
        }
        final FileTemplate template =
                myFileTemplateManager.addTemplate(name, RubyFileType.RUBY.getDefaultExtension());
// setting template as internal
       // ((FileTemplateImpl) template).setInternal(true);
// setting template text
        template.setText(text);
    }

    private void registerRubyFilePatternIfNotRegistered(final String name, final String text) {
        if (myFileTemplateManager.getPattern(name) != null) {
            return;
        }
      /*  final FileTemplate pattern =
                myFileTemplateManager.addPattern(name, RubyFileType.RUBY.getDefaultExtension());
// setting pattern as internal
      //  ((FileTemplateImpl) pattern).setInternal(true);
// setting pattern text
        pattern.setText(text); */
    }

    public void initComponent() {
        final HashMap<String, String> templateId2Text = RORTemplatesSettingsUtil.loadDefaultSettings();

        initFileTemplate(templateId2Text);
        initLiveTemplates();
    }


    /**
     * Loads live templates from ruby.xml
     *
     * @param inputStream  data source
     * @param templateName Template Name
     */
    public void loadTemplates(@NotNull final InputStream inputStream, final String templateName) {
      /*  final SAXBuilder parser = new SAXBuilder();
        try {
            final Document doc = parser.build(inputStream);
            final Element root = doc.getRootElement();
            for (Object o : root.getChildren()) {
                if (o instanceof Element) {
                    final Template t = readExternal((Element) o, templateName);
// if template with the same key is already loaded, we ignore
// TODO: rewrite, when API will be improved!
                    final String key = t.getKey();
                    if (key != null && myTemplateSettings.getTemplate(key) == null) {
                        myTemplateSettings.addTemplate(t);
                    }
                }
            }
        } catch (JDOMException e) {
            LOG.warn(e);
        } catch (IOException e) {
            LOG.warn(e);
        }    */
    }


    /**
     * Reads template content from element
     *
     * @param element      Element, containing template information
     * @param templateName Template name
     * @return Template from element
     */
    @NotNull
    protected Template readExternal(@NotNull final Element element, final String templateName) {
        final String name = element.getAttributeValue(NAME);
        final String value = element.getAttributeValue(VALUE);
        final TemplateImpl template = new TemplateImpl(name, value, templateName);
        template.setDescription(element.getAttributeValue(DESCRIPTION));
        template.setToReformat(Boolean.valueOf(element.getAttributeValue(TO_REFORMAT)));
        template.setToShortenLongNames(Boolean.valueOf(element.getAttributeValue(TO_SHORTEN_FQ_NAMES)));
        TemplateContext context = template.getTemplateContext();

        for (final Object o : element.getChildren(VARIABLE)) {
            Element e = (Element) o;
            String variableName = e.getAttributeValue(NAME);
            String expression = e.getAttributeValue(EXPRESSION);
            String defaultValue = e.getAttributeValue(DEFAULT_VALUE);
            boolean isAlwaysStopAt = Boolean.valueOf(e.getAttributeValue(ALWAYS_STOP_AT));
            template.addVariable(variableName, expression, defaultValue, isAlwaysStopAt);
        }

        final Element contextElement = element.getChild(CONTEXT);
        if (contextElement != null) {
            try {
                DefaultJDOMExternalizer.readExternal(context, contextElement);
            } catch (InvalidDataException e) {
                LOG.warn("Failed reading template context");
            }
        }

//        template.getTemplateContext().readExternal(element.getChild());
        return template;
    }

    private void initLiveTemplates() {
        loadLiveTemplateFile(RUBY_TEMPLATES_FILE, RUBY_LIVE_TEMPLATE_NAME);
        loadLiveTemplateFile(RSPEC_TEMPLATES_FILE, RSPEC_LIVE_TEMPLATE_NAME);
    }

    private void loadLiveTemplateFile(String templatesFile, final String templateName) {
        final InputStream is = getClass().getResourceAsStream(templatesFile);
        if (is != null) {
            loadTemplates(is, templateName);
        } else {
            LOG.warn("Cannot find " + templatesFile + " file with ruby live templates");
        }
    }

    private void initFileTemplate(@NotNull final  HashMap<String, String> templateId2Text) {
        if (myFileTemplateManager == null) {
            LOG.warn("Cannot load ruby template");
            return;
        }
        // pattern
        registerRubyFilePatternIfNotRegistered(FILE_RUBY_PATTERN_NAME,
                                templateId2Text.get(FILE_RUBY_PATTERN_ID));

        //templates
        registerRubyFileTemplateIfNotRegistered(FILE_RUBY_TEMPLATE_NAME,
                templateId2Text.get(RORTemplatesSettingsUtil.FILE_RUBY_ID));
        registerRubyFileTemplateIfNotRegistered(FILE_RSPEC_TEMPLATE_NAME,
                templateId2Text.get(FILE_RSPEC_TEMPLATE_ID));
        registerRubyFileTemplateIfNotRegistered(FILE_TEST_UNIT_TEMPLATE_NAME,
                templateId2Text.get(FILE_TEST_UNIT_TEMPLATE_ID));
    }

    public void disposeComponent() {
    }
}
