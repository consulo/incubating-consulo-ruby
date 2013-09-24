package org.jetbrains.plugins.ruby.rails.langs.yaml;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author: oleg
 * @date: Jun 17, 2008
 */
public class YAMLParserDefinition implements ParserDefinition, YAMLElementTypes {

  @NotNull
  public Lexer createLexer(final Project project) {
    return new YAMLLexer();
  }

  @Nullable
  public PsiParser createParser(final Project project) {
    return new YAMLMockParser();
  }

  public IFileElementType getFileNodeType() {
    return FILE;
  }

  @NotNull
  public TokenSet getWhitespaceTokens() {
    return TokenSet.EMPTY;
  }

  @NotNull
  public TokenSet getCommentTokens() {
    return TokenSet.create(YAMLTokenTypes.COMMENT_TYPE);
  }

  @NotNull
  public TokenSet getStringLiteralElements() {
    return TokenSet.EMPTY;
  }

  @NotNull
  public PsiElement createElement(final ASTNode node) {
    return PsiUtil.NULL_PSI_ELEMENT;
  }

  public PsiFile createFile(final FileViewProvider viewProvider) {
    return new YAMLFileImpl(viewProvider);
  }

  public SpaceRequirements spaceExistanceTypeBetweenTokens(final ASTNode left, final ASTNode right) {
    return SpaceRequirements.MAY;
  }
}