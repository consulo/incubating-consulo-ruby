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

import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ContentFolder;
import com.intellij.openapi.roots.ExcludeFolder;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.roots.ui.configuration.FilePathClipper;
import com.intellij.openapi.roots.ui.configuration.IconActionComponent;
import com.intellij.openapi.roots.ui.configuration.ResizingWrapper;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.HoverHyperlinkLabel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 22, 2008
 */
public abstract class RAbstractContentRootPanel extends JPanel {
    private static final Color SELECTED_HEADER_COLOR = new Color(0xDEF2FF);
    private static final Color HEADER_COLOR = new Color(0xF5F5F5);
    private static final Color SELECTED_CONTENT_COLOR = new Color(0xF0F9FF);
    private static final Color CONTENT_COLOR = Color.WHITE;
    private static final Color UNSELECTED_TEXT_COLOR = new Color(0x333333);

    private static final Icon DELETE_FOLDER_ICON = IconLoader.getIcon("/modules/deleteContentFolder.png");
    private static final Icon DELETE_FOLDER_ROLLOVER_ICON = IconLoader.getIcon("/modules/deleteContentFolderRollover.png");

    protected final ContentEntry myContentEntry;

    private JComponent myHeader;
    private JComponent myBottom;
    private Map<JComponent, Color> myComponentToForegroundMap = new HashMap<JComponent, Color>();

    protected RContentEntryEditor myCallBack;

