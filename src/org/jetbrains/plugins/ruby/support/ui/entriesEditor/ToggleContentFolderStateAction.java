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

package org.jetbrains.plugins.ruby.support.ui.entriesEditor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.roots.ExcludeFolder;
import com.intellij.openapi.roots.ui.configuration.IconSet;
import com.intellij.openapi.roots.ui.configuration.actions.ContentEntryEditingAction;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.plugins.ruby.RBundle;
import static org.jetbrains.plugins.ruby.support.ui.entriesEditor.ToggleContentFolderStateAction.ContentType.EXCLUDED;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Eugene Zhuravlev, Roman Chernyatchik
 * @date: Aug 20, 2007
 */
@SuppressWarnings({"ComponentNotRegistered"})
public abstract class ToggleContentFolderStateAction extends ContentEntryEditingAction {
    protected final RContentEntryTreeEditor myEntryTreeEditor;
    protected final ContentType myContentType;

    public ToggleContentFolderStateAction(final JTree tree,
                                     final RContentEntryTreeEditor entryEditor,
                                     final ContentType contentType) {
        super(tree);
        myEntryTreeEditor = entryEditor;
        myContentType = contentType;

        final Presentation presentation = getTemplatePresentation();
        if (EXCLUDED.equals(myContentType)) {
            presentation.setText(RBundle.message("module.toggle.excluded.action"));
            presentation.setDescription(RBundle.message("module.toggle.excluded.action.description"));
            presentation.setIcon(IconSet.EXCLUDE_FOLDER);
        }
    }

    public boolean isSelected(final AnActionEvent e) {
        final VirtualFile[] selectedFiles = getSelectedFiles();
        if (selectedFiles == null || selectedFiles.length == 0) {
            return false;
        }
        final RContentEntryEditor contentEntryEditor =
                myEntryTreeEditor.getContentEntryEditor();

        //noinspection SimplifiableIfStatement
        if (EXCLUDED.equals(myContentType)) {
                return contentEntryEditor.isExcluded(selectedFiles[0])
                        || contentEntryEditor.isUnderExcludedDirectory(selectedFiles[0]);
        }
        return false;
    }

  public void setSelected(final AnActionEvent e, final boolean isSelected) {
    final VirtualFile[] selectedFiles = getSelectedFiles();
      if (selectedFiles == null || selectedFiles.length == 0) {
          return;
      }

      final RContentEntryEditor contentEntryEditor = myEntryTreeEditor.getContentEntryEditor();
      final VirtualFile sFile = selectedFiles[0];

      if (EXCLUDED.equals(myContentType)) {
          final ExcludeFolder excludeFolder =
                  contentEntryEditor.getExcludeFolder(sFile);
          if (isSelected) {
              // not excluded yet
              if (excludeFolder == null) {
                  contentEntryEditor.addExcludeFolder(sFile);
              }
          } else {
              if (excludeFolder != null) {
                  contentEntryEditor.removeExcludeFolder(excludeFolder);
              }
          }
      }
  }


  public void update(final AnActionEvent e) {
    super.update(e);
    final Presentation presentation = e.getPresentation();

      final VirtualFile[] selectedFiles = getSelectedFiles();

      String text = null;
      if (EXCLUDED.equals(myContentType)) {
              text = RBundle.message("module.toggle.excluded.action");
              if (selectedFiles != null && selectedFiles.length > 0) {
                  presentation.setEnabled(true);
              }
      }
      presentation.setText(text);
  }

    public enum ContentType {
        /**
         * Excluded folders
         */
        EXCLUDED,
        /**
         * Ruby test folder
         */
        TEST,
        /**
         * Additional folders for rails view, e.g. shared views, etc.
         */
        RAILS_VIEW_USER_FOLDER
    }
}