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

package org.jetbrains.plugins.ruby.rails.actions.generators.actions.special;

import java.awt.FontMetrics;

import javax.annotation.Nonnull;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorOptions;
import org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorPanel;
import org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorsUtil;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.NamingConventions;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.DocumentAdapter;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 29.12.2006
 */
public class ActionPanel implements GeneratorPanel
{
	protected String myPath;

	private JCheckBox myPretendCheckBox;
	private JCheckBox myForceCheckBox;
	private JCheckBox mySkipCheckBox;
	private JCheckBox myBacktraceCheckBox;
	private JCheckBox mySVNCheckBox;
	private JTextField myActionName;
	private JPanel myLocationPanel;
	private JLabel myActionLocationValueLabel;
	private JLabel myLocationLabel;
	private JPanel myContentPanel;
	private GeneratorOptions myOptions;


	public ActionPanel(@Nullable final String path)
	{
		myPath = path;

		myActionName.getDocument().addDocumentListener(new DocumentAdapter()
		{
			final StringBuffer myBuff = new StringBuffer();

			@Override
			public void textChanged(DocumentEvent event)
			{
				myBuff.delete(0, myBuff.length());
				if(!TextUtil.isEmpty(myPath))
				{
					myBuff.append(ControllersConventions.getControllerClassName(myPath));
					myBuff.append(".");
				}
				myBuff.append(NamingConventions.toUnderscoreCase(myActionName.getText().trim()));
				final int width = myActionName.getWidth() - myLocationLabel.getSize().width;
				final FontMetrics fontMetrics = myLocationPanel.getFontMetrics(myLocationPanel.getFont());

				TextUtil.truncWithDots(myBuff, width, fontMetrics);
				myActionLocationValueLabel.setText(myBuff.toString());
			}

		});
		myActionName.setText(TextUtil.DOTS);
		myActionName.setText(TextUtil.EMPTY_STRING);
		myContentPanel.doLayout();
	}

	@Override
	public void initPanel(final GeneratorOptions options)
	{
		myOptions = options;
		GeneratorsUtil.initOptionsCheckBoxes(myPretendCheckBox, myForceCheckBox, mySkipCheckBox, myBacktraceCheckBox, mySVNCheckBox, myOptions);
	}


	@Override
	@Nonnull
	public JPanel getContent()
	{
		return myContentPanel;
	}

	@Override
	public String getMainArgument()
	{
		return myActionName.getText().trim();
	}

	@Override
	@Nonnull
	public String getGeneratorArgs()
	{
		final StringBuffer buff = new StringBuffer();
		buff.append(GeneratorsUtil.calcGeneralOptionsString(myBacktraceCheckBox, myForceCheckBox, myPretendCheckBox, mySkipCheckBox, mySVNCheckBox));
		if(!TextUtil.isEmpty(myPath))
		{
			buff.append(myPath);
		}

		buff.append(" ");
		buff.append(myActionName.getText().trim());

		return buff.toString();
	}

	@Override
	@Nonnull
	public JComponent getPreferredFocusedComponent()
	{
		return myActionName;
	}

	@Override
	public void saveSettings(final Project project)
	{
		GeneratorsUtil.saveSettings(myPretendCheckBox, myForceCheckBox, mySkipCheckBox, myBacktraceCheckBox, mySVNCheckBox, myOptions, project);
	}
}
