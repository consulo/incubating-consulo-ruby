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

import java.awt.Color;
import java.awt.Font;

import org.jetbrains.annotations.NonNls;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 21, 2008
 */
public class YAMLHighlighter
{
	@NonNls
	static final String SCALAR_KEY_ID = "YAML_SCALAR_KEY";
	@NonNls
	static final String SCALAR_TEXT_ID = "YAML_SCALAR_VALUE";
	@NonNls
	static final String SCALAR_STRING_ID = "YAML_SCALAR_STRING";
	@NonNls
	static final String SCALAR_DSTRING_ID = "YAML_SCALAR_DSTRING";
	@NonNls
	static final String SCALAR_LIST_ID = "YAML_SCALAR_LIST";
	@NonNls
	static final String SCALAR_VALUE_ID = "YAML_SCALAR_VALUE4";
	@NonNls
	static final String VALUE_ID = "YAML_VALUE";
	@NonNls
	static final String COMMENT_ID = "YAML_COMMENT";
	@NonNls


	public static final TextAttributes SCALAR_TEXT_DEFAULT_ATTRS = HighlighterColors.TEXT.getDefaultAttributes().clone();
	public static final TextAttributes SCALAR_STRING_DEFAULT_ATTRS = HighlighterColors.TEXT.getDefaultAttributes().clone();
	public static final TextAttributes SCALAR_DSTRING_DEFAULT_ATTRS = HighlighterColors.TEXT.getDefaultAttributes().clone();
	public static final TextAttributes SCALAR_LIST_DEFAULT_ATTRS = HighlighterColors.TEXT.getDefaultAttributes().clone();
	public static final TextAttributes SCALAR_VALUE_DEFAULT_ATTRS = HighlighterColors.TEXT.getDefaultAttributes().clone();

	static
	{
		SCALAR_STRING_DEFAULT_ATTRS.setForegroundColor(new Color(0, 128, 128));
		SCALAR_STRING_DEFAULT_ATTRS.setFontType(Font.BOLD);

		SCALAR_DSTRING_DEFAULT_ATTRS.setForegroundColor(new Color(0, 128, 0));
		SCALAR_DSTRING_DEFAULT_ATTRS.setFontType(Font.BOLD);

		SCALAR_LIST_DEFAULT_ATTRS.setBackgroundColor(new Color(218, 233, 246));
		SCALAR_VALUE_DEFAULT_ATTRS.setBackgroundColor(new Color(232, 211, 211));
	}

	// text attributes keys
	public static final TextAttributesKey SCALAR_KEY = TextAttributesKey.createTextAttributesKey(SCALAR_KEY_ID, DefaultLanguageHighlighterColors.KEYWORD);
	public static final TextAttributesKey SCALAR_TEXT = TextAttributesKey.createTextAttributesKey(SCALAR_TEXT_ID, SCALAR_TEXT_DEFAULT_ATTRS);
	public static final TextAttributesKey SCALAR_STRING = TextAttributesKey.createTextAttributesKey(SCALAR_STRING_ID, SCALAR_STRING_DEFAULT_ATTRS);
	public static final TextAttributesKey SCALAR_DSTRING = TextAttributesKey.createTextAttributesKey(SCALAR_DSTRING_ID, SCALAR_DSTRING_DEFAULT_ATTRS);
	public static final TextAttributesKey SCALAR_LIST = TextAttributesKey.createTextAttributesKey(SCALAR_LIST_ID, SCALAR_LIST_DEFAULT_ATTRS);
	public static final TextAttributesKey SCALAR_VALUE = TextAttributesKey.createTextAttributesKey(SCALAR_VALUE_ID, SCALAR_VALUE_DEFAULT_ATTRS);
	public static final TextAttributesKey VALUE = TextAttributesKey.createTextAttributesKey(VALUE_ID, DefaultLanguageHighlighterColors.KEYWORD);
	public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(COMMENT_ID, DefaultLanguageHighlighterColors.DOC_COMMENT);
}
