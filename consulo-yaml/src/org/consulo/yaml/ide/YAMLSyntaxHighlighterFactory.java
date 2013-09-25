package org.consulo.yaml.ide;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class YAMLSyntaxHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory
{
	@NotNull
	@Override
	protected SyntaxHighlighter createHighlighter()
	{
		return new YAMLSyntaxHighlighter();
	}
}
