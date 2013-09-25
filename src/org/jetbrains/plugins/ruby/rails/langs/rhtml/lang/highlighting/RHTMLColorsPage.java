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

import com.intellij.ide.highlighter.HtmlFileHighlighter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.highlighting.impl.RHTMLColorsPageHighlightingLexer;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.highlighting.impl.RHTMLFileHighlighterImpl;
import org.jetbrains.plugins.ruby.ruby.lang.highlighter.RubySyntaxHighlighter;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyElementType;

import javax.swing.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 15, 2007
 */
public class RHTMLColorsPage implements ColorSettingsPage {
    private static final String DEMO_TEXT =
            "<%# Sample comment %> \n" +
                    "<% 3.times do { -%> \n" +
                    "   <%= \"Hello\" + \" word!\" %> <br />\n" +
                    "<% } %> \n";

    private static final AttributesDescriptor[] ATTRS = new AttributesDescriptor[]{
            new AttributesDescriptor(RBundle.message("color.settings.rhtml.scriptlet.start"), RHTMLHighlighter.RHTML_SCRIPTLET_START),
            new AttributesDescriptor(RBundle.message("color.settings.rhtml.scriptlet.end"), RHTMLHighlighter.RHTML_SCRIPTLET_END),
            new AttributesDescriptor(RBundle.message("color.settings.rhtml.expr.start"), RHTMLHighlighter.RHTML_EXPRESSION_START),
            new AttributesDescriptor(RBundle.message("color.settings.rhtml.expr.end"), RHTMLHighlighter.RHTML_EXPRESSION_END),
            new AttributesDescriptor(RBundle.message("color.settings.rhtml.comment"), RHTMLHighlighter.COMMENT),
            new AttributesDescriptor(RBundle.message("color.settings.rhtml.omit.new.line"), RHTMLHighlighter.OMIT_NEW_LINE),
            new AttributesDescriptor(RBundle.message("color.settings.rhtml.scripting.bkg"), RHTMLHighlighter.RHTML_SCRIPTING_BACKGROUND)
    };

    private static final ColorDescriptor[] COLORS = new ColorDescriptor[0];

    @Override
	@Nullable
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
	@NotNull
    public String getDisplayName() {
        return RBundle.message("color.settings.rhtml.name");
    }

    @Override
	@NotNull
    public Icon getIcon() {
        return RailsIcons.RHTML_ICON;
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
        return new MyRHTMLFileHighlighterImpl();
    }

    @Override
	@NotNull
    public String getDemoText() {
        return DEMO_TEXT;
    }

    public class MyRHTMLFileHighlighterImpl extends RHTMLFileHighlighterImpl {
        private SyntaxHighlighter myRubyHighlighter;
        private SyntaxHighlighter myHTMLHighlighter;

        public MyRHTMLFileHighlighterImpl() {
            myRubyHighlighter = new RubySyntaxHighlighter();
            myHTMLHighlighter = new HtmlFileHighlighter();
        }

        @Override
		@NotNull
        public Lexer getHighlightingLexer() {
            return new RHTMLColorsPageHighlightingLexer();
        }

        @Override
		@NotNull
        public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
            final TextAttributesKey[] keys = super.getTokenHighlights(tokenType);
            if (keys.length != 0) {
                return keys;
            }

            if (tokenType instanceof RubyElementType) {
                return pack(RHTMLHighlighter.RHTML_SCRIPTING_BACKGROUND,
                            myRubyHighlighter.getTokenHighlights(tokenType));
            }

           return myHTMLHighlighter.getTokenHighlights(tokenType);
        }
    }
}