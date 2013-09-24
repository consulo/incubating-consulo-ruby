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
 * @date: Sep 22, 2007
 */
public class RHTMLNodeInfo extends NodeInfo {
    private final ASTNode parentNode;
    private final ASTNode previousNode;
    private final ASTNode nextNode;

    @Nullable
    private final TextRange nextNodeRange;

    protected RHTMLNodeInfo(@NotNull final ASTNode parentNode,
                            @Nullable final ASTNode previousNode,   //TODO remove, really no usages
                            @Nullable final ASTNode nextNode,
                            @Nullable final TextRange nextNodeRange) {
        this.parentNode = parentNode;
        this.previousNode = previousNode;
        this.nextNode = nextNode;
        this.nextNodeRange = nextNodeRange;
    }

    public NodeType getType() {
        return NodeType.RHTML_NODE;
    }

    @NotNull
    public ASTNode getParentNode() {
        return parentNode;
    }

    @Nullable
    public ASTNode getPreviousNode() {
        return previousNode;
    }

    @Nullable
    public ASTNode getNextNode() {
        return nextNode;
    }

    @Nullable
    public TextRange getNextNodeTRange() {
        return nextNodeRange;
    }
}
