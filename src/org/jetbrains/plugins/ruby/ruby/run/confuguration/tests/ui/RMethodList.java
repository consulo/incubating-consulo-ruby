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

package org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.ui;

import com.intellij.ide.structureView.impl.StructureNodeRenderer;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Iconable;
import com.intellij.ui.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.RClassPresentationUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.RContainerPresentationUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.RMethodPresentationUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.RPresentationConstants;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 06.08.2007
 */
public class RMethodList extends JPanel {
  private final RVirtualClass myRVClass;

    private final SortedListModel<RVirtualMethod> myListModel;

    private final JList myList;

    public RMethodList(@NotNull final RVirtualClass rVClass,
                       @NotNull final Condition<RVirtualMethod> filter,
                       @NotNull final RMethodProvider methodProvider) {
        super(new BorderLayout());

        myRVClass = rVClass;

        myListModel = new SortedListModel<RVirtualMethod>(new RMethodComparator());
        myList = new JList(myListModel);

        createList(methodProvider.getAllMethods(), filter);

        add(ScrollPaneFactory.createScrollPane(myList));

        myList.setCellRenderer(new MyMethodsListCellRenderer());
        myList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListScrollingUtil.ensureSelectionExists(myList);
    }

    private void createList(@NotNull final RVirtualMethod[] allMethods,
                            @NotNull final Condition<RVirtualMethod> filter) {
        for (RVirtualMethod method : allMethods) {
            if (filter.value(method)) myListModel.add(method);
        }
    }

    public RVirtualMethod getSelected() {
        return (RVirtualMethod)myList.getSelectedValue();
    }

    public static RVirtualMethod showDialog(final RVirtualClass rClass,
                                            final Condition<RVirtualMethod> filter,
                                            @NotNull final RMethodProvider methodProvider,
                                            final JComponent parent) {
    final RMethodList RMethodList = new RMethodList(rClass, filter, methodProvider);
    final DialogBuilder builder = new DialogBuilder(parent);
    builder.setCenterPanel(RMethodList);
    builder.setPreferedFocusComponent(RMethodList.myList);
    builder.setTitle(RBundle.message("choose.test.method.dialog.title"));
    return builder.show() == DialogWrapper.OK_EXIT_CODE ? RMethodList.getSelected() : null;
  }

    private static class RMethodComparator implements Comparator<RVirtualMethod> {
        @Override
		public int compare(final RVirtualMethod rMethod1, final RVirtualMethod rMethod2) {
            return rMethod1.getName().compareToIgnoreCase(rMethod2.getName());
        }
    }

    private class MyMethodsListCellRenderer extends ColoredListCellRenderer {
        @Override
		protected void customizeCellRenderer(final JList list, final Object value,
                                             final int index, final boolean selected,
                                             final boolean hasFocus) {

            final RVirtualMethod rVMethod = (RVirtualMethod)value;

            final SimpleTextAttributes methodAttrs =
                    StructureNodeRenderer.applyDeprecation(rVMethod, SimpleTextAttributes.REGULAR_ATTRIBUTES);
            append(RMethodPresentationUtil.formatName(rVMethod, RPresentationConstants.SHOW_NAME),
                    methodAttrs);

            final RVirtualClass containingClass = RVirtualPsiUtil.getContainingRVClass(rVMethod);
            final SimpleTextAttributes locationAttrs = SimpleTextAttributes.GRAY_ATTRIBUTES;
            //assert containingClass != null;
            if (containingClass != null) {
                if (!myRVClass.equals(containingClass)) {
                    append(" (" + RClassPresentationUtil.formatName(containingClass, RPresentationConstants.SHOW_NAME) + ")",
                            locationAttrs);
                }
            } else {
                final RVirtualContainer parentCont = rVMethod.getVirtualParentContainer();
                assert parentCont != null; //belongs to class, module of file
                append(" (" + RContainerPresentationUtil.formatName(parentCont, RPresentationConstants.SHOW_NAME) + ")",
                        locationAttrs);
            }

            setIcon(RMethodPresentationUtil.getIcon(rVMethod, Iconable.ICON_FLAG_VISIBILITY));
        }
    }

    public interface RMethodProvider {
        public RVirtualMethod[] getAllMethods();
    }
}
