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

package org.jetbrains.plugins.ruby.settings;

import org.jetbrains.plugins.ruby.support.OpenLinkInBrowserHyperlinkListener;

import javax.swing.*;
import java.util.jar.Manifest;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 22.04.2007
 */
public class AboutPluginPane {
    private JLabel myPluginBuildInfo;
    private JLabel mySvnRevisionInfo;
    private JTextPane myHomePageInfo;
    private JTextPane myRecentChangesInfo;
    private JTextPane myIssueTrackerInfo;
    private JPanel myContentPane;
    private JTextPane myForumInfo;
    private JTextPane myPluginRepository;

    public AboutPluginPane() {
        final Manifest manifest = RPluginInfoUtil.getManifest();

        final String build = RPluginInfoUtil.getBuild(manifest);
        if (build != null) {
            myPluginBuildInfo.setText(build);
        }

        final String revision = RPluginInfoUtil.getRevision(manifest);
        if (revision != null) {
            mySvnRevisionInfo.setText(revision);
        }

        initHyperLinkListener(RPluginInfoUtil.HOME_PAGE_URL, myHomePageInfo);
        initHyperLinkListener(RPluginInfoUtil.RECENT_CHANGES_URL, myRecentChangesInfo);
        initHyperLinkListener(RPluginInfoUtil.JIRA_BROWSE_RUBY_URL, myIssueTrackerInfo);
        initHyperLinkListener(RPluginInfoUtil.FORUM_URL, myForumInfo);
        initHyperLinkListener(RPluginInfoUtil.PLUGIN_REPOSITORY_URL, myPluginRepository);
    }

    private void initHyperLinkListener(final String hyperLinkText, final JTextPane textPane) {
        textPane.addHyperlinkListener(new OpenLinkInBrowserHyperlinkListener(myContentPane));
        textPane.setText("<html><a href=\"" + hyperLinkText + "\">" + hyperLinkText + "</a></html>");
        textPane.setBackground(myContentPane.getBackground());
    }

    public JPanel getContentPanel() {
        return myContentPane;
    }
}
