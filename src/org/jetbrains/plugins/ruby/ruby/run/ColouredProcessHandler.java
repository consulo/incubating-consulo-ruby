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

package org.jetbrains.plugins.ruby.ruby.run;

import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.util.Key;
import com.intellij.util.text.StringTokenizer;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecSupportLoader;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 17, 2007
 */
public class ColouredProcessHandler extends OSProcessHandler {
    public static final char TEXT_ATTRS_PREFIX_CH = '\u001B';
    public static final String TEXT_ATTRS_PREFIX = Character.toString(TEXT_ATTRS_PREFIX_CH);

    private StringTokenizer mySt;
    private Key previousColor;

    public ColouredProcessHandler(Process process, String commandLine) {
        super(process, commandLine);
    }


    @Override
	public void notifyTextAvailable(String text, Key outputType) {
        if (outputType != ProcessOutputTypes.STDOUT
                || text.indexOf(TEXT_ATTRS_PREFIX_CH) == -1) {
            super.notifyTextAvailable(text, outputType);
            return;
        }

        if (mySt == null) {
            mySt = new StringTokenizer(text, TEXT_ATTRS_PREFIX);
        } else {
            mySt.reset(text);
        }

        final String firstToken = mySt.nextToken();
        if (text.startsWith(TEXT_ATTRS_PREFIX)) {
            previousColor = processColoredText(firstToken);
        } else {
            super.notifyTextAvailable(firstToken, previousColor != null ? previousColor : outputType);
        }
        while (mySt.hasMoreTokens()) {
            previousColor = processColoredText(mySt.nextToken());
        }
    }

    private Key processColoredText(final String token) {
        final String text;
        final Key type;
        if (token.startsWith(RSpecSupportLoader.DEFAULT_TEXT_ATTRS)) {
            text = token.substring(RSpecSupportLoader.DEFAULT_TEXT_ATTRS.length());
            type = ProcessOutputTypes.STDOUT;

        } else if (token.startsWith(RSpecSupportLoader.RED_TEXT_ATTRS)) {
            text = token.substring(RSpecSupportLoader.RED_TEXT_ATTRS.length());
            type = RSpecSupportLoader.RED_TEXT;

        } else if (token.startsWith(RSpecSupportLoader.GREEN_TEXT_ATTRS)) {
            text = token.substring(RSpecSupportLoader.GREEN_TEXT_ATTRS.length());
            type = RSpecSupportLoader.GREEN_TEXT;
        } else if (token.startsWith(RSpecSupportLoader.MAGENTA_TEXT_ATTRS)) {
            text = token.substring(RSpecSupportLoader.MAGENTA_TEXT_ATTRS.length());
            type = RSpecSupportLoader.MAGENTA_TEXT;
        } else if (token.startsWith(RSpecSupportLoader.YELLOW_TEXT_ATTRS)) {
            text = token.substring(RSpecSupportLoader.YELLOW_TEXT_ATTRS.length());
            type = RSpecSupportLoader.YELLOW_TEXT;
        } else if (token.startsWith(RSpecSupportLoader.BLUE_TEXT_ATTRS)) {
            text = token.substring(RSpecSupportLoader.BLUE_TEXT_ATTRS.length());
            type = RSpecSupportLoader.BLUE_TEXT;
        } else {
            text = token;
            type = ProcessOutputTypes.STDOUT;
        }
        //TODO
        /*
          Implement support for full list of text attributes

          Description
           0	Cancel all attributes except foreground/background color
           1	Bright (bold)
           2	Normal (not bold)
           4	Underline
           5	Blink
           7	Reverse video
           8	Concealed (don't display characters)
           30	Make foreground (the characters) black
           31	Make foreground red
           32	Make foreground green
           33	Make foreground yellow
           34	Make foreground blue
           35	Make foreground magenta
           36	Make foreground cyan
           37	Make foreground white

           40	Make background (around the characters) black
           41	Make background red
           42	Make background green
           43	Make background yellow
           44	Make background blue
           45	Make background magenta
           46	Make background cyan
           47	Make background white (you may need 0 instead, or in addition)

           see full doc at http://www.linux-mag.com/downloads/2003-09/power/escape_sequences.html
        */
        super.notifyTextAvailable(text, type);
        return type;
    }
}
