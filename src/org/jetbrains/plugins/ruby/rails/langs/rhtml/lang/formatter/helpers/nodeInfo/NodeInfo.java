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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.nodeInfo;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 20, 2007
 */
public abstract class NodeInfo {
    public abstract NodeType getType();

    public abstract TextRange getNextNodeTRange();

    public static TemplateNodeInfo createTemplateInfo(@NotNull final ASTNode templateParentNode,
                                                      @NotNull final ASTNode templateDateNode,
                                                      @Nullable final ASTNode nextNode,
                                                      @Nullable final TextRange nextNodeTRange) {
        return new TemplateNodeInfo(templateParentNode, templateDateNode, nextNodeTRange, nextNode);
    }

    public static RHTMLNodeInfo createRHTMLInfo(@NotNull final ASTNode parentNode,
                                                @Nullable final ASTNode previousNode,
                                                @Nullable final ASTNode nextNode) {
        return new RHTMLNodeInfo(parentNode, previousNode, nextNode, nextNode == null ? null : nextNode.getTextRange());
    }

    @Nullable
    public static ASTNode getNextRHTMLNodeByInfo(@NotNull final NodeInfo childNodeInfo) {
        ASTNode childNode;
        switch (childNodeInfo.getType()) {
            case RHTML_NODE:
                childNode = ((RHTMLNodeInfo) childNodeInfo).getNextNode();
                break;
            case TEMPLATE_NODE:
                childNode = ((TemplateNodeInfo) childNodeInfo).getTemplateDataNode();
                break;
            default:
                childNode = null;
                break;
        }
        return childNode;
    }

    @Nullable
    public static ASTNode getNextNodeByInfo(@NotNull final NodeInfo childNodeInfo) {
        ASTNode childNode;
        switch (childNodeInfo.getType()) {
            case RHTML_NODE:
                childNode = ((RHTMLNodeInfo) childNodeInfo).getNextNode();
                break;
            case TEMPLATE_NODE:
                childNode = ((TemplateNodeInfo) childNodeInfo).getNextTemplateNode();
                break;
            default:
                childNode = null;
                break;
        }
        return childNode;
    }

    public boolean isTemplate() {
        return getType() == NodeType.TEMPLATE_NODE;
    }
}

