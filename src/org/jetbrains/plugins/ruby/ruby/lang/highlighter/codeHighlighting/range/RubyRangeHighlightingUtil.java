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

package org.jetbrains.plugins.ruby.ruby.lang.highlighter.codeHighlighting.range;

import java.util.ArrayList;
import java.util.Collection;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 26, 2007
 */
public class RubyRangeHighlightingUtil
{
	private static final Logger LOG = Logger.getInstance(RubyRangeHighlightingUtil.class.getName());

	public static void setHighlightInfosToEditor(final Project project, final Document document, final Collection<HighlightInfo> markers)
	{
		ApplicationManager.getApplication().assertIsDispatchThread();

		final ArrayList<HighlightInfo> array = new ArrayList<HighlightInfo>();
		final HighlightInfo[] oldMarkers = RubyRangeHighlightDaemon.getHighlightInfos(document, project);

      /*  final MarkupModel markupModel =  DocumentMarkupModel.forDocument(document, project, false);
		if (oldMarkers != null) {
            for (HighlightInfo info : oldMarkers) {
                RangeHighlighter highlighter = info.highlighter;
                    markupModel.removeHighlighter(highlighter);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Removed line markers:" + (oldMarkers.length - array.size()));
            }
        }   */

		final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
		for(HighlightInfo info : markers)
		{
            /*info.highlighter =
                    markupModel.addRangeHighlighter(
                            info.startOffset,
                            info.endOffset,
                            HighlighterLayer.ADDITIONAL_SYNTAX,
                            info.getTextAttributes(psiFile),
                            HighlighterTargetArea.EXACT_RANGE);  */
			array.add(info);
		}

		RubyRangeHighlightDaemon.setHighligthInfos(document, array.toArray(new HighlightInfo[array.size()]), project);

		if(LOG.isDebugEnabled())
		{
			LOG.debug("Added line markers:" + markers.size());
		}
	}
}
