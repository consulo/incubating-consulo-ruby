package org.jetbrains.plugins.ruby.rails.langs.yaml;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.langs.YAMLFileType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 17, 2008
 */
public class YAMLFileImpl extends PsiFileBase {
  protected YAMLFileImpl(FileViewProvider viewProvider) {
    super(viewProvider, YAMLLanguage.INSTANCE);
  }

  @NotNull
  public FileType getFileType() {
    return YAMLFileType.YML;
  }
}
