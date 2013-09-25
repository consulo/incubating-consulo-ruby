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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.HighlightPassConstants;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.actions.navigation.SwitchToView;
import org.jetbrains.plugins.ruby.rails.codeInsight.daemon.RailsLineMarkerInfo;
import org.jetbrains.plugins.ruby.rails.codeInsight.daemon.RailsUpdateHighlightersUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.ruby.lang.highlighter.RubyHighlightUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 02.02.2007
 */
public class RailsControllerToViewMarkersPass extends ElementsStartsAtRangeHighlighterPass {
    /**
     * "Go to view of action" markers
     */
    private Collection<RailsLineMarkerInfo> myActionToViewMarkers = Collections.emptyList();
    private final Module myModule;

    public RailsControllerToViewMarkersPass(final Module module, final PsiFile psiFile,
                                            final Editor editor) {
        super(module.getProject(),
             psiFile, editor, false, HighlightPassConstants.CONTROLLER_TO_VIEW_MARKERS_GROUP);
        myModule = module;
        assert RailsFacetUtil.hasRailsSupport(module);
    }

    @Override
	public void doCollectInformation(final ProgressIndicator progress) {
        // We call this often enough
        ProgressManager.getInstance().checkCanceled();

        ApplicationManager.getApplication().assertReadAccessAllowed();
        
        myActionToViewMarkers = new ArrayList<RailsLineMarkerInfo>();

        for (PsiElement element : getElementsInRange()) {
            if (element instanceof RMethod
                || element instanceof RClass) {
                addActionViewInfo(myActionToViewMarkers, (RContainer)element);
            }
        }
    }

    private void addActionViewInfo(@NotNull final Collection<RailsLineMarkerInfo> viewMarkers,
                                   @NotNull final RContainer methodOrClass) {

        if (SwitchToView.isSwitchToViewEnabled(methodOrClass, myModule)) {
            final RailsLineMarkerInfo info =
                    new RailsLineMarkerInfo(myModule,
                            RailsLineMarkerInfo.MarkerType.CONTROLLER_TO_VIEW,
                            methodOrClass,
                            RubyHighlightUtil.getStartOffset(methodOrClass),
                            RailsIcons.RAILS_ACTION_TO_VIEW_MARKER);
            viewMarkers.add(info);
        }
    }

    @Override
	public void doApplyInformationToEditor() {
        RailsUpdateHighlightersUtil.setLineMarkersToEditor(myProject, myDocument,
                                                      myActionToViewMarkers,
                                                      HighlightPassConstants.CONTROLLER_TO_VIEW_MARKERS_GROUP);
    }
}
