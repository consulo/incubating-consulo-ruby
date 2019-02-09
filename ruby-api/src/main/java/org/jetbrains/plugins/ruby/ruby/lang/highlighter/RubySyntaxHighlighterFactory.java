package org.jetbrains.plugins.ruby.ruby.lang.highlighter;

import javax.annotation.Nonnull;

import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;

/**
 * @author VISTALL
 * @since 24.09.13.
 */
public class RubySyntaxHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory
{
	@Nonnull
	@Override
	protected SyntaxHighlighter createHighlighter()
	{
		return new RubySyntaxHighlighter();
	}
}
