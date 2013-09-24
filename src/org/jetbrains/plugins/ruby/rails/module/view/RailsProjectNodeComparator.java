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
import org.jetbrains.plugins.ruby.rails.module.view.nodes.RailsAbstractNode;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 16.10.2006
 */
public class RailsProjectNodeComparator implements Comparator<NodeDescriptor> {
    protected static final int HIGHER = -1;
    protected static final int LOWER = 1;

    public int compare(final NodeDescriptor node1, final NodeDescriptor node2) {
        if (node1 instanceof RailsAbstractNode) {
            if (node2 instanceof RailsAbstractNode) {
                final RailsAbstractNode rFirstNode = (RailsAbstractNode)node1;
                final RailsAbstractNode rSecondNode = (RailsAbstractNode)node2;
                final NodeType secondType = rSecondNode.getType();

                switch (rFirstNode.getType()) {
                    case RMODULE:
                        if (secondType == NodeType.RMODULE) {
                            return compareNodes(rFirstNode, rSecondNode);
                        }
                        return HIGHER;
                    case SPECIAL_FOLDER:
                        if (secondType == NodeType.SPECIAL_FOLDER) {
                            return compareNodes(rFirstNode, rSecondNode);
                        }
                        return HIGHER;
                    case MIGRATION:
                        return HIGHER;
                    case FOLDER:
                        if (secondType == NodeType.FOLDER) {
                            return compareNodes(rFirstNode, rSecondNode);
                        }
                        if (secondType == NodeType.MIGRATION) {
                            return LOWER;
                        }
                        return HIGHER;
                    case CONTROLLER:
                        if (secondType == NodeType.FOLDER) {
                            return LOWER;
                        } else if (secondType == NodeType.CONTROLLER) {
                            return compareNodes(rFirstNode, rSecondNode);
                        }
                        return HIGHER;
                    case HELPER:
                        if (secondType == NodeType.HELPER) {
                            return compareNodes(rFirstNode, rSecondNode);
                        }
                        return HIGHER;
                    case CLASS:
                        if (secondType == NodeType.FOLDER
                            || secondType == NodeType.MIGRATION
                            || secondType == NodeType.HELPER
                            || secondType == NodeType.CONTROLLER) {
                            return LOWER;
                        } else if (secondType == NodeType.CLASS) {
                            return compareNodes(rFirstNode, rSecondNode);
                        }
                        return HIGHER;
                    case ACTION:
                        if (secondType == NodeType.FOLDER
                            || secondType == NodeType.CLASS
                            || secondType == NodeType.CONTROLLER
                            || secondType == NodeType.HELPER) {
                            return LOWER;
                        } else if (secondType == NodeType.ACTION) {
                            return compareNodes(rFirstNode, rSecondNode);
                        }
                        return HIGHER;
                    case METHOD:
                        if (secondType == NodeType.FOLDER
                            || secondType == NodeType.CLASS
                            || secondType == NodeType.CONTROLLER
                            || secondType == NodeType.HELPER
                            || secondType == NodeType.ACTION) {
                            return LOWER;
                        } else if (secondType == NodeType.METHOD) {
                            return compareNodes(rFirstNode, rSecondNode);
                        }
                        return HIGHER;

                    case LAYOUT:
                        if (secondType == NodeType.UNKNOWN) {
                            return HIGHER;
                        } else if (secondType == NodeType.LAYOUT) {
                            return compareNodes(rFirstNode, rSecondNode);
                        }
                        return LOWER;
                    case BD_SCHEMA:
                        return HIGHER;
                    case USER_FOLDERS_ROOT:
                        if (secondType == NodeType.SPECIAL_FOLDER) {
                            return LOWER;
                        } else if (secondType == NodeType.USER_FOLDERS_ROOT) {
                            return compareNodes(rFirstNode, rSecondNode);
                        }
                        return HIGHER;
                    case UNKNOWN:
                        if (secondType == NodeType.UNKNOWN) {
                            return compareNodes(rFirstNode, rSecondNode);
                        }
                        return LOWER;
                }
            }
        } else {
            if (node2 instanceof RailsAbstractNode) {
                return LOWER;
            }
        }
        return node1.getIndex() - node2.getIndex();
    }

    protected int compareNodes(final RailsAbstractNode node1, final RailsAbstractNode node2) {
        return node1.getName().compareTo(node2.getName());
    }

    public enum NodeType {
        ROOT,
        RMODULE,
        SPECIAL_FOLDER,
        FOLDER,
        HELPER,
        CONTROLLER,
        CLASS,
        ACTION,
        METHOD,
        LAYOUT,
        MIGRATION,
        BD_SCHEMA,
        USER_FOLDERS_ROOT,
        UNKNOWN
    }
}