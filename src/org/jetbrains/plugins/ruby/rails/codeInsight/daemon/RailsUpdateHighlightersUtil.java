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

package org.jetbrains.plugins.ruby.rails.codeInsight.daemon;

import java.util.ArrayList;
import java.util.Collection;

import org.jetbrains.plugins.ruby.HighlightPassConstants;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 04.02.2007
 */
public class RailsUpdateHighlightersUtil {
    private static final Logger LOG = Logger.getInstance(RailsUpdateHighlightersUtil.class.getName());

    public static void setLineMarkersToEditor(final Project project,
                                              final Document document,
                                              final Collection<RailsLineMarkerInfo> markers,
                                              final int group) {
        ApplicationManager.getApplication().assertIsDispatchThread();

        final ArrayList<RailsLineMarkerInfo> array = new ArrayList<RailsLineMarkerInfo>();
        final RailsLineMarkerInfo[] oldMarkers = DaemonCodeAnalyzerUtil.getLineMarkers(document, project);

        final MarkupModel markupModel = DocumentMarkupModel.forDocument(document, project, false);
        if (oldMarkers != null) {
            for (RailsLineMarkerInfo info : oldMarkers) {
                final RangeHighlighter highlighter = info.highlighter;
                boolean toRemove = !highlighter.isValid() || isLineMarkerInGroup(info.type, group);
                if (toRemove) {
                    markupModel.removeHighlighter(highlighter);
                } else {
                    array.add(info);
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Removed line markers:" + (oldMarkers.length - array.size()));
            }
        }

        for (RailsLineMarkerInfo info : markers) {
           RangeHighlighter marker = markupModel.addRangeHighlighter(info.startOffset,
                                                                                     info.startOffset,
                                                                                     HighlighterLayer.ADDITIONAL_SYNTAX,
                                                                                     info.attributes,
                                                                                     HighlighterTargetArea.EXACT_RANGE);
            marker.setGutterIconRenderer(info.createGutterRenderer());
            marker.setLineSeparatorColor(info.separatorColor);
            marker.setLineSeparatorPlacement(info.separatorPlacement);
            info.highlighter = marker;
            array.add(info);
        }

        RailsLineMarkerInfo[] newMarkers = array.toArray(new RailsLineMarkerInfo[array.size()]);
        DaemonCodeAnalyzerUtil.setRubyLineMarkers(document, newMarkers, project);
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Added line markers:" + markers.size());
        }
    }

    private static boolean isLineMarkerInGroup(final RailsLineMarkerInfo.MarkerType type,
                                               final int group) {
        switch (type) {
            case CONTROLLER_TO_VIEW:
                return group == HighlightPassConstants.CONTROLLER_TO_VIEW_MARKERS_GROUP;
            case VIEW_TO_ACTION:
                return group == HighlightPassConstants.VIEW_TO_ACTION_MARKERS_GROUP;
            case VIEW_TO_CONTROLLER:
                return group == HighlightPassConstants.VIEW_TO_CONTROLLER_MARKERS_GROUP;
            default:
                LOG.assertTrue(false);
                return false;
        }
    }
}
