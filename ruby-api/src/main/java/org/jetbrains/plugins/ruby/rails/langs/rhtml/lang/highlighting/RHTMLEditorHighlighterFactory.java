package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.highlighting;

import consulo.codeEditor.EditorHighlighter;
import consulo.colorScheme.EditorColorsScheme;
import consulo.language.editor.highlight.EditorHighlighterProvider;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileType;

/**
 * @author VISTALL
 * @since 01.10.13.
 */
public class RHTMLEditorHighlighterFactory implements EditorHighlighterProvider
{
	@Nonnull
	@Override
	public FileType getFileType()
	{
		return RHTMLFileType.INSTANCE;
	}

	@Override
	public EditorHighlighter getEditorHighlighter(@Nullable Project project, @Nonnull FileType fileType, @Nullable VirtualFile virtualFile, @Nonnull EditorColorsScheme editorColorsScheme)
	{
		return new RHTMLEditorHighlighter(editorColorsScheme, project, virtualFile);
	}
}
