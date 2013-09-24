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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 25, 2007
 */
public class RubyLineHighlightingUtil {
    private static final Logger LOG = Logger.getInstance(RubyLineHighlightingUtil.class.getName());

    public static void setLineMarkersToEditor(final Project project,
                                              final Document document,
                                              final Collection<RubyLineMarkerInfo> markers,
                                              final boolean slow) {
        ApplicationManager.getApplication().assertIsDispatchThread();

        final ArrayList<RubyLineMarkerInfo> array = new ArrayList<RubyLineMarkerInfo>();
        final RubyLineMarkerInfo[] oldMarkers = RubyLineHighlightDaemon.getLineMarkers(document, project);

        final MarkupModel markupModel = document.getMarkupModel(project);
        if (oldMarkers != null) {
            for (RubyLineMarkerInfo info : oldMarkers) {
                final RangeHighlighter highlighter = info.highlighter;
                final boolean toRemove = !highlighter.isValid() || info.isSlow == slow;
                if (toRemove){
                    markupModel.removeHighlighter(highlighter);
                } else {
                    array.add(info);
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Removed line markers:" + (oldMarkers.length - array.size()));
            }
        }

        for (RubyLineMarkerInfo info : markers) {
            assert info.isSlow == slow;
            RangeHighlighter marker = markupModel.addRangeHighlighter(
                    info.startOffset,
                    info.startOffset,
                    HighlighterLayer.ADDITIONAL_SYNTAX,
                    info.attributes,
                    HighlighterTargetArea.LINES_IN_RANGE);
            marker.setGutterIconRenderer(info.createGutterRenderer());
            marker.setLineSeparatorColor(info.separatorColor);
            marker.setLineSeparatorPlacement(info.separatorPlacement);
            info.highlighter = marker;
            array.add(info);
        }

        RubyLineMarkerInfo[] newMarkers = array.toArray(new RubyLineMarkerInfo[array.size()]);
        RubyLineHighlightDaemon.setRubyLineMarkers(document, newMarkers, project);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Added line markers:" + markers.size());
        }
    }
}

