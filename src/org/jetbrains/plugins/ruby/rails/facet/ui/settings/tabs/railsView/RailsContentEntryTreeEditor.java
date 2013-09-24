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

package org.jetbrains.plugins.ruby.rails.facet.ui.settings.tabs.railsView;

import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.module.Module;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.RContentEntryTreeEditor;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.ToggleContentFolderStateAction;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 25, 2007
 */
public class RailsContentEntryTreeEditor extends RContentEntryTreeEditor {
    private final ToggleContentFolderStateAction myToggleRailsUserFolderAction;

    public RailsContentEntryTreeEditor(final Module module) {
        super(module);

        //rails additional sources
        myToggleRailsUserFolderAction =
                new RailsViewToggleContentFolderStateAction(myTree, this, ToggleContentFolderStateAction.ContentType.RAILS_VIEW_USER_FOLDER);

        myToggleRailsUserFolderAction.registerCustomShortcutSet(new CustomShortcutSet(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.ALT_MASK)), myTree);

    }

    protected void createEditingActions() {
        myEditingActionsGroup.add(myToggleRailsUserFolderAction);
    }

    protected TreeCellRenderer getContentEntryCellRenderer() {
        return new RailsContentEntryTreeCellRenderer(this);
    }
}
