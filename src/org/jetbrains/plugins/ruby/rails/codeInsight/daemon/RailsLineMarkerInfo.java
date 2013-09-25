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

import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.SeparatorPlacement;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.reference.SoftReference;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 04.02.2007
 */
public class RailsLineMarkerInfo {
    public static final RailsLineMarkerInfo[] EMPTY_ARRAY = new RailsLineMarkerInfo[0];

    public final int startOffset;
    public TextAttributes attributes;
    public Color separatorColor;
    public SeparatorPlacement separatorPlacement;
    public RangeHighlighter highlighter;
    public final MarkerType type;

    private final SoftReference<PsiElement> elementRef;
    private Icon myIcon;
    private final Module myModule;

    public RailsLineMarkerInfo(@NotNull final Module module,
                              @NotNull final MarkerType type, final PsiElement element,
                              final int startOffset, final Icon icon) {
        this.type = type;
        myIcon = icon;
        elementRef = new SoftReference<PsiElement>(element);
        this.startOffset = startOffset;
        myModule = module;
    }

    @Nullable
    public GutterIconRenderer createGutterRenderer() {
        if (myIcon == null) {
            return null;
        }
        return new MyGutterIconRenderer();
    }

    public SoftReference<PsiElement> getElementRef() {
        return elementRef;
    }

    private String getMethodTooltip(final RMethod method) {
        switch (type) {
            case CONTROLLER_TO_VIEW:
                return RBundle.message("codeInsight.rails.action_to_view.tooltip", method.getName());
            default:
                return null;
        }
    }

    private String getPsiFileTooltip(final PsiFile psiFile) {
        switch (type) {
            case VIEW_TO_ACTION:
                return RBundle.message("codeInsight.rails.view_to_action.tooltip", psiFile.getName());
            default:
                return null;
        }
    }

    public enum MarkerType {
        CONTROLLER_TO_VIEW,
        VIEW_TO_ACTION,
        VIEW_TO_CONTROLLER
    }

    private class MyGutterIconRenderer extends GutterIconRenderer {
        @Override
		@NotNull
        public Icon getIcon() {
            return myIcon;
        }

        @Override
		public AnAction getClickAction() {
            return new MyNavigateAction();
        }

        @Override
		public boolean isNavigateAction() {
            return true;
        }

        @Override
		public String getTooltipText() {
            final PsiElement element = elementRef.get();
            if (element == null || !element.isValid()) {
                return null;
            }

            final Ref<String> toolTip = new Ref<String>();
            element.accept(new RubyElementVisitor() {

                @Override
				public void visitRMethod(final RMethod rMethod) {
                    toolTip.set(getMethodTooltip(rMethod));
                }

                @Override
				public void visitFile(final PsiFile psiFile) {
                    toolTip.set(getPsiFileTooltip(psiFile));
                }
            });
            return toolTip.get();
        }

        @Override
		public com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment getAlignment() {
            return type == MarkerType.CONTROLLER_TO_VIEW ? Alignment.LEFT : Alignment.RIGHT;
        }

		@Override
		public boolean equals(Object o)
		{
			return false;
		}

		@Override
		public int hashCode()
		{
			return 0;
		}
	}

    private class MyNavigateAction extends AnAction {
        @Override
		public void actionPerformed(final AnActionEvent e) {
            MouseEvent mouseEvent = (MouseEvent)e.getInputEvent();
            RailsLineMarkerNavigator.browse(mouseEvent, RailsLineMarkerInfo.this, myModule);
        }
    }
}
