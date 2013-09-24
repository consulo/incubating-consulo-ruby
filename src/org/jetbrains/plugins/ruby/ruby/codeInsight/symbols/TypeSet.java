package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols;

/**
 * @author yole
 */
public class TypeSet {
  public static final TypeSet EMPTY = new TypeSet();

  private long myMask;

  public TypeSet(final Type... types) {
    for (Type type: types) {
      myMask |= (1L << type.getId());
    }
  }

  public boolean contains(final Type type) {
    return (myMask & (1L << type.getId())) != 0;
  }

  public TypeSet union(final Type... types) {
    TypeSet result = new TypeSet();
    result.myMask = myMask;
    for (Type type : types) {
      result.myMask |= (1L << type.getId());
    }
    return result;
  }

  public TypeSet union(final TypeSet typeSet) {
    TypeSet result = new TypeSet();
    result.myMask = myMask | typeSet.myMask;
    return result;
  }
}
