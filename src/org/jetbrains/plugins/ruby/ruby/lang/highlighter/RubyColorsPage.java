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

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 01.08.2006
 */
public class RubyColorsPage implements ColorSettingsPage
{

	private static final String DEMO_TEXT = "<gm>gem</gm> \"my_gem\"\n" +
			"<req>require</req> <reqa>\"test\"</reqa>\n" +
			"<req>require</req> <reqmr>\"test2\"</reqmr>\n\n" +

			"<cdef>CONSTANT</cdef> = 777\n\n" +

			"# Sample comment\n\n" +
			"module SampleModule\n" +
			"   <inc>include</inc> Testcase\n" +
			"   <jr>include_class</jr> 'java.lang.Runnable'\n\n" +

			"   <ra>attr_internal</ra> <sym>:context</sym>\n" +
			"   <ar>attr_reader</ar> <sym>:foo</sym>\n" +
			"   <aw>attr_writer</aw> <sym>:@bar</sym>\n" +
			"   <aa>attr_accessor</aa> <sym>:\"baz\"</sym>\n\n" +

			"   <pri>private</pri>\n" +
			"   <pro>protected</pro>\n" +
			"   <pub>public</pub>\n\n" +

			"   def foo(<lvar>some_parameter</lvar>)\n" +
			"   end\n\n" +

			"   @greeting = eval <<-\"FOO\";\\\n" +
			"   printIndex \"Hello world!\"\n" +
			"   And now this is heredoc!\n" +
			"   printIndex \"Hello world again!\"\n" +
			"   FOO\n" +
			"   foo(\"#{$GLOBAL_TIME >> $`} is \\Z sample \\\"string\\\"\" * 777);\n" +
			"   if ($1 =~ /sample regular expression/ni) then\n" +
			"   begin\n" +
			"       puts %W(sanple words), CONSTANT, <sym>:fooo</sym>;\n" +
			"       do_something <sym>:action</sym> => \"action\"\n" +
			"   end\n" +
			"   1.upto(@@n) do |<lvar>index</lvar>| printIndex \"Hello\" + <lvar>index</lvar> end\n" +
			"   \\\\\\\\\\\\\\\\\\\\\n" +
			"end";

