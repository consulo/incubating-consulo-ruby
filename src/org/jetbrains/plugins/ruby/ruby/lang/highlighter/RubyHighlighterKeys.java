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

package org.jetbrains.plugins.ruby.ruby.lang.highlighter;

import org.jetbrains.annotations.NonNls;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.XmlHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

@SuppressWarnings("UseJBColor")
public class RubyHighlighterKeys
{
	@NonNls
	static final String KEYWORD_ID = "RUBY_KEYWORD";
	@NonNls
	static final String COMMENT_ID = "RUBY_COMMENT";
	@NonNls
	static final String HEREDOC_ID_ID = "RUBY_HEREDOC_ID";
	@NonNls
	static final String HEREDOC_CONTENT_ID = "RUBY_HEREDOC_CONTENT";
	@NonNls
	static final String NUMBER_ID = "RUBY_NUMBER";
	@NonNls
	static final String STRING_ID = "RUBY_STRING";
	@NonNls
	static final String ESCAPE_SEQUENCE_ID = "RUBY_ESCAPE_SEQUENCE";
	@NonNls
	static final String INVALID_ESCAPE_SEQUENCE_ID = "RUBY_INVALID_ESCAPE_SEQUENCE";
	@NonNls
	static final String OPERATION_SIGN_ID = "RUBY_OPERATION_SIGN";
	@NonNls
	static final String BRACKETS_ID = "RUBY_BRACKETS";
	@NonNls
	static final String EXPR_IN_STRING_ID = "RUBY_EXPR_IN_STRING";
	@NonNls
	static final String BAD_CHARACTER_ID = "RUBY_BAD_CHARACTER";
	@NonNls
	static final String REGEXP_ID = "RUBY_REGEXP";
	@NonNls
	static final String WORDS_ID = "RUBY_WORDS";
	@NonNls
	static final String IDENTIFIER_ID = "RUBY_IDENTIFIER";
	@NonNls
	static final String CONSTANT_ID = "RUBY_CONSTANT";
	@NonNls
	static final String GVAR_ID = "RUBY_GVAR";
	@NonNls
	static final String CVAR_ID = "RUBY_CVAR";
	@NonNls
	static final String IVAR_ID = "RUBY_IVAR";
	@NonNls
	static final String NTH_REF_ID = "RUBY_NTH_REF";
	@NonNls
	static final String BACK_REF_ID = "RUBY_BACK_REF";
	@NonNls
	static final String COMMA_ID = "RUBY_COMMA";
	@NonNls
	static final String SEMICOLON_ID = "RUBY_SEMICOLON";
	@NonNls
	static final String HASH_ASSOC_ID = "RUBY_HASH_ASSOC";
	@NonNls
	static final String LINE_CONTINUATION_ID = "RUBY_LINE_CONTINUATION";

	// Additional syntax
	@NonNls
	public static final String CONSTANT_DEF_ID = "RUBY_CONSTANT_DEF_ID";
	@NonNls
	public static final String LOCAL_VARIABLE_ID = "RUBY_LOCAL_VAR_ID";
	@NonNls
	public static final String SYMBOL_ID = "RUBY_SYMBOL";
	@NonNls
	public static final String REQUIRE_OR_LOAD_CALL_ID = "RUBY_REQUIRE_CALL";
	@NonNls
	public static final String REQUIRE_OR_LOAD_CALL_ARG_ID = "RUBY_REQUIRE_ARG_CALL";
	@NonNls
	public static final String INCLUDE_OR_EXTEND_CALL_ID = "RUBY_IMPORT_CALL";
	@NonNls
	public static final String PUBLIC_CALL_ID = "RUBY_PUBLIC_CALL";
	@NonNls
	public static final String PRIVATE_CALL_ID = "RUBY_PRIVATE_CALL";
	@NonNls
	public static final String PROTECTED_CALL_ID = "RUBY_PROTECTED_CALL";
	@NonNls
	public static final String ATTR_READER_CALL_ID = "RUBY_ATTR_READER_CALL";
	@NonNls
	public static final String ATTR_WRITER_CALL_ID = "RUBY_ATTR_WRITER_CALL";
	@NonNls
	public static final String ATTR_ACCESSOR_CALL_ID = "RUBY_ATTR_ACCESSOR_CALL";
	@NonNls
	public static final String RAILS_ATTR_CALL_ID = "ATTR_INTERNAL_CALL_ID";

