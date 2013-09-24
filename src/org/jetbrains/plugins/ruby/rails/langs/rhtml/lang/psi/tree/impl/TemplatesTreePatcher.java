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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.impl;

import com.intellij.psi.impl.source.jsp.jspJava.OuterLanguageElement;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.CharTable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.TreePatcher;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 07.04.2007
 */
public class TemplatesTreePatcher implements TreePatcher {
    public TokenSet myLexemsToInsertBeforeParent;

    public TemplatesTreePatcher(final TokenSet lexemsToInsertBeforeParent) {
        myLexemsToInsertBeforeParent = lexemsToInsertBeforeParent;
    }

    public void insert(final CompositeElement parent,
                       final TreeElement anchorBefore,
                       final OuterLanguageElement toInsert) {

        TreeElement treeElement = anchorBefore;
        if(treeElement != null) {
            //Some kind of hack.
            if (myLexemsToInsertBeforeParent.contains(treeElement.getElementType())) {
                treeElement = treeElement.getTreeParent();
            }

            TreeUtil.insertBefore(treeElement, (TreeElement)toInsert);
        } else {
            TreeUtil.addChildren(parent, (TreeElement)toInsert);
        }
    }

    public LeafElement split(LeafElement leaf, int offset, final CharTable table) {
        return split(leaf, offset, offset, table);
    }

    public LeafElement split(final LeafElement leaf,
                             final int lOffset, final int rOffset,
                             final CharTable table) {
        final CharSequence chars = leaf.getInternedText();
        final LeafElement leftPart = Factory.createLeafElement(leaf.getElementType(), chars, 0, lOffset, table);
        final LeafElement rightPart = Factory.createLeafElement(leaf.getElementType(), chars, rOffset, chars.length(), table);
        TreeUtil.insertAfter(leaf, leftPart);
        TreeUtil.insertAfter(leftPart, rightPart);
        TreeUtil.remove(leaf);
        return leftPart;
    }
}
