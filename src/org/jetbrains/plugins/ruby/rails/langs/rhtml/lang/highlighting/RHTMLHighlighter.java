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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.highlighting;

import java.awt.Color;

import org.jetbrains.annotations.NonNls;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 16, 2007
 */
public class RHTMLHighlighter {
    @NonNls
    private static final String RHTML_COMMENT_ID = "RHTML_COMMENT_ID";
    @NonNls
    private static final String RHTML_SCRIPTING_BACKGROUND_ID = "RHTML_SCRIPTING_BACKGROUND_ID";
    @NonNls
    private static final String RHTML_OMIT_NEW_LINE_ID = "RHTML_OMIT_NEW_LINE_ID";
    @NonNls
    private static final String RHTML_SCRIPTLET_START_ID = "RHTML_SCRIPTLET_START_ID";
    @NonNls
    private static final String RHTML_SCRIPTLET_END_ID = "RHTML_SCRIPTLET_END_ID";
    @NonNls
    private static final String RHTML_EXPRESSION_START_ID = "RHTML_EXPRESSION_START_ID";
    @NonNls
    private static final String RHTML_EXPRESSION_END_ID = "RHTML_EXPRESSION_END_ID";
    @NonNls
    private static final String FLEX_ERROR_ID = "FLEX_ERROR_ID";

    // Text default attrs
    private static final TextAttributes COMMENT_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.LINE_COMMENT.getDefaultAttributes().clone();
    private static final TextAttributes OMIT_NEW_LINE_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE.getDefaultAttributes().clone();

    private static final TextAttributes RHTML_SCRIPTLET_START_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.MARKUP_TAG.getDefaultAttributes().clone();
    private static final TextAttributes RHTML_SCRIPTLET_END_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.MARKUP_TAG.getDefaultAttributes().clone();
    private static final TextAttributes RHTML_EXPRESSION_START_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.MARKUP_TAG.getDefaultAttributes().clone();
    private static final TextAttributes RHTML_EXPRESSION_END_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.MARKUP_TAG.getDefaultAttributes().clone();

    private static final TextAttributes FLEX_ERROR_DEFAULT_ATTRS = HighlighterColors.BAD_CHARACTER.getDefaultAttributes().clone();

    private static final TextAttributes RHTML_SCRIPTING_BACKGROUND_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR.getDefaultAttributes().clone();

    // fixes for default attributes
    static {
        //Init additional color, font types and effects
        RHTML_SCRIPTING_BACKGROUND_DEFAULT_ATTRS.setBackgroundColor(new Color(242, 232, 228));
        RHTML_SCRIPTLET_START_DEFAULT_ATTRS.setBackgroundColor(new Color(242, 232, 228));
        RHTML_SCRIPTLET_END_DEFAULT_ATTRS.setBackgroundColor(new Color(242, 232, 228));
        OMIT_NEW_LINE_DEFAULT_ATTRS.setBackgroundColor(new Color(242, 232, 228));

        RHTML_EXPRESSION_START_DEFAULT_ATTRS.setBackgroundColor(DefaultLanguageHighlighterColors.MARKUP_TAG.getDefaultAttributes().getBackgroundColor());
        RHTML_EXPRESSION_END_DEFAULT_ATTRS.setBackgroundColor(DefaultLanguageHighlighterColors.MARKUP_TAG.getDefaultAttributes().getBackgroundColor());

    }

    //---------
    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(
            RHTML_COMMENT_ID,
            COMMENT_DEFAULT_ATTRS
    );
    public static final TextAttributesKey OMIT_NEW_LINE = TextAttributesKey.createTextAttributesKey(
            RHTML_OMIT_NEW_LINE_ID,
            OMIT_NEW_LINE_DEFAULT_ATTRS
    );
    public static final TextAttributesKey RHTML_SCRIPTLET_START = TextAttributesKey.createTextAttributesKey(
            RHTML_SCRIPTLET_START_ID,
            RHTML_SCRIPTLET_START_DEFAULT_ATTRS
    );
    public static final TextAttributesKey RHTML_SCRIPTLET_END = TextAttributesKey.createTextAttributesKey(
            RHTML_SCRIPTLET_END_ID,
            RHTML_SCRIPTLET_END_DEFAULT_ATTRS
    );
    public static final TextAttributesKey RHTML_EXPRESSION_START = TextAttributesKey.createTextAttributesKey(
            RHTML_EXPRESSION_START_ID,
            RHTML_EXPRESSION_START_DEFAULT_ATTRS
    );
    public static final TextAttributesKey RHTML_EXPRESSION_END = TextAttributesKey.createTextAttributesKey(
            RHTML_EXPRESSION_END_ID,
            RHTML_EXPRESSION_END_DEFAULT_ATTRS
    );

    public static final TextAttributesKey RHTML_SCRIPTING_BACKGROUND = TextAttributesKey.createTextAttributesKey(
            RHTML_SCRIPTING_BACKGROUND_ID,
            RHTML_SCRIPTING_BACKGROUND_DEFAULT_ATTRS
    );

    public static final TextAttributesKey FLEX_ERROR = TextAttributesKey.createTextAttributesKey(
            FLEX_ERROR_ID,
            FLEX_ERROR_DEFAULT_ATTRS
    );
}
