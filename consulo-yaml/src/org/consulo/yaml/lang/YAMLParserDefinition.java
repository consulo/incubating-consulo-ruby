package org.consulo.yaml.lang;

import org.consulo.yaml.lang.parsing.YAMLLexer;
import org.consulo.yaml.lang.psi.YAMLTokenTypes;
import org.consulo.yaml.lang.parsing.YAMLMockParser;
import org.consulo.yaml.lang.psi.YAMLElementTypes;
import org.consulo.yaml.lang.psi.impl.YAMLFileImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author: oleg
 * @date: Jun 17, 2008
 */
public class YAMLParserDefinition implements ParserDefinition, YAMLElementTypes
{

  @NotNull
  public Lexer createLexer(final Project project, LanguageVersion languageVersion) {
    return new YAMLLexer();
  }

  @Nullable
  public PsiParser createParser(final Project project, LanguageVersion languageVersion) {
    return new YAMLMockParser();
  }

  public IFileElementType getFileNodeType() {
    return FILE;
  }

  @NotNull
  public TokenSet getWhitespaceTokens(LanguageVersion languageVersion) {
    return TokenSet.EMPTY;
  }

  @NotNull
  public TokenSet getCommentTokens(LanguageVersion languageVersion) {
    return TokenSet.create(YAMLTokenTypes.COMMENT_TYPE);
  }

  @NotNull
  public TokenSet getStringLiteralElements(LanguageVersion languageVersion) {
    return TokenSet.EMPTY;
  }

  @NotNull
  public PsiElement createElement(final ASTNode node) {
    return new ASTWrapperPsiElement(node);
  }

  public PsiFile createFile(final FileViewProvider viewProvider) {
    return new YAMLFileImpl(viewProvider);
  }

  public SpaceRequirements spaceExistanceTypeBetweenTokens(final ASTNode left, final ASTNode right) {
    return SpaceRequirements.MAY;
  }
}