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

package org.jetbrains.plugins.ruby.ruby.ri.ui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicRadioButtonUI;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 8, 2006
 */
public class CheckBoxList extends JList
{
	private static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	private static final int DEFAULT_CHECK_BOX_WIDTH = 20;

	public CheckBoxList(final ListModel dataModel, final CheckBoxListListener checkBoxListListener)
	{
		super(dataModel);
		setCellRenderer(new CellRenderer());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setBorder(BorderFactory.createEtchedBorder());
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(isEnabled())
				{
					int index = locationToIndex(e.getPoint());

					if(index != -1)
					{
						JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
						int iconArea;
						try
						{
							iconArea = ((BasicRadioButtonUI) checkbox.getUI()).getDefaultIcon().getIconWidth();
						}
						catch(ClassCastException c)
						{
							iconArea = DEFAULT_CHECK_BOX_WIDTH;
						}
						if(e.getX() < iconArea)
						{
							boolean value = !checkbox.isSelected();
							checkbox.setSelected(value);
							repaint();
							checkBoxListListener.checkBoxSelectionChanged(index, value);
						}
					}
				}
			}
		});
	}


	private class CellRenderer implements ListCellRenderer
	{
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			JCheckBox checkbox = (JCheckBox) value;
			checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
			checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
			checkbox.setEnabled(isEnabled());
			checkbox.setFont(getFont());
			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
			checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
			return checkbox;
		}
	}

}
