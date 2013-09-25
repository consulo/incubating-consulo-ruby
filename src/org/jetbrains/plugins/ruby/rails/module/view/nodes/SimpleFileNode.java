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

package org.jetbrains.plugins.ruby.rails.module.view.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecIcons;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeId;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeIdUtil;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 29.09.2006
 */
public class SimpleFileNode extends RailsNode {
    private final VirtualFile myFile;

    /**
     * Creates Node for representing file. Default icon for file's type will be used.
     * @param module Rails module
     * @param file File
     */
    public SimpleFileNode(final Module module, final VirtualFile file) {
        this(module, file, null);
    }

    /**
     * Creates Node for representing file. If <code>icon</code> is null
     * default icon for file's type will be used.
     * @param module Rails module
     * @param icon Icon
     * @param file File
     */
    public SimpleFileNode(final Module module, final VirtualFile file, final Icon icon) {
        super(module);
        myFile = file;
        init(generateNodeId(file.getUrl()), initPresentationData(file, icon));
    }

    @NotNull
    public static NodeId generateNodeId(final String fileUrl) {
        return NodeIdUtil.createForFile(fileUrl);
    }

    private static PresentationData initPresentationData(final VirtualFile file, final Icon icon) {
        final Icon fileIcon = getFileIcon(file, icon);
        return new PresentationData(file.getName(), file.getName(),
                                    fileIcon, fileIcon,
                                    null);

    }

    private static Icon getFileIcon(final VirtualFile file, final Icon icon) {
        if (icon != null) {
            return icon;
        }
        if (RSpecUtil.isFileWithRSpecTestFileName(file)) {
            if (RSpecUtil.isRSpecTestFile(file)) {
                return RSpecIcons.TEST_SCRIPT_ICON;
            }
        }
        return FileTypeManager.getInstance().getFileTypeByFile(file).getIcon();
    }

    @Override
	@NotNull
    public RailsProjectNodeComparator.NodeType getType() {
        return RailsProjectNodeComparator.NodeType.UNKNOWN;
    }

    @Override
	public VirtualFile getVirtualFile() {
        return myFile;
    }
}
