package org.jetbrains.plugins.ruby.ruby.lang.highlighter;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.highlight.SingleLazyInstanceSyntaxHighlighterFactory;
import consulo.language.editor.highlight.SyntaxHighlighter;
import jakarta.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;

/**
 * @author VISTALL
 * @since 24.09.13.
 */
@ExtensionImpl
public class RubySyntaxHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory
{
	@Nonnull
	@Override
	protected SyntaxHighlighter createHighlighter()
	{
		return new RubySyntaxHighlighter();
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return RubyLanguage.INSTANCE;
	}
}
