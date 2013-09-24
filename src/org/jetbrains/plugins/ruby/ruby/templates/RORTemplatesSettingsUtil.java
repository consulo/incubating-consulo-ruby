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

import com.intellij.openapi.diagnostic.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jetbrains.annotations.NonNls;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Dec 14, 2007
 */
/*
 Move only with "template_settings.xml"
 */
public class RORTemplatesSettingsUtil {
    private static final Logger LOG = Logger.getInstance(RORTemplatesSettingsUtil.class.getName());

    @NonNls
    private static final String TEMPLATES_SETTINGS_FILE = "template_settings.xml";

    //GENERAL SETTINGS
    @NonNls
    public static final String FILE_RUBY_ID = "FILE_RUBY_ID";

    // for deserialization
    @NonNls
    static final String ID = "id";

    public static HashMap<String, String> loadDefaultSettings() {
        final HashMap<String, String> templateId2Text = new HashMap<String, String>();

        final InputStream inputStream = RORTemplatesSettingsUtil.class.getResourceAsStream(TEMPLATES_SETTINGS_FILE);
        if (inputStream != null) {
            final SAXBuilder parser = new SAXBuilder();
            try {
                final Document doc = parser.build(inputStream);
                final Element root = doc.getRootElement();
                for (Object o : root.getChildren()) {
                    if (o instanceof Element) {
                        Element e = (Element) o;
                        final String id = e.getAttributeValue(ID);
                        final String text = e.getText();
                        templateId2Text.put(id, text);
                    }
                }
                inputStream.close();
            } catch (JDOMException e) {
                LOG.warn(e);
            } catch (IOException e) {
                LOG.warn(e);
            }
        } else {
            LOG.warn("File " + TEMPLATES_SETTINGS_FILE + " wasn't found");
        }
        return templateId2Text;
    }
}
