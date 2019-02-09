package org.jetbrains.plugins.ruby.rails.langs.rhtml;

import javax.annotation.Nonnull;

import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.FileViewProviderFactory;
import com.intellij.psi.PsiManager;

/**
 * @author VISTALL
 * @since 24.09.13.
 */
public class eRubyFileViewProviderFactory implements FileViewProviderFactory
{
	@Override
	public FileViewProvider createFileViewProvider(@Nonnull VirtualFile virtualFile, Language language, @Nonnull PsiManager psiManager, boolean b)
	{
		return new RHTMLFileViewProvider(psiManager, virtualFile, b);
	}
}
