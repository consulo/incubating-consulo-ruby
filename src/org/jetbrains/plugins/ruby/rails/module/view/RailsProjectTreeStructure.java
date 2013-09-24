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

package org.jetbrains.plugins.ruby.rails.module.view;

import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTreeStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.RailsProjectRootNode;

/**
 * Created by IntelliJ IDEA.
 * @author: Roman Chernyatchik
 * @date: 27.09.2006
 */
public class RailsProjectTreeStructure extends SimpleTreeStructure {
    private final RailsProjectRootNode myRootNode;
    private final String EMPTY_DESCRIPTOR = "EMPTY_DESCRIPTOR";

    protected RailsProjectTreeStructure(final RailsProjectRootNode rootNode) {
        myRootNode = rootNode;
    }

    public RailsProjectRootNode getRootElement() {
        return myRootNode;
    }

    public boolean hasSomethingToCommit() {
        return false;
    }

    public Object getParentElement(final Object element) {
        try {
            return ((SimpleNode)element).getParent();
        } catch (ClassCastException e) {
            return null;
        }
    }

    /*
      This method is fix for preventing exception: 

      @NotNull method com/intellij/ui/treeStructure/SimpleTreeStructure.createDescriptor must not
      return null
      java.lang.IllegalStateException: @NotNull method com/intellij/ui/treeStructure/SimpleTreeStructure.createDescriptor
                                       must not return null
     */
    @NotNull
    public NodeDescriptor createDescriptor(Object element, NodeDescriptor parentDescriptor) {
        if (element == null || !(element instanceof NodeDescriptor)) {
            return new SimpleNode() {
                public SimpleNode[] getChildren() {
                    return new SimpleNode[0];
                }
                public Object[] getEqualityObjects() {
                    return new Object[]{EMPTY_DESCRIPTOR};
                }
            };
        }
       return (NodeDescriptor) element;
    }
}
