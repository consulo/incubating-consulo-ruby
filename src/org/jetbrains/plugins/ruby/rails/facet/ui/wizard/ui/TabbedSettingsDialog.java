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

package org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui;

import com.intellij.facet.impl.ui.FacetErrorPanel;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.TabbedPaneWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 4, 2008
 */
public class TabbedSettingsDialog extends DialogWrapper {
    private final TabbedSettingsEditorTab[] myEditorTabs;
    private final Set<FacetEditorTab> myVisitedTabs = new HashSet<FacetEditorTab>();
    private final FacetErrorPanel myErrorPanel;

//    private @Nullable TabbedPaneWrapper myTabbedPane;
    private int mySelectedTabIndex = 0;

    protected TabbedSettingsDialog(@Nullable final Project project,
                                   final String title,
                                   @NotNull final TabbedSettingsEditorTab[] editorTabs,
                                   @NotNull final TabbedSettingsContext context) {
        super(project, true);
        myEditorTabs = editorTabs;

        setTitle(title);
        myErrorPanel = new FacetErrorPanel();
        for (TabbedSettingsEditorTab tab : editorTabs) {
            tab.setContext(context);
            tab.reset();
        }
        init();
    }

    /**
     * Creates TabbedSettingsDilogs and shows it. In Unit Test or headless modes always returns 0 and doesn't show dialog.
     *
     * @param title      Dialog title
     * @param editorTabs Settings tabs
     * @param context    Settings context
     * @return Exit code. (See exit codes constants in com.intellij.openapi.DialogWrapper)
     */
    public static int showDialog(final String title,
                                 @NotNull final TabbedSettingsEditorTab[] editorTabs,
                                 @NotNull final TabbedSettingsContext context) {
        final Application application = ApplicationManager.getApplication();
        if (application.isUnitTestMode() || application.isHeadlessEnvironment()) {
            //TODO
            //        return ourTestImplementation.show(message);
            return 0;
        }

        final TabbedSettingsDialog dialog = new TabbedSettingsDialog(null, title, editorTabs, context);
        dialog.show();
        return dialog.getExitCode();
    }

    public void show() {
        for (TabbedSettingsEditorTab tab : myEditorTabs) {
            tab.beforeShow();
        }
        onTabSelected(getSelectedTab());
        super.show();
    }

    public TabbedSettingsEditorTab getSelectedTab() {
        return myEditorTabs[mySelectedTabIndex];
    }

    protected void createDefaultActions() {
        super.createDefaultActions();

        getHelpAction().setEnabled(false);
        getCancelAction().setEnabled(true);
    }

    protected Action[] createActions() {
      return new Action[]{getOKAction(), getCancelAction()};
    }    

    protected void doOKAction() {
      if (getOKAction().isEnabled()) {
          for (FacetEditorTab visitedTab : myVisitedTabs) {
              try {
                  visitedTab.apply();
              } catch (ConfigurationException e) {
                  Messages.showErrorDialog(getContentPane(), e.getMessage(), e.getTitle());
                  return;
              }
          }
      }
      super.doOKAction();
    }

    public void doCancelAction() {
        if (getCancelAction().isEnabled()) {
            for (TabbedSettingsEditorTab editorTab : myEditorTabs) {
                editorTab.reset();
            }
        }
        super.doCancelAction();
    }

    protected void dispose() {
        myErrorPanel.disposeUIResources();
        super.dispose();
    }

    @Nullable
    protected JComponent createCenterPanel() {
        final JComponent editorComponent;
        if (myEditorTabs.length > 1) {
            final TabbedPaneWrapper tabbedPane = new TabbedPaneWrapper();
            for (FacetEditorTab editorTab : myEditorTabs) {
                tabbedPane.addTab(editorTab.getDisplayName(), editorTab.getIcon(), editorTab.createComponent(), null);
            }
            tabbedPane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    myEditorTabs[mySelectedTabIndex].onTabLeaving();
                    mySelectedTabIndex = tabbedPane.getSelectedIndex();
                    onTabSelected(myEditorTabs[mySelectedTabIndex]);
                }
            });
            editorComponent = tabbedPane.getComponent();
//            myTabbedPane = tabbedPane;
        } else if (myEditorTabs.length == 1) {
            editorComponent = myEditorTabs[0].createComponent();
        } else {
            editorComponent = new JPanel();
        }
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(BorderLayout.CENTER, editorComponent);
//        panel.add(BorderLayout.SOUTH, myErrorPanel.getComponent());
//        myErrorPanel.getValidatorsManager().registerValidator(new FacetEditorValidator() {
//            public ValidationResult check() {
//                if (mySelectedTabIndex != 0) {
//                    return new ValidationResult("Please select first tab! Instead of: " + myEditorTabs[mySelectedTabIndex].getDisplayName());
//                }
//                return ValidationResult.OK;
//            }
//        }, editorComponent);

        return panel;
    }

    private void onTabSelected(final FacetEditorTab selectedTab) {
        selectedTab.onTabEntering();
        // If we have already visited tab than we used previously foced component in it
        // otherwise we want focus preferredFocusedComponent

        if (myVisitedTabs.add(selectedTab)) {
            final JComponent preferredFocusedComponent = selectedTab.getPreferredFocusedComponent();
            if (preferredFocusedComponent != null) {
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    public void run() {
                        if (preferredFocusedComponent.isShowing()) {
                            preferredFocusedComponent.requestFocus();
                        }
                    }
                });
            }
        }
    }
}
