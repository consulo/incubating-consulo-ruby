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

package org.jetbrains.plugins.ruby.rails.codeInsight.daemon;

import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.rails.actions.navigation.SwitchToAction;
import org.jetbrains.plugins.ruby.rails.actions.navigation.SwitchToController;
import org.jetbrains.plugins.ruby.rails.actions.navigation.SwitchToView;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.awt.RelativePoint;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 04.02.2007
 */
public class RailsLineMarkerNavigator
{
	private static final Logger LOG = Logger.getInstance(RailsLineMarkerNavigator.class.getName());

	public static void browse(@Nonnull final MouseEvent e, @Nonnull final RailsLineMarkerInfo info, @Nonnull final Module module)
	{
		final PsiElement element = info.getElementRef().get();
		if(element == null || !element.isValid())
		{
			return;
		}

		element.accept(new RubyElementVisitor()
		{
			@Override
			public void visitRMethod(final RMethod rMethod)
			{
				browseRMethodOrClass(info, module, rMethod, e);
			}

			@Override
			public void visitRClass(final RClass rClass)
			{
				browseRMethodOrClass(info, module, rClass, e);
			}

			@Override
			public void visitRFile(final RFile rFile)
			{
				visitFile(rFile);
			}

			@Override
			public void visitFile(final PsiFile file)
			{
				browsePsiFile(info, module, file);
			}
		});
	}

	private static void browseRMethodOrClass(final RailsLineMarkerInfo info, final Module module, final RContainer methodOrClass, final MouseEvent e)
	{
		if(info.type == RailsLineMarkerInfo.MarkerType.CONTROLLER_TO_VIEW)
		{
			SwitchToView.switchToView(module, methodOrClass, new RelativePoint(e));
		}
		else
		{
			LOG.assertTrue(false);
		}
	}

	private static void browsePsiFile(final RailsLineMarkerInfo info, final Module module, final PsiFile file)
	{
		switch(info.type)
		{
			case VIEW_TO_ACTION:
				SwitchToAction.switchToAction(module, file);
				break;
			case VIEW_TO_CONTROLLER:
				SwitchToController.switchToController(module, file);
				break;
			default:
				LOG.assertTrue(false);
		}
	}

	public static void openTargets(@Nonnull final Navigatable[] targets, @Nonnull final String title, @Nonnull final ListCellRenderer listRenderer, @Nonnull final RelativePoint relativePoint)
	{
		if(targets.length == 0)
		{
			return;
		}

		if(targets.length == 1)
		{
			targets[0].navigate(true);
			return;
		}

		final JList list = new JList(targets);
		list.setCellRenderer(listRenderer);
		new PopupChooserBuilder(list).setTitle(title).setMovable(true).setItemChoosenCallback(new Runnable()
		{
			@Override
			public void run()
			{
				int[] ids = list.getSelectedIndices();
				if(ids == null || ids.length == 0)
				{
					return;
				}
				Object[] selectedElements = list.getSelectedValues();
				for(Object element : selectedElements)
				{
					PsiElement selected = (PsiElement) element;
					LOG.assertTrue(selected.isValid());
					((Navigatable) selected).navigate(true);
				}
			}
		}).createPopup().show(relativePoint);
	}
}

