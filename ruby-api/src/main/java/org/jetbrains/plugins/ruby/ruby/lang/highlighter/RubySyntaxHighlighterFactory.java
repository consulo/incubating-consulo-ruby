package org.jetbrains.plugins.ruby.ruby.lang.highlighter;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;

/**
 * @author VISTALL
 * @since 24.09.13.
 */
public class RubySyntaxHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory
{
	@NotNull
	@Override
	protected SyntaxHighlighter createHighlighter()
	{
		return new RubySyntaxHighlighter();
	}
}
