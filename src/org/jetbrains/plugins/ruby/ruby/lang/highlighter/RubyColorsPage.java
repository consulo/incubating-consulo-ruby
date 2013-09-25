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

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 01.08.2006
 */
public class RubyColorsPage implements ColorSettingsPage {

    private static final String DEMO_TEXT =
        "<gm>gem</gm> \"my_gem\"\n" +
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
        "       puts %W(sanple words), CONSTANT, <sym>:fooo</sym>;\n"+
        "       do_something <sym>:action</sym> => \"action\"\n"+
        "   end\n"+
        "   1.upto(@@n) do |<lvar>index</lvar>| printIndex \"Hello\" + <lvar>index</lvar> end\n" +
        "   \\\\\\\\\\\\\\\\\\\\\n" +
        "end";

    private static final AttributesDescriptor[] ATTRS = new AttributesDescriptor[]{
            new AttributesDescriptor(RBundle.message("color.settings.ruby.keyword"), RubyHighlighter.KEYWORD),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.comment"), RubyHighlighter.COMMENT),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.number"), RubyHighlighter.NUMBER),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.string"), RubyHighlighter.STRING),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.escape_sequence"), RubyHighlighter.ESCAPE_SEQUENCE),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.invalid_escape_sequence"), RubyHighlighter.INVALID_ESCAPE_SEQUENCE),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.operation"), RubyHighlighter.OPERATION_SIGN),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.brackets"), RubyHighlighter.BRACKETS),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.expression_subtitution_marks"), RubyHighlighter.EXPR_SUBST_MARKS),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.bad_character"), RubyHighlighter.BAD_CHARACTER),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.comma"), RubyHighlighter.COMMA),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.semicolon"), RubyHighlighter.SEMICOLON),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.symbol"), RubyHighlighter.SYMBOL),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.hash_assoc"), RubyHighlighter.HASH_ASSOC),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.line_continuation"), RubyHighlighter.LINE_CONTINUATION),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.regexp"), RubyHighlighter.REGEXPS),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.words"), RubyHighlighter.WORDS),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.heredoc_id"), RubyHighlighter.HEREDOC_ID),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.heredoc_content"), RubyHighlighter.HEREDOC_CONTENT),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.gvar"), RubyHighlighter.GVAR),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.cvar"), RubyHighlighter.CVAR),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.ivar"), RubyHighlighter.IVAR),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.identifier"), RubyHighlighter.IDENTIFIER),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.local.variable"), RubyHighlighter.LOCAL_VARIABLE),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.constant"), RubyHighlighter.CONSTANT),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.constant.in.definition"), RubyHighlighter.CONSTANT_DEF),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.nth_ref"), RubyHighlighter.NTH_REF),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.back_ref"), RubyHighlighter.BACK_REF),

            new AttributesDescriptor(RBundle.message("color.settings.ruby.attr_reader"), RubyHighlighter.ATTR_READER),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.attr_writer"), RubyHighlighter.ATTR_WRITER),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.attr_accessor"), RubyHighlighter.ATTR_ACCESSOR),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.rails_attr"), RubyHighlighter.RAILS_ATTR),

            new AttributesDescriptor(RBundle.message("color.settings.ruby.private"), RubyHighlighter.PRIVATE_CALL),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.protected"), RubyHighlighter.PROTECTED_CALL),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.public"), RubyHighlighter.PUBLIC_CALL),

            new AttributesDescriptor(RBundle.message("color.settings.ruby.require.gem"), RubyHighlighter.REQUIRE_GEM_CALL),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.require.or.load"), RubyHighlighter.INCLUDE_JAVA_CALL),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.require.or.load.arg"), RubyHighlighter.REQUIRE_OR_LOAD_CALL_ARG),
            new AttributesDescriptor(RBundle.message("color.settings.ruby.include.or.extend"), RubyHighlighter.INCLUDE_OR_EXTEND_CALL),

            new AttributesDescriptor(RBundle.message("color.settings.ruby.jruby.include"), RubyHighlighter.INCLUDE_JAVA_CALL),

            new AttributesDescriptor(RBundle.message("color.settings.ruby.inspection.inspection", RBundle.message("annotation.warning.implicit.multivariant.required.item")), RubyHighlighter.INSPECTION_MULTIPLE_RESOLVE_WARNING),
    };
    
    private static Map<String, TextAttributesKey> ADDITIONAL_HIGHLIGHT_DESCRIPTORS = new HashMap<String, TextAttributesKey>();
    static{
        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("sym", RubyHighlighter.SYMBOL);
        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("cdef", RubyHighlighter.CONSTANT_DEF);
        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("lvar", RubyHighlighter.LOCAL_VARIABLE);

        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("aa", RubyHighlighter.ATTR_ACCESSOR);
        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("aw", RubyHighlighter.ATTR_WRITER);
        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("ar", RubyHighlighter.ATTR_READER);
        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("ra", RubyHighlighter.RAILS_ATTR);

        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("pri", RubyHighlighter.PRIVATE_CALL);
        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("pub", RubyHighlighter.PUBLIC_CALL);
        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("pro", RubyHighlighter.PROTECTED_CALL);

        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("req", RubyHighlighter.REQUIRE_OR_LOAD_CALL);
        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("reqa", RubyHighlighter.REQUIRE_OR_LOAD_CALL_ARG);
        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("inc", RubyHighlighter.INCLUDE_OR_EXTEND_CALL);

        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("jr", RubyHighlighter.INCLUDE_JAVA_CALL);
        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("gm", RubyHighlighter.REQUIRE_GEM_CALL);

        ADDITIONAL_HIGHLIGHT_DESCRIPTORS.put("reqmr", RubyHighlighter.INSPECTION_MULTIPLE_RESOLVE_WARNING);
    }

    private static final ColorDescriptor[] COLORS = new ColorDescriptor[0];

    @Override
	@Nullable
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return ADDITIONAL_HIGHLIGHT_DESCRIPTORS;
    }

    @Override
	@NotNull
    public String getDisplayName() {
        return RBundle.message("color.settings.ruby.name");
    }

    @Override
	@NotNull
    public Icon getIcon() {
        return RubyIcons.RUBY_COLOR_PAGE;
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
        return new RubySyntaxHighlighter();
    }

    @Override
	@NotNull
    public String getDemoText() {
        return DEMO_TEXT;
    }

}
