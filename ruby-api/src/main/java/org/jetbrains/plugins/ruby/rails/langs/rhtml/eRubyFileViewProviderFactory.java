package org.jetbrains.plugins.ruby.rails.langs.rhtml;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.file.FileViewProvider;
import consulo.language.file.LanguageFileViewProviderFactory;
import consulo.language.psi.PsiManager;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.eRubyLanguage;

/**
 * @author VISTALL
 * @since 24.09.13.
 */
@ExtensionImpl
public class eRubyFileViewProviderFactory implements LanguageFileViewProviderFactory
{
	@Override
	public FileViewProvider createFileViewProvider(@Nonnull VirtualFile virtualFile, Language language, @Nonnull PsiManager psiManager, boolean b)
	{
		return new RHTMLFileViewProvider(psiManager, virtualFile, b);
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return eRubyLanguage.INSTANCE;
	}
}
