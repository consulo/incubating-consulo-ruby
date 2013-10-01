package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.highlighting;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.EditorHighlighterProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author VISTALL
 * @since 01.10.13.
 */
public class RHTMLEditorHighlighterFactory implements EditorHighlighterProvider
{
	@Override
	public EditorHighlighter getEditorHighlighter(@Nullable Project project, @NotNull FileType fileType, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme editorColorsScheme)
	{
		return new RHTMLEditorHighlighter(editorColorsScheme, project, virtualFile);
	}
}
