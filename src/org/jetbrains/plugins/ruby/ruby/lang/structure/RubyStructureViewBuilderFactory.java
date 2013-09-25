package org.jetbrains.plugins.ruby.ruby.lang.structure;

import org.jetbrains.annotations.Nullable;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.psi.PsiFile;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class RubyStructureViewBuilderFactory implements PsiStructureViewFactory
{
	@Nullable
	@Override
	public StructureViewBuilder getStructureViewBuilder(PsiFile psiFile)
	{
		return new RubyStructureViewBuilder(psiFile);
	}
}
