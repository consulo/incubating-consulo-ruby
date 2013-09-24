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

package org.jetbrains.plugins.ruby.rails.actions.generators;

import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorFontType;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.Output;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 05.12.2006
 */
public class GeneratorHelpPanel {
    private JPanel myContentPanel;
    private JTextPane myTextPane;
    private JScrollPane myScrollPane;

    public GeneratorHelpPanel(final Output output) {
        final Document doc = myTextPane.getDocument();
        try {
            doc.remove(0, doc.getLength());
            SimpleAttributeSet attrs = new SimpleAttributeSet();

            myTextPane.setFont(EditorColorsManager.getInstance().getGlobalScheme().getFont(EditorFontType.PLAIN));
            doc.insertString(0, output.getStdout() + "\n", attrs);
            if (!TextUtil.isEmpty(output.getStderr())) {
                StyleConstants.setForeground(attrs, Color.RED);
                doc.insertString(doc.getLength(), output.getStderr(), attrs);
            }
        } catch (BadLocationException e) {
            // Shouldn't be thrown
        }

        myScrollPane.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
                myScrollPane.getViewport().scrollRectToVisible(new Rectangle(0, -myScrollPane.getHeight(), 0, 0));
            }

            public void componentMoved(ComponentEvent e) {
                //Do nothing
            }

            public void componentShown(ComponentEvent e) {
                //Do nothing
            }

            public void componentHidden(ComponentEvent e) {
                //Do nothing
            }
        });
    }

    public JPanel getContent() {
        return myContentPanel;
    }
}