	private static final AttributesDescriptor[] ATTRS = new AttributesDescriptor[]{
			new AttributesDescriptor(RBundle.message("color.settings.ruby.keyword"), RubyHighlighterKeys.KEYWORD),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.comment"), RubyHighlighterKeys.COMMENT),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.number"), RubyHighlighterKeys.NUMBER),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.string"), RubyHighlighterKeys.STRING),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.escape_sequence"), RubyHighlighterKeys.ESCAPE_SEQUENCE),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.invalid_escape_sequence"), RubyHighlighterKeys.INVALID_ESCAPE_SEQUENCE),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.operation"), RubyHighlighterKeys.OPERATION_SIGN),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.brackets"), RubyHighlighterKeys.BRACKETS),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.expression_subtitution_marks"), RubyHighlighterKeys.EXPR_SUBST_MARKS),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.bad_character"), RubyHighlighterKeys.BAD_CHARACTER),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.comma"), RubyHighlighterKeys.COMMA),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.semicolon"), RubyHighlighterKeys.SEMICOLON),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.symbol"), RubyHighlighterKeys.SYMBOL),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.hash_assoc"), RubyHighlighterKeys.HASH_ASSOC),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.line_continuation"), RubyHighlighterKeys.LINE_CONTINUATION),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.regexp"), RubyHighlighterKeys.REGEXPS),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.words"), RubyHighlighterKeys.WORDS),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.heredoc_id"), RubyHighlighterKeys.HEREDOC_ID),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.heredoc_content"), RubyHighlighterKeys.HEREDOC_CONTENT),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.gvar"), RubyHighlighterKeys.GVAR),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.cvar"), RubyHighlighterKeys.CVAR),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.ivar"), RubyHighlighterKeys.IVAR),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.identifier"), RubyHighlighterKeys.IDENTIFIER),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.local.variable"), RubyHighlighterKeys.LOCAL_VARIABLE),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.constant"), RubyHighlighterKeys.CONSTANT),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.constant.in.definition"), RubyHighlighterKeys.CONSTANT_DEF),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.nth_ref"), RubyHighlighterKeys.NTH_REF),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.back_ref"), RubyHighlighterKeys.BACK_REF),

			new AttributesDescriptor(RBundle.message("color.settings.ruby.attr_reader"), RubyHighlighterKeys.ATTR_READER),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.attr_writer"), RubyHighlighterKeys.ATTR_WRITER),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.attr_accessor"), RubyHighlighterKeys.ATTR_ACCESSOR),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.rails_attr"), RubyHighlighterKeys.RAILS_ATTR),

			new AttributesDescriptor(RBundle.message("color.settings.ruby.private"), RubyHighlighterKeys.PRIVATE_CALL),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.protected"), RubyHighlighterKeys.PROTECTED_CALL),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.public"), RubyHighlighterKeys.PUBLIC_CALL),

			new AttributesDescriptor(RBundle.message("color.settings.ruby.require.gem"), RubyHighlighterKeys.REQUIRE_GEM_CALL),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.require.or.load"), RubyHighlighterKeys.INCLUDE_JAVA_CALL),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.require.or.load.arg"), RubyHighlighterKeys.REQUIRE_OR_LOAD_CALL_ARG),
			new AttributesDescriptor(RBundle.message("color.settings.ruby.include.or.extend"), RubyHighlighterKeys.INCLUDE_OR_EXTEND_CALL),

			new AttributesDescriptor(RBundle.message("color.settings.ruby.jruby.include"), RubyHighlighterKeys.INCLUDE_JAVA_CALL),

			new AttributesDescriptor(RBundle.message("color.settings.ruby.inspection.inspection", RBundle.message("annotation.warning.implicit.multivariant.required.item")), RubyHighlighterKeys.INSPECTION_MULTIPLE_RESOLVE_WARNING),
	};

	private static Map<String, TextAttributesKey> ADDITIONAL_HIGHLIGHT_DESCRIPTORS = new HashMap<String, TextAttributesKey>();

	static
	{
		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("sym", RubyHighlighterKeys.SYMBOL);
		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("cdef", RubyHighlighterKeys.CONSTANT_DEF);
		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("lvar", RubyHighlighterKeys.LOCAL_VARIABLE);

		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("aa", RubyHighlighterKeys.ATTR_ACCESSOR);
		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("aw", RubyHighlighterKeys.ATTR_WRITER);
		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("ar", RubyHighlighterKeys.ATTR_READER);
		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("ra", RubyHighlighterKeys.RAILS_ATTR);

		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("pri", RubyHighlighterKeys.PRIVATE_CALL);
		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("pub", RubyHighlighterKeys.PUBLIC_CALL);
		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("pro", RubyHighlighterKeys.PROTECTED_CALL);

		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("req", RubyHighlighterKeys.REQUIRE_OR_LOAD_CALL);
		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("reqa", RubyHighlighterKeys.REQUIRE_OR_LOAD_CALL_ARG);
		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("inc", RubyHighlighterKeys.INCLUDE_OR_EXTEND_CALL);

		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("jr", RubyHighlighterKeys.INCLUDE_JAVA_CALL);
		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("gm", RubyHighlighterKeys.REQUIRE_GEM_CALL);

		ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("reqmr", RubyHighlighterKeys.INSPECTION_MULTIPLE_RESOLVE_WARNING);
	}

	private static final ColorDescriptor[] COLORS = new ColorDescriptor[0];

	@Override
	@Nullable
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap()
	{
		return ADDITIONAL_HIGHLIGHT_DESCRIPTORS;
	}

	@Override
	@NotNull
	public String getDisplayName()
	{
		return RBundle.message("color.settings.ruby.name");
	}

	@Override
	@NotNull
	public Icon getIcon()
	{
		return RubyIcons.RUBY_COLOR_PAGE;
	}

	@Override
	@NotNull
	public AttributesDescriptor[] getAttributeDescriptors()
	{
		return ATTRS;
	}

	@Override
	@NotNull
	public ColorDescriptor[] getColorDescriptors()
	{
		return COLORS;
	}

	@Override
	@NotNull
	public SyntaxHighlighter getHighlighter()
	{
		return new RubySyntaxHighlighter();
	}

	@Override
	@NotNull
	public String getDemoText()
	{
		return DEMO_TEXT;
	}

}