	@NonNls
	public static final String INCLUDE_JAVA_CALL_ID = "INCLUDE_JAVA_CALL";
	@NonNls
	public static final String REQUIRE_GEM_CALL_ID = "REQUIRE_GEM_CALL";

	@NonNls
	public static final String INSPECTION_MULTIPLE_RESOLVE_WARNING_ID = "INSPECTION_MULTIPLE_RESOLVE_WARNING_ID";

	/*public static final TextAttributes EXPR_SUBST_MARKS_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP.getDefaultAttributes().clone();
	public static final TextAttributes HASH_ASSOC_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.KEYWORD.getDefaultAttributes().clone();
	public static final TextAttributes LINE_CONTINUATION_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.SEMICOLON.getDefaultAttributes().clone();
	public static final TextAttributes REGEXPS_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.STRING.getDefaultAttributes().clone();
	public static final TextAttributes WORDS_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.STRING.getDefaultAttributes().clone();
	public static final TextAttributes HEREDOC_ID_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.DOC_COMMENT_TAG.getDefaultAttributes().clone();
	public static final TextAttributes HEREDOC_CONTENT_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE.getDefaultAttributes().clone();
	public static final TextAttributes GVAR_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.KEYWORD.getDefaultAttributes().clone();
	public static final TextAttributes NTH_REF_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.KEYWORD.getDefaultAttributes().clone();
	public static final TextAttributes BACK_REF_DEFAULT_ATTRS = DefaultLanguageHighlighterColors.KEYWORD.getDefaultAttributes().clone();
	public static final TextAttributes SYMBOL_DEFAULT_ATTRS = XmlHighlighterColors.HTML_COMMENT.getDefaultAttributes().clone();
	public static final TextAttributes ATTR_ACCESSOR_DEFAULT_ATTRS = XmlHighlighterColors.XML_TAG_DATA.getDefaultAttributes().clone();
	public static final TextAttributes ATTR_WRITER_DEFAULT_ATTRS = XmlHighlighterColors.XML_TAG_DATA.getDefaultAttributes().clone();
	public static final TextAttributes ATTR_READER_DEFAULT_ATTRS = XmlHighlighterColors.XML_TAG_DATA.getDefaultAttributes().clone();
	public static final TextAttributes RAILS_ATTR_DEFAULT_ATTRS = XmlHighlighterColors.XML_TAG_DATA.getDefaultAttributes().clone();
	public static final TextAttributes REQUIRE_OR_LOAD_CALL_ARG_DEFAULT_ATTRS = HighlighterColors.TEXT.getDefaultAttributes().clone();
	public static final TextAttributes INCLUDE_JAVA_CALL_DEFAULT_ATTRS = XmlHighlighterColors.XML_TAG_DATA.getDefaultAttributes().clone();

	public static final TextAttributes INSPECTION_MULTIPLE_RESOLVE_WARNING_DEFAULT_ATTRS = new TextAttributes();

	// fixes for default attributes
	static
	{
		EXPR_SUBST_MARKS_DEFAULT_ATTRS.setFontType(Font.PLAIN);
		EXPR_SUBST_MARKS_DEFAULT_ATTRS.setBackgroundColor(new Color(235, 235, 235));

		HASH_ASSOC_DEFAULT_ATTRS.setFontType(Font.BOLD);
		HASH_ASSOC_DEFAULT_ATTRS.setForegroundColor(Color.BLUE);

		LINE_CONTINUATION_DEFAULT_ATTRS.setFontType(Font.PLAIN);
		LINE_CONTINUATION_DEFAULT_ATTRS.setBackgroundColor(new Color(245, 245, 245));

		REGEXPS_DEFAULT_ATTRS.setFontType(Font.BOLD);
		REGEXPS_DEFAULT_ATTRS.setForegroundColor(new Color(128, 0, 0));

		WORDS_DEFAULT_ATTRS.setFontType(Font.BOLD);
		WORDS_DEFAULT_ATTRS.setForegroundColor(new Color(0, 128, 128));

		HEREDOC_ID_DEFAULT_ATTRS.setFontType(Font.BOLD);
		HEREDOC_ID_DEFAULT_ATTRS.setForegroundColor(Color.BLACK);
		HEREDOC_ID_DEFAULT_ATTRS.setEffectColor(Color.BLACK);
		HEREDOC_ID_DEFAULT_ATTRS.setEffectType(EffectType.LINE_UNDERSCORE);

		HEREDOC_CONTENT_DEFAULT_ATTRS.setFontType(Font.BOLD);
		HEREDOC_CONTENT_DEFAULT_ATTRS.setForegroundColor(new Color(41, 123, 222));

		GVAR_DEFAULT_ATTRS.setFontType(Font.BOLD);
		GVAR_DEFAULT_ATTRS.setForegroundColor(Color.BLACK);

		NTH_REF_DEFAULT_ATTRS.setFontType(Font.BOLD);
		NTH_REF_DEFAULT_ATTRS.setForegroundColor(Color.BLACK);

		BACK_REF_DEFAULT_ATTRS.setFontType(Font.BOLD);
		BACK_REF_DEFAULT_ATTRS.setForegroundColor(Color.BLACK);


		SYMBOL_DEFAULT_ATTRS.setBackgroundColor(new Color(208, 237, 237));
		SYMBOL_DEFAULT_ATTRS.setForegroundColor(null);

		ATTR_ACCESSOR_DEFAULT_ATTRS.setBackgroundColor(new Color(232, 211, 211));

		ATTR_WRITER_DEFAULT_ATTRS.setBackgroundColor(new Color(210, 247, 210));

		ATTR_READER_DEFAULT_ATTRS.setBackgroundColor(new Color(246, 246, 208));

		RAILS_ATTR_DEFAULT_ATTRS.setBackgroundColor(new Color(162, 207, 245));

		REQUIRE_OR_LOAD_CALL_ARG_DEFAULT_ATTRS.setBackgroundColor(new Color(235, 235, 245));
		REQUIRE_OR_LOAD_CALL_ARG_DEFAULT_ATTRS.setForegroundColor(null);
		REQUIRE_OR_LOAD_CALL_ARG_DEFAULT_ATTRS.setFontType(Font.PLAIN);

		INCLUDE_JAVA_CALL_DEFAULT_ATTRS.setBackgroundColor(new Color(247, 184, 145));

		//Intension
		INSPECTION_MULTIPLE_RESOLVE_WARNING_DEFAULT_ATTRS.setBackgroundColor(new Color(255, 220, 212));
		INSPECTION_MULTIPLE_RESOLVE_WARNING_DEFAULT_ATTRS.setErrorStripeColor(new Color(249, 178, 178));
	}*/