    protected RAbstractContentRootPanel(final ContentEntry contentEntry, final RContentEntryEditor editorCallBack) {
        super(new GridBagLayout());
        myContentEntry = contentEntry;
        myCallBack = editorCallBack;

        myHeader = createHeader();
        this.add(myHeader, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 8, 0), 0, 0));
        addEntryEditorComponents();

        myBottom = new JPanel(new BorderLayout());
        myBottom.add(Box.createVerticalStrut(3), BorderLayout.NORTH);
        this.add(myBottom, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        setSelected(false);
    }

    protected void addEntryEditorComponents() {
        // init
    }

    private JComponent createHeader() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final JLabel headerLabel = new JLabel(toDisplayPath(myContentEntry.getUrl()));
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
        headerLabel.setOpaque(false);
        if (myContentEntry.getFile() == null) {
            headerLabel.setForeground(Color.RED);
        }
        final ResizingWrapper wrapper = new ResizingWrapper(headerLabel);
        panel.add(wrapper, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 0, 0));
        FilePathClipper.install(headerLabel, wrapper);
        return panel;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            myHeader.setBackground(SELECTED_HEADER_COLOR);
            setBackground(SELECTED_CONTENT_COLOR);
            myBottom.setBackground(SELECTED_HEADER_COLOR);
            for (final JComponent component : myComponentToForegroundMap.keySet()) {
                component.setForeground(myComponentToForegroundMap.get(component));
            }
        } else {
            myHeader.setBackground(HEADER_COLOR);
            setBackground(CONTENT_COLOR);
            myBottom.setBackground(HEADER_COLOR);
            for (final JComponent component : myComponentToForegroundMap.keySet()) {
                component.setForeground(UNSELECTED_TEXT_COLOR);
            }
        }
    }

    protected JComponent createFolderGroupComponent(String title, ContentFolder[] folders, Color foregroundColor) {

        final JPanel panel = new JPanel(new GridLayoutManager(folders.length, 3, new Insets(1, 17, 0, 2), 0, 1));
        panel.setOpaque(false);

        for (int idx = 0; idx < folders.length; idx++) {
            final ContentFolder folder = folders[idx];
            final int verticalPolicy = idx == folders.length - 1 ? GridConstraints.SIZEPOLICY_CAN_GROW : GridConstraints.SIZEPOLICY_FIXED;
            panel.add(createFolderComponent(folder, foregroundColor), new GridConstraints(idx, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK, verticalPolicy, null, null, null));
            int column = 1;
            int colspan = 2;
            panel.add(createFolderDeleteComponent(folder), new GridConstraints(idx, column, 1, colspan, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, verticalPolicy, null, null, null));
        }

        final JLabel titleLabel = new JLabel(title);
        final Font labelFont = UIUtil.getLabelFont();
        titleLabel.setFont(labelFont.deriveFont(Font.BOLD).deriveFont((float)labelFont.getSize() - 0.5f));
        titleLabel.setOpaque(false);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        registerTextComponent(titleLabel, foregroundColor);

        final JPanel groupPanel = new JPanel(new BorderLayout());
        groupPanel.setOpaque(false);
        groupPanel.add(titleLabel, BorderLayout.NORTH);
        groupPanel.add(panel, BorderLayout.CENTER);

        return groupPanel;
    }

    private void registerTextComponent(final JComponent component, final Color foreground) {
        component.setForeground(foreground);
        myComponentToForegroundMap.put(component, foreground);
    }

    private JComponent createFolderComponent(final ContentFolder folder, Color foreground) {
        final VirtualFile folderFile = folder.getFile();
        final VirtualFile contentEntryFile = myContentEntry.getFile();
        if (folderFile != null && contentEntryFile != null) {
            String path = folderFile.equals(contentEntryFile) ? "." : VfsUtil.getRelativePath(folderFile, contentEntryFile, File.separatorChar);
            HoverHyperlinkLabel hyperlinkLabel = new HoverHyperlinkLabel(path, foreground);
            hyperlinkLabel.setMinimumSize(new Dimension(0, 0));
            hyperlinkLabel.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    myCallBack.navigateFolder(myContentEntry, folder);
                }
            });
            registerTextComponent(hyperlinkLabel, foreground);
            return new UnderlinedPathLabel(hyperlinkLabel);
        } else {
            String path = toRelativeDisplayPath(folder.getUrl(), myContentEntry.getUrl());
            final JLabel pathLabel = new JLabel(path);
            pathLabel.setOpaque(false);
            pathLabel.setForeground(Color.RED);

            return new UnderlinedPathLabel(pathLabel);
        }
    }

    private JComponent createFolderDeleteComponent(final ContentFolder folder) {
        final String tooltipText;
        if (folder.getFile() != null && myContentEntry.getFile() != null) {
            if (folder instanceof SourceFolder) {
                tooltipText = ((SourceFolder)folder).isTestSource()
                        ? RBundle.message("module.paths.unmark.tests.tooltip")
                        : RBundle.message("module.paths.unmark.source.tooltip");
            } else if (folder instanceof ExcludeFolder) {
                tooltipText = RBundle.message("module.paths.include.excluded.tooltip");
            } else {
                tooltipText = null;
            }
        } else {
            tooltipText = RBundle.message("module.paths.remove.tooltip");
        }
        return new IconActionComponent(DELETE_FOLDER_ICON, DELETE_FOLDER_ROLLOVER_ICON, tooltipText, new Runnable() {
            public void run() {
                myCallBack.deleteContentFolder(myContentEntry, folder);
            }
        });
    }

    public boolean isExcluded(VirtualFile file) {
        return getExcludeFolder(file) != null;
    }

    public boolean isUnderExcludedDirectory(final VirtualFile file) {
        if (myContentEntry == null) {
            return false;
        }
        final ExcludeFolder[] excludeFolders = myContentEntry.getExcludeFolders();
        for (ExcludeFolder excludeFolder : excludeFolders) {
            final VirtualFile excludedDir = excludeFolder.getFile();
            if (excludedDir == null) {
                continue;
            }
            if (VfsUtil.isAncestor(excludedDir, file, true)) {
                return true;
            }
        }
        return false;
    }

    public ExcludeFolder getExcludeFolder(VirtualFile file) {
        if (myContentEntry == null) {
            return null;
        }
        final ExcludeFolder[] excludeFolders = myContentEntry.getExcludeFolders();
        for (final ExcludeFolder excludeFolder : excludeFolders) {
            final VirtualFile f = excludeFolder.getFile();
            if (f == null) {
                continue;
            }
            if (f.equals(file)) {
                return excludeFolder;
            }
        }
        return null;
    }

    private static String toRelativeDisplayPath(String url, String ancestorUrl) {
        if (!StringUtil.endsWithChar(ancestorUrl, '/')) {
            ancestorUrl = ancestorUrl + "/";
        }
        if (url.startsWith(ancestorUrl)) {
            return url.substring(ancestorUrl.length()).replace('/', File.separatorChar);
        }
        return toDisplayPath(url);
    }

    private static String toDisplayPath(final String url) {
        return VirtualFileManager.extractPath(url).replace('/', File.separatorChar);
    }


    private static class UnderlinedPathLabel extends ResizingWrapper {
        private static final float[] DASH = new float[]{0, 2, 0, 2};
        private static final Color DASH_LINE_COLOR = new Color(0xC9C9C9);

        public UnderlinedPathLabel(JLabel wrappedComponent) {
            super(wrappedComponent);
            FilePathClipper.install(wrappedComponent, this);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            final int startX = myWrappedComponent.getWidth();
            final int endX = getWidth();
            if (endX > startX) {
                final FontMetrics fontMetrics = myWrappedComponent.getFontMetrics(myWrappedComponent.getFont());
                final int y = fontMetrics.getMaxAscent();
                final Color savedColor = g.getColor();
                g.setColor(DASH_LINE_COLOR);
                drawDottedLine((Graphics2D)g, startX, y, endX, y);
                g.setColor(savedColor);
            }
        }

        private void drawDottedLine(Graphics2D g, int x1, int y1, int x2, int y2) {
            /*
            // TODO!!!
            final Color color = g.getColor();
            g.setColor(getBackground());
            g.setColor(color);
            for (int i = x1 / 2 * 2; i < x2; i += 2) {
              g.drawRect(i, y1, 0, 0);
            }
            */
            if (SystemInfo.isMac) {
                UIUtil.drawLine(g, x1, y1, x2, y2);
            } else {
                final Stroke saved = g.getStroke();
                g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, DASH, y1 % 2));

                UIUtil.drawLine(g, x1, y1, x2, y2);

                g.setStroke(saved);
            }
        }
    }

    public static class RSourceFolder implements SourceFolder {
        private final VirtualFile myFolder;
        private final ContentEntry myContentEntry;

        public RSourceFolder(final VirtualFile folder,
                             final ContentEntry contentEntry) {
            myFolder = folder;
            myContentEntry = contentEntry;
        }

        public boolean isTestSource() {
            return true;
        }

        public String getPackagePrefix() {
            return null;
        }

        public void setPackagePrefix(String packagePrefix) {
            // N/A/
        }

        @Nullable
        public VirtualFile getFile() {
            return myFolder;
        }

        @NotNull
        public ContentEntry getContentEntry() {
            return myContentEntry;
        }

        @NotNull
        public String getUrl() {
            return myFolder == null ? "" : myFolder.getUrl();
        }

        public boolean isSynthetic() {
            return false;
        }
    }
}
