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

package org.jetbrains.plugins.ruby.rails.highlighter.codeHighlighting;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.RailsComponents;
import org.jetbrains.plugins.ruby.rails.codeInsight.daemon.DaemonCodeAnalyzerUtil;
import org.jetbrains.plugins.ruby.rails.codeInsight.daemon.RailsLineMarkerInfo;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.nameConventions.ViewsConventions;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import com.intellij.codeHighlighting.Pass;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory;
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 02.02.2007
 */
public class RailsHighlightPassFactory implements TextEditorHighlightingPassFactory
{
	private final TextEditorHighlightingPassRegistrar myRegistrar;

	public RailsHighlightPassFactory(final TextEditorHighlightingPassRegistrar passRegistrar)
	{
		myRegistrar = passRegistrar;
	}

	@Override
	@Nullable
	public TextEditorHighlightingPass createHighlightingPass(final @Nullable PsiFile psiFile, @NotNull final Editor editor)
	{
		if(psiFile == null)
		{
			return null;
		}

		final Module module = ModuleUtil.findModuleForPsiElement(psiFile);
		if(module != null && RailsFacetUtil.hasRailsSupport(module))
		{
			final VirtualFile file = psiFile.getVirtualFile();
			if(file != null)
			{
				final String name = file.getName();
				if(ViewsConventions.isValidViewFileName(name))
				{
					if(ViewsConventions.isPartialViewName(name))
					{
						return new RailsViewToControllerMarkersPass(module, psiFile, editor);
					}
					return new RailsViewToActionMarkersPass(module, psiFile, editor);
				}
				else if(RubyVirtualFileScanner.isRubyFile(file))
				{
					return new RailsControllerToViewMarkersPass(module, psiFile, editor);
				}
			}
		}

		//[HACK] may be it is hack.. Removes all Rails Highlighter if Rails Facet was deleted from module.
		final Document document = editor.getDocument();
		final Project project = psiFile.getProject();
		final MarkupModel markupModel = DocumentMarkupModel.forDocument(document, project, false);
		final RailsLineMarkerInfo[] markers = DaemonCodeAnalyzerUtil.getLineMarkers(document, project);
		//removes all Rails specific markers
		if(markers != null)
		{
			for(RailsLineMarkerInfo marker : markers)
			{
				markupModel.removeHighlighter(marker.highlighter);
			}
		}
		return null;
	}

	@Override
	public void projectOpened()
	{
	}

	@Override
	public void projectClosed()
	{
	}

	@Override
	@NonNls
	@NotNull
	public String getComponentName()
	{
		return RailsComponents.RAILS_HIGHLIGHT_PASS_FACTORY;
	}

	@Override
	public void initComponent()
	{
		myRegistrar.registerTextEditorHighlightingPass(this, TextEditorHighlightingPassRegistrar.Anchor.LAST, Pass.UPDATE_ALL, true, true);
	}

	@Override
	public void disposeComponent()
	{
	}
}

