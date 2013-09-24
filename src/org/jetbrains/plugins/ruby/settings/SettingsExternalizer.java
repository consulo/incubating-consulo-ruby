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

package org.jetbrains.plugins.ruby.settings;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 8, 2006
 */
public abstract class SettingsExternalizer {
    @NonNls public static final String NAME =          "NAME";
    @NonNls public static final String VALUE =         "VALUE";

    /**
     * @return Special ID for each externalizer
     */
    public abstract String getID();

    /**
     * Creates Map, that contains options by name
     * @param elem element to extract options
     * @return Map, that contains options by name
     */
    protected Map<String, String> buildOptionsByElement(@NotNull Element elem) {
        //noinspection unchecked
        return buildOptionsByName(elem.getChildren(getID()));
    }

    protected Map<String, String> buildOptionsByName(List<Element> children) {
        Map<String, String> options = new HashMap<String, String>();

        for (Element elem : children) {
            options.put(elem.getAttribute(NAME).getValue(), elem.getAttribute(VALUE).getValue());
        }

        return options;
    }

    /**
     * Gets attribute from Element.
     * @param key attribute Name
     * @param element xml element
     * @return value
     */
    @NotNull
    protected String getAttributeFromElement(@NotNull final String key,
                                  @NotNull final Element element) {
        final Attribute attr = element.getAttribute(key);
        return attr != null ? attr.getValue() : TextUtil.EMPTY_STRING;
    }

    /**
     * Stores attribute in Element
     * @param key attribute name
     * @param value value
     * @param element xml element
     */
    protected void storeAttributeInElement(@NotNull final String key,
                                           @Nullable final String value,
                                           @NotNull final Element element) {
        element.setAttribute(key, value != null ? value : TextUtil.EMPTY_STRING);
    }

    /**
     * Writes option to given element
     * @param name name of option
     * @param value value of option
     * @param elem elem to write
     */
    public void writeOption(@Nullable final String name, @Nullable final String value,
                            @NotNull final Element elem) {
        Element option = new Element(getID());
        option.setAttribute(NAME, name == null ? "" : name);
        option.setAttribute(VALUE, value == null ? "" : value);
        elem.addContent(option);
    }
}
