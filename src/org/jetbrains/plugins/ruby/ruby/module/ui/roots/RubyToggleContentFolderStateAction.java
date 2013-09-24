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

package org.jetbrains.plugins.ruby.ruby.module.ui.roots;

import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.IconSet;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.RContentEntryTreeEditor;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.ToggleContentFolderStateAction;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.RContentEntryEditor;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 22, 2008
 */
@SuppressWarnings({"ComponentNotRegistered"})
public class RubyToggleContentFolderStateAction extends ToggleContentFolderStateAction {

    public RubyToggleContentFolderStateAction(final JTree tree, final RContentEntryTreeEditor entryEditor, final ContentType contentType) {
        super(tree, entryEditor, contentType);

        final Presentation presentation = getTemplatePresentation();
        if (ContentType.TEST.equals(contentType)) {
                presentation.setText(RBundle.message("module.toggle.test.sources.action"));
                presentation.setDescription(RBundle.message("module.toggle.test.sources.action.description"));
                presentation.setIcon(IconSet.TEST_ROOT_FOLDER);
        }
    }

    public boolean isSelected(final AnActionEvent e) {

        final RContentEntryEditor contentEntryEditor =
                myEntryTreeEditor.getContentEntryEditor();

        final VirtualFile[] selectedFiles = getSelectedFiles();
        if (selectedFiles == null || selectedFiles.length == 0) {
            return false;
        }

        if (ContentType.TEST.equals(myContentType)) {
            return ((RubyContentEntryEditor)contentEntryEditor).isTestSource(selectedFiles[0]);
        }

        return super.isSelected(e);
    }

    public void setSelected(final AnActionEvent e, final boolean isSelected) {
      final VirtualFile[] selectedFiles = getSelectedFiles();
        if (selectedFiles == null || selectedFiles.length == 0) {
            return;
        }

        final RubyContentEntryEditor contentEntryEditor = (RubyContentEntryEditor)myEntryTreeEditor.getContentEntryEditor();
        final VirtualFile sFile = selectedFiles[0];

        if (myContentType.equals(ContentType.TEST)) {
            final boolean isTestsFolder =
                    contentEntryEditor.isTestSource(sFile);
            if (isSelected) {
                if (!isTestsFolder) {
                    contentEntryEditor.addTestSourceFolder(sFile);
                }
            } else {
                if (isTestsFolder) {
                    contentEntryEditor.removeTestSourceFolder(sFile);
                }
            }
        } else {
            super.setSelected(e, isSelected);
        }
    }

      public void update(final AnActionEvent e) {
          super.update(e);
          final Presentation presentation = e.getPresentation();

          final VirtualFile[] selectedFiles = getSelectedFiles();
          final RContentEntryEditor contentEntryEditor = myEntryTreeEditor.getContentEntryEditor();

          String text = null;
          switch (myContentType) {
              case EXCLUDED:
                  text = RBundle.message("module.toggle.excluded.action");
                  if (selectedFiles != null && selectedFiles.length > 0) {
                      presentation.setEnabled(!((RubyContentEntryEditor)contentEntryEditor).isTestSource(selectedFiles[0]));
                  }
                  break;
              case TEST:
                  text = RBundle.message("module.toggle.test.sources.action");
                  if (selectedFiles != null && selectedFiles.length > 0) {
                      presentation.setEnabled(!contentEntryEditor.isExcluded(selectedFiles[0])
                              && !contentEntryEditor.isUnderExcludedDirectory(selectedFiles[0]));
                  }
                  break;
          }
          presentation.setText(text);
      }
}
