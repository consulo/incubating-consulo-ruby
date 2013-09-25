package org.consulo.yaml.lang.psi;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 17, 2008
 */
public interface YAMLTokenTypes {
  YAMLElementType ALIAS_TYPE = new YAMLElementType("alias");
  YAMLElementType ANCHOR_TYPE = new YAMLElementType("anchor");
  YAMLElementType COMMENT_TYPE = new YAMLElementType("comment");
  YAMLElementType SCALAR_KEY_TYPE = new YAMLElementType("scalar key");
  YAMLElementType VALUE_TYPE = new YAMLElementType("value");
  YAMLElementType UNKNOWN_TYPE = new YAMLElementType("unknown");

  YAMLElementType SCALAR_TEXT_TYPE = new YAMLElementType("scalar value");
  YAMLElementType SCALAR_STRING_TYPE = new YAMLElementType("scalar value \\");
  YAMLElementType SCALAR_DSTRING_TYPE = new YAMLElementType("scalar value \"");
  YAMLElementType SCALAR_LIST_TYPE = new YAMLElementType("scalar value |");
  YAMLElementType SCALAR_VALUE_TYPE = new YAMLElementType("scalar value >");
}
