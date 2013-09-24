package org.jetbrains.plugins.ruby.rails.langs.yaml;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 17, 2008
 */
public class YAMLMockParser implements PsiParser {
  @NotNull
  public ASTNode parse(final IElementType root, final PsiBuilder builder) {
    final PsiBuilder.Marker marker = builder.mark();
    while (builder.getTokenType()!=null){
      builder.advanceLexer();
    }
    marker.done(YAMLElementTypes.FILE);
    return builder.getTreeBuilt();
  }
}