	// text attributes keys
	public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(KEYWORD_ID, DefaultLanguageHighlighterColors.KEYWORD);
	public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(COMMENT_ID, DefaultLanguageHighlighterColors.BLOCK_COMMENT);
	public static final TextAttributesKey HEREDOC_ID = TextAttributesKey.createTextAttributesKey(HEREDOC_ID_ID);
	public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey(NUMBER_ID, DefaultLanguageHighlighterColors.NUMBER);
	public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(STRING_ID, DefaultLanguageHighlighterColors.STRING);
	public static final TextAttributesKey ESCAPE_SEQUENCE = TextAttributesKey.createTextAttributesKey(ESCAPE_SEQUENCE_ID, DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);
	public static final TextAttributesKey INVALID_ESCAPE_SEQUENCE = TextAttributesKey.createTextAttributesKey(INVALID_ESCAPE_SEQUENCE_ID, DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
	public static final TextAttributesKey OPERATION_SIGN = TextAttributesKey.createTextAttributesKey(OPERATION_SIGN_ID, DefaultLanguageHighlighterColors.OPERATION_SIGN);
	public static final TextAttributesKey BRACKETS = TextAttributesKey.createTextAttributesKey(BRACKETS_ID, DefaultLanguageHighlighterColors.BRACKETS);
	public static final TextAttributesKey EXPR_SUBST_MARKS = TextAttributesKey.createTextAttributesKey(EXPR_IN_STRING_ID);
	public static final TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(BAD_CHARACTER_ID, HighlighterColors.BAD_CHARACTER);
	public static final TextAttributesKey REGEXPS = TextAttributesKey.createTextAttributesKey(REGEXP_ID, DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);
	public static final TextAttributesKey WORDS = TextAttributesKey.createTextAttributesKey(WORDS_ID);
	public static final TextAttributesKey HEREDOC_CONTENT = TextAttributesKey.createTextAttributesKey(HEREDOC_CONTENT_ID);


	public static final TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey(IDENTIFIER_ID, DefaultLanguageHighlighterColors.IDENTIFIER);
	public static final TextAttributesKey CONSTANT = TextAttributesKey.createTextAttributesKey(CONSTANT_ID, DefaultLanguageHighlighterColors.CONSTANT);
	public static final TextAttributesKey GVAR = TextAttributesKey.createTextAttributesKey(GVAR_ID);
	public static final TextAttributesKey CVAR = TextAttributesKey.createTextAttributesKey(CVAR_ID, DefaultLanguageHighlighterColors.STATIC_FIELD);
	public static final TextAttributesKey IVAR = TextAttributesKey.createTextAttributesKey(IVAR_ID, DefaultLanguageHighlighterColors.INSTANCE_FIELD);
	public static final TextAttributesKey NTH_REF = TextAttributesKey.createTextAttributesKey(NTH_REF_ID);
	public static final TextAttributesKey BACK_REF = TextAttributesKey.createTextAttributesKey(BACK_REF_ID);


	public static final TextAttributesKey COMMA = TextAttributesKey.createTextAttributesKey(COMMA_ID, DefaultLanguageHighlighterColors.COMMA);
	public static final TextAttributesKey SEMICOLON = TextAttributesKey.createTextAttributesKey(SEMICOLON_ID, DefaultLanguageHighlighterColors.SEMICOLON);

	public static final TextAttributesKey HASH_ASSOC = TextAttributesKey.createTextAttributesKey(HASH_ASSOC_ID);
	public static final TextAttributesKey LINE_CONTINUATION = TextAttributesKey.createTextAttributesKey(LINE_CONTINUATION_ID);


	// Additional syntax
	public static final TextAttributesKey CONSTANT_DEF = TextAttributesKey.createTextAttributesKey(CONSTANT_DEF_ID, DefaultLanguageHighlighterColors.CONSTANT);
	public static final TextAttributesKey LOCAL_VARIABLE = TextAttributesKey.createTextAttributesKey(LOCAL_VARIABLE_ID, DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
	public static final TextAttributesKey SYMBOL = TextAttributesKey.createTextAttributesKey(SYMBOL_ID);

	public static final TextAttributesKey REQUIRE_OR_LOAD_CALL = TextAttributesKey.createTextAttributesKey(REQUIRE_OR_LOAD_CALL_ID, DefaultLanguageHighlighterColors.MARKUP_TAG);
	public static final TextAttributesKey REQUIRE_OR_LOAD_CALL_ARG = TextAttributesKey.createTextAttributesKey(REQUIRE_OR_LOAD_CALL_ARG_ID);

	public static final TextAttributesKey INCLUDE_OR_EXTEND_CALL = TextAttributesKey.createTextAttributesKey(INCLUDE_OR_EXTEND_CALL_ID, DefaultLanguageHighlighterColors.MARKUP_TAG);
	public static final TextAttributesKey PRIVATE_CALL = TextAttributesKey.createTextAttributesKey(PRIVATE_CALL_ID, XmlHighlighterColors.XML_TAG_DATA);
	public static final TextAttributesKey PROTECTED_CALL = TextAttributesKey.createTextAttributesKey(PROTECTED_CALL_ID, XmlHighlighterColors.XML_TAG_DATA);
	public static final TextAttributesKey PUBLIC_CALL = TextAttributesKey.createTextAttributesKey(PUBLIC_CALL_ID, XmlHighlighterColors.XML_TAG_DATA);
	public static final TextAttributesKey INCLUDE_JAVA_CALL = TextAttributesKey.createTextAttributesKey(INCLUDE_JAVA_CALL_ID);
	public static final TextAttributesKey ATTR_READER = TextAttributesKey.createTextAttributesKey(ATTR_READER_CALL_ID);
	public static final TextAttributesKey ATTR_WRITER = TextAttributesKey.createTextAttributesKey(ATTR_WRITER_CALL_ID);
	public static final TextAttributesKey ATTR_ACCESSOR = TextAttributesKey.createTextAttributesKey(ATTR_ACCESSOR_CALL_ID);
	public static final TextAttributesKey RAILS_ATTR = TextAttributesKey.createTextAttributesKey(RAILS_ATTR_CALL_ID);
	public static final TextAttributesKey REQUIRE_GEM_CALL = TextAttributesKey.createTextAttributesKey(REQUIRE_GEM_CALL_ID, XmlHighlighterColors.XML_TAG_DATA);
	//Intentions
	public static final TextAttributesKey INSPECTION_MULTIPLE_RESOLVE_WARNING = TextAttributesKey.createTextAttributesKey(INSPECTION_MULTIPLE_RESOLVE_WARNING_ID);
}
