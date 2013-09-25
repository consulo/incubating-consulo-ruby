package org.consulo.yaml.lang.psi;

import org.consulo.yaml.lang.psi.YAMLTokenTypes;
import org.jetbrains.jvaymlb.tokens.AliasToken;
import org.jetbrains.jvaymlb.tokens.AnchorToken;
import org.jetbrains.jvaymlb.tokens.CommentToken;
import org.jetbrains.jvaymlb.tokens.KeyToken;
import org.jetbrains.jvaymlb.tokens.ScalarToken;
import org.jetbrains.jvaymlb.tokens.Token;
import org.jetbrains.jvaymlb.tokens.ValueToken;
import com.intellij.psi.tree.IElementType;

/**
 * @author oleg
 */
public class YAMLTokenTypeFactory implements YAMLTokenTypes
{

  public static IElementType getTypeForToken(final Token previousToken, final Token token) {
    if (token instanceof AliasToken) {
      return ALIAS_TYPE;
    }
    if (token instanceof AnchorToken) {
      return ANCHOR_TYPE;
    }
    if (token instanceof CommentToken) {
      return COMMENT_TYPE;
    }
    if (token instanceof ScalarToken) {
      if (previousToken instanceof KeyToken) {
        return SCALAR_KEY_TYPE;
      }
      final char style = ((ScalarToken)token).getStyle();
      switch (style) {
        case '\'':
          return SCALAR_STRING_TYPE;
        case '"':
          return SCALAR_DSTRING_TYPE;
        case '|':
          return SCALAR_LIST_TYPE;
        case '>':
          return SCALAR_VALUE_TYPE;
        default:
          return SCALAR_TEXT_TYPE;
      }
    }
    if (token instanceof ValueToken) {
      return VALUE_TYPE;
    }
    return UNKNOWN_TYPE;
  }
}