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

package org.jetbrains.plugins.ruby.rails.codeInsight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 30, 2007
 */
public class RCodeInsightUtil {
    private static final Logger LOG = Logger.getInstance(RCodeInsightUtil.class.getName());

    @NotNull
    public static List<PsiElement> getElementsStartsInRange(@NotNull final PsiElement root,
                                                            final int startOffset,
                                                            final int endOffset,
                                                            boolean includeAllParents) {
        return getElementsStartsInRange(root, startOffset, endOffset, includeAllParents, null);
    }

    @NotNull
    public static List<PsiElement> getElementsStartsInRange(@NotNull final PsiElement root,
                                                            final int startOffset,
                                                            final int endOffset,
                                                            boolean includeAllParents,
                                                            final RubyElementVisitor elVisitor) {

        final PsiElement commonParent = findCommonParent(root, startOffset, endOffset);
        if (commonParent == null) {
            return Collections.emptyList();
        }
        final List<PsiElement> list = new ArrayList<PsiElement>();

        final int currentOffset = commonParent.getTextRange().getStartOffset();

        final PsiElementVisitor visitor = elVisitor == null
                ? new ElementsInRangeElementVisitor(currentOffset, endOffset, startOffset, list)
                : elVisitor;

        commonParent.accept(visitor);

        PsiElement parent = commonParent;
        while (parent != null && parent != root) {
            list.add(parent);
            parent = includeAllParents ? parent.getParent() : null;
        }
        list.add(root);

        return Collections.unmodifiableList(list);
    }

    //TODO refactor with IDEA CodeInsightUtil
    @Nullable
    private static PsiElement findCommonParent(@NotNull final PsiElement root,
                                               final int startOffset, final int endOffset) {
        if (startOffset == endOffset) {
            return null;
        }

        final PsiElement left = findElementAtInRoot(root, startOffset);
        final PsiElement right = findElementAtInRoot(root, endOffset - 1);
        if (left == null || right == null) {
            return null;
        }

        PsiElement commonParent = PsiTreeUtil.findCommonParent(left, right);

        LOG.assertTrue(commonParent != null);
        LOG.assertTrue(commonParent.getTextRange() != null);

        while (commonParent.getParent() != null
               && commonParent.getTextRange().equals(commonParent.getParent().getTextRange())) {
            commonParent = commonParent.getParent();
        }
        return commonParent;
    }

    //TODO refactor with IDEA CodeInsightUtil
    @Nullable
    private static PsiElement findElementAtInRoot(@NotNull final PsiElement root,
                                                  final int offset) {
        if (root instanceof PsiFile) {
            final PsiFile file = (PsiFile) root;

            /*final LanguageDialect dialect = file.getLanguageDialect();
            if (dialect != null) {
                final PsiElement element = file.getViewProvider().findElementAt(offset, dialect);
                if (element != null) {
                    return element;
                }
            }    */

            return file.getViewProvider().findElementAt(offset, root.getLanguage());
        }

        return root.findElementAt(offset);
    }

    public static class ElementsInRangeElementVisitor extends RubyElementVisitor {
        private final int myEndOffset;
        private final int myStartOffset;
        private final List<PsiElement> myList;

        private int myOffset;

        public ElementsInRangeElementVisitor(final int currentOffset,
                                             final int endOffset, final int startOffset,
                                             final List<PsiElement> list) {
            myEndOffset = endOffset;
            myStartOffset = startOffset;
            myList = list;
            myOffset = currentOffset;
        }

        @Override
		public void visitReferenceExpression(PsiReferenceExpression expression) {
            visitElement(expression);
        }

        @Override
		public void visitElement(PsiElement element) {
            processElement(element);
        }

        protected void processElement(PsiElement element) {
            PsiElement child = element.getFirstChild();
            if (child != null) {
                // composite element
                while (child != null) {
                    if (myOffset > myEndOffset) break;
                    int start = myOffset;
                    child.accept(this);
                    //element start
                    if (myStartOffset <= start) {
                        myList.add(child);
                    }
                    child = child.getNextSibling();
                }
            } else {
                // leaf element
                myOffset += element.getTextLength();
            }
        }
    }
}
