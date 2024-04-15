package org.jetbrains.plugins.ruby.ruby.lang.structure;

import consulo.annotation.component.ExtensionImpl;
import consulo.fileEditor.structureView.StructureViewBuilder;
import consulo.language.Language;
import consulo.language.editor.structureView.PsiStructureViewFactory;
import consulo.language.psi.PsiFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
@ExtensionImpl
public class RubyStructureViewBuilderFactory implements PsiStructureViewFactory
{
	@Nullable
	@Override
	public StructureViewBuilder getStructureViewBuilder(PsiFile psiFile)
	{
		return new RubyStructureViewBuilder(psiFile);
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return RubyLanguage.INSTANCE;
	}
}
