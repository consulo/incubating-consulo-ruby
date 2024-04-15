package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.highlighting;

import jakarta.annotation.Nullable;

import consulo.codeEditor.EditorHighlighter;
import consulo.project.Project;
import consulo.colorScheme.EditorColorsScheme;
import consulo.language.editor.highlight.EditorHighlighterProvider;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 01.10.13.
 */
public class RHTMLEditorHighlighterFactory implements EditorHighlighterProvider
{
	@Override
	public EditorHighlighter getEditorHighlighter(@Nullable Project project, @Nonnull FileType fileType, @Nullable VirtualFile virtualFile, @Nonnull EditorColorsScheme editorColorsScheme)
	{
		return new RHTMLEditorHighlighter(editorColorsScheme, project, virtualFile);
	}
}
