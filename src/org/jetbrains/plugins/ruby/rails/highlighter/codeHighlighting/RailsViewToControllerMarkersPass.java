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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.plugins.ruby.HighlightPassConstants;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.actions.navigation.SwitchToController;
import org.jetbrains.plugins.ruby.rails.codeInsight.daemon.RailsLineMarkerInfo;
import org.jetbrains.plugins.ruby.rails.codeInsight.daemon.RailsUpdateHighlightersUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.ruby.lang.highlighter.RubyHighlightUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 04.02.2007
 */
public class RailsViewToControllerMarkersPass extends ElementsStartsAtRangeHighlighterPass {
    /**
     * "Switch to controller of partial view" markers
     */
    private Collection<RailsLineMarkerInfo> myViewToControllerMarkers = Collections.emptyList();
    private final Module myModule;

    public RailsViewToControllerMarkersPass(final Module module, final PsiFile psiFile,
                                            final Editor editor) {
        super(module.getProject(),
             psiFile, editor, true, HighlightPassConstants.VIEW_TO_CONTROLLER_MARKERS_GROUP);
        myModule = module;
        assert RailsFacetUtil.hasRailsSupport(module);
    }

    @Override
	public void doCollectInformation(final ProgressIndicator progress) {
        // We call this often enough
        ProgressManager.getInstance().checkCanceled();

        ApplicationManager.getApplication().assertReadAccessAllowed();

        myViewToControllerMarkers = new ArrayList<RailsLineMarkerInfo>();

        if (!SwitchToController.isSwitchToControllerEnabled(myFile, myModule)) {
            return;
        }

        final RailsLineMarkerInfo info =
                new RailsLineMarkerInfo(myModule,
                    RailsLineMarkerInfo.MarkerType.VIEW_TO_CONTROLLER,
                    myFile,
                    RubyHighlightUtil.getStartOffset(myFile),
                    RailsIcons.RAILS_VIEW_TO_CONTROLLER_MARKER);
        myViewToControllerMarkers.add(info);
    }

    @Override
	public void doApplyInformationToEditor() {
        RailsUpdateHighlightersUtil.setLineMarkersToEditor(myProject, myDocument,
                                                      myViewToControllerMarkers,
                                                      HighlightPassConstants.VIEW_TO_CONTROLLER_MARKERS_GROUP);

    }
}