/*
 * Copyright 2000-2008 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.ruby.rails.langs.rhtml;

import com.intellij.ide.highlighter.XmlLikeFileType;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.localize.LocalizeValue;
import consulo.ruby.api.localize.RubyApiLocalize;
import consulo.ui.image.Image;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.eRubyLanguage;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.highlighting.RHTMLEditorHighlighter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Roman Chernyatchik
 * @date 17.10.2006
 */
public class RHTMLFileType extends XmlLikeFileType
{
	public static final String VALID_EXTENTIONS = "rhtml;erb;";
	public static final RHTMLFileType INSTANCE = new RHTMLFileType();

	private RHTMLFileType()
	{
		super(eRubyLanguage.INSTANCE);
	}

	@Override
	@Nonnull
	public String getId()
	{
		return "RHTML";
	}

	@Override
	@Nonnull
	public LocalizeValue getDescription()
	{
		return RubyApiLocalize.filetypeDescriptionRhtml();
	}

	@Override
	@Nonnull
	public String getDefaultExtension()
	{
		return "rhtml";
	}

	@Nonnull
	public String getERBExtension()
	{
		return "erb";
	}

	@Override
	@Nullable
	public Image getIcon()
	{
		return RailsIcons.RHTML_ICON;
	}

	public EditorHighlighter getEditorHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile, @Nonnull EditorColorsScheme colors)
	{
		return new RHTMLEditorHighlighter(colors, project, virtualFile);
	}
}