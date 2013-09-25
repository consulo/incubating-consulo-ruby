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

package org.consulo.yaml.ide;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import org.consulo.yaml.YAMLBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 28, 2008
 */
public class YAMLColorsPage implements ColorSettingsPage {

    private static final String DEMO_TEXT =
        "# Read about fixtures at http://ar.rubyonrails.org/classes/Fixtures.html\n" +
        "static_sidebar:\n" +
        "  id: \"foo\"\n" +
        "  name: 'side_bar'\n" +
        "  staged_position: 1\n" +
        "  blog_id: 1\n" +
        "  config: |+\n" +
        "    --- !map:HashWithIndifferentAccess\n" +
        "      title: Static Sidebar\n" +
        "      body: The body of a static sidebar\n" +
        "  type: StaticSidebar\n" +
        "  type: > some_type_here";

    private static final AttributesDescriptor[] ATTRS = new AttributesDescriptor[]{
            new AttributesDescriptor(YAMLBundle.message("color.settings.yaml.key"), YAMLHighlighter.SCALAR_KEY),
            new AttributesDescriptor(YAMLBundle.message("color.settings.yaml.text"), YAMLHighlighter.SCALAR_TEXT),
            new AttributesDescriptor(YAMLBundle.message("color.settings.yaml.string"), YAMLHighlighter.SCALAR_STRING),
            new AttributesDescriptor(YAMLBundle.message("color.settings.yaml.dstring"), YAMLHighlighter.SCALAR_DSTRING),
            new AttributesDescriptor(YAMLBundle.message("color.settings.yaml.block"), YAMLHighlighter.SCALAR_LIST),
            new AttributesDescriptor(YAMLBundle.message("color.settings.yaml.block2"), YAMLHighlighter.SCALAR_VALUE),
    };

    // Empty still
    private static Map<String, TextAttributesKey> ADDITIONAL_HIGHLIGHT_DESCRIPTORS = new HashMap<String, TextAttributesKey>();

    private static final ColorDescriptor[] COLORS = new ColorDescriptor[0];

    @Override
	@Nullable
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return ADDITIONAL_HIGHLIGHT_DESCRIPTORS;
    }

    @Override
	@NotNull
    public String getDisplayName() {
        return YAMLBundle.message("color.settings.yaml.name");
    }

    @Override
	@NotNull
    public Icon getIcon() {
        return AllIcons.Nodes.DataTables;
    }

    @Override
	@NotNull
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTRS;
    }

    @Override
	@NotNull
    public ColorDescriptor[] getColorDescriptors() {
        return COLORS;
    }

    @Override
	@NotNull
    public SyntaxHighlighter getHighlighter() {
        return new YAMLSyntaxHighlighter();
    }

    @Override
	@NotNull
    public String getDemoText() {
        return DEMO_TEXT;
    }

}
