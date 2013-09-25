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

import com.intellij.ui.treeStructure.SimpleNode;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 15.10.2006
 */
public abstract class RailsNodeVisitorAdapter implements RailsNodeVisitor {
    @Override
	public boolean accept(final SimpleNode simpleNode) {
        return true;
    }

    @Override
	public void visitClassNode() {
        // Do nothing
    }

    @Override
	public void visitControllerNode() {
        // Do nothing
    }

    @Override
	public void visitModelNode() {
        // Do nothing
    }


    @Override
	public void visitTestNode() {
        // Do nothing
    }

    @Override
	public void visitUserNode(final boolean isUnderTestsRoot) {
        // Do nothing
    }

    @Override
	public void visitSharedPartialsNode() {
        // Do nothing
    }
}
