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

package org.jetbrains.plugins.ruby.ruby.lang.highlighter.codeHighlighting.line;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.HighlightPassConstants;
import org.jetbrains.plugins.ruby.ruby.lang.documentation.RubyHelpUtil;
import org.jetbrains.plugins.ruby.ruby.lang.highlighter.RubyHighlightUtil;
import org.jetbrains.plugins.ruby.ruby.lang.highlighter.codeHighlighting.AbstractRubyHighlighterPass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RStructuralElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.SeparatorPlacement;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 25, 2007
 */
public class RubyFastLineHighlightPass extends AbstractRubyHighlighterPass
{
	private static final DaemonCodeAnalyzerSettings mySettings = DaemonCodeAnalyzerSettings.getInstance();
	private static final EditorColorsScheme myScheme = EditorColorsManager.getInstance().getGlobalScheme();

	private Collection<RubyLineMarkerInfo> myLineMarkers;


	public RubyFastLineHighlightPass(@Nonnull final Project project, @Nonnull final RFile psiFile, @Nonnull final Editor editor)
	{
		super(project, psiFile, editor, false, HighlightPassConstants.RUBY_LINE_MARKERS_GROUP);
	}

	@Override
	public void doCollectInformation(final ProgressIndicator progress)
	{
		ApplicationManager.getApplication().assertReadAccessAllowed();

		myLineMarkers = new ArrayList<RubyLineMarkerInfo>();
		if(mySettings.SHOW_METHOD_SEPARATORS)
		{
			setSeparators();
		}
	}

	@Override
	public void doApplyInformationToEditor()
	{
		RubyLineHighlightingUtil.setLineMarkersToEditor(myProject, myDocument, myLineMarkers, false);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Separators gathering
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gathers information about all the containers separators
	 */
	private void setSeparators()
	{
		for(PsiElement element : getElementsInRange())
		{
			// Hope it`s often enough
			ProgressManager.getInstance().checkCanceled();
			addSeparatorIfNeeded(element);
		}
	}

	/**
	 * Recursively gathers information about all the containers separators
	 *
	 * @param element The parent container to collect information
	 */
	private void addSeparatorIfNeeded(final PsiElement element)
	{
		if(element instanceof RContainer)
		{
			List<RStructuralElement> elements = ((RContainer) element).getStructureElements();
			// we ignore fisrt method separator
			boolean containerSeen = false;
			for(RStructuralElement child : elements)
			{
				if(child.getType().isContainer())
				{
					if(containerSeen)
					{
						// we should show separator before the comments
						final List<PsiComment> comments = RubyHelpUtil.getPsiComments(child);
						final RubyLineMarkerInfo info = comments.isEmpty() ? new RubyLineMarkerInfo(RubyHighlightUtil.getStartOffset(child), false) : new RubyLineMarkerInfo(RubyHighlightUtil.getStartOffset(comments.get(0)), false);
						info.separatorColor = myScheme.getColor(CodeInsightColors.METHOD_SEPARATORS_COLOR);
						info.separatorPlacement = SeparatorPlacement.TOP;
						myLineMarkers.add(info);
					}
					else
					{
						containerSeen = true;
					}
				}
			}
		}
	}
}