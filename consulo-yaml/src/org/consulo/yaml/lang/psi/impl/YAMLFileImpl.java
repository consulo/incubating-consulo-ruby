package org.consulo.yaml.lang.psi.impl;

import org.consulo.yaml.lang.fileType.YAMLFileType;
import org.consulo.yaml.lang.YAMLLanguage;
import org.jetbrains.annotations.NotNull;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 17, 2008
 */
public class YAMLFileImpl extends PsiFileBase {
	public YAMLFileImpl(FileViewProvider viewProvider) {
    super(viewProvider, YAMLLanguage.INSTANCE);
  }

  @NotNull
  public FileType getFileType() {
    return YAMLFileType.INSTANCE;
  }
}
