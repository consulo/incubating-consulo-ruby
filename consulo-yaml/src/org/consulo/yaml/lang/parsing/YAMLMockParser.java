package org.consulo.yaml.lang.parsing;

import org.consulo.yaml.lang.psi.YAMLElementTypes;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 17, 2008
 */
public class YAMLMockParser implements PsiParser
{
	@Override
	@NotNull
	public ASTNode parse(final IElementType root, final PsiBuilder builder, LanguageVersion languageVersion)
	{
		final PsiBuilder.Marker marker = builder.mark();
		while(builder.getTokenType() != null)
		{
			builder.advanceLexer();
		}
		marker.done(YAMLElementTypes.FILE);
		return builder.getTreeBuilt();
	}
}