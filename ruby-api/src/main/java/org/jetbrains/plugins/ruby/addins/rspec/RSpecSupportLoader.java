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

package org.jetbrains.plugins.ruby.addins.rspec;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.editor.markup.TextAttributes;
import consulo.util.dataholder.Key;
import org.jetbrains.annotations.NonNls;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 17, 2007
 */
public class RSpecSupportLoader
{
	//red color
	public final static String RED_TEXT_ATTRS = "[31m";
	public static final Key RED_TEXT = new Key("RED_TEXT");
	private static final TextAttributes RED_TEXT_ATTRIBUTES = new TextAttributes();
	private static final MyConsoleViewContentType RSPEC_RED_TEXT_TYPE = new MyConsoleViewContentType("RSPEC_RED_TEXT_TYPE", RED_TEXT_ATTRIBUTES);

	//green
	public final static String GREEN_TEXT_ATTRS = "[32m";
	public static final Key GREEN_TEXT = new Key("GREEN_TEXT");
	private static final TextAttributes GREEN_TEXT_ATTRIBUTES = new TextAttributes();
	private static final MyConsoleViewContentType RSPEC_GREEN_TEXT_TYPE = new MyConsoleViewContentType("RSPEC_GREEN_TEXT_TYPE", GREEN_TEXT_ATTRIBUTES);

	//yellow
	public final static String YELLOW_TEXT_ATTRS = "[33m";
	public static final Key YELLOW_TEXT = new Key("YELLOW_TEXT");
	private static final TextAttributes YELLOW_TEXT_ATTRIBUTES = new TextAttributes();
	private static final MyConsoleViewContentType RSPEC_YELLOW_TEXT_TYPE = new MyConsoleViewContentType("RSPEC_YELLOW_TEXT_TYPE", YELLOW_TEXT_ATTRIBUTES);

	//blue
	public final static String BLUE_TEXT_ATTRS = "[34m";
	public static final Key BLUE_TEXT = new Key("BLUE_TEXT");
	private static final TextAttributes BLUE_TEXT_ATTRIBUTES = new TextAttributes();
	private static final MyConsoleViewContentType RSPEC_BLUE_TEXT_TYPE = new MyConsoleViewContentType("RSPEC_BLUE_TEXT_TYPE", BLUE_TEXT_ATTRIBUTES);

	//magenta
	public final static String MAGENTA_TEXT_ATTRS = "[35m";
	public static final Key MAGENTA_TEXT = new Key("MAGENTA_TEXT");
	private static final TextAttributes MAGENTA_TEXT_ATTRIBUTES = new TextAttributes();
	private static final MyConsoleViewContentType RSPEC_MAGENTA_TEXT_TYPE = new MyConsoleViewContentType("RSPEC_MAGENTA_TEXT_TYPE", MAGENTA_TEXT_ATTRIBUTES);

	public final static String DEFAULT_TEXT_ATTRS = "[0m";

	static
	{
		RED_TEXT_ATTRIBUTES.setForegroundColor(new Color(221, 33, 33));
		GREEN_TEXT_ATTRIBUTES.setForegroundColor(new Color(61, 164, 61));
		MAGENTA_TEXT_ATTRIBUTES.setForegroundColor(new Color(255, 0, 255));
		BLUE_TEXT_ATTRIBUTES.setForegroundColor(new Color(0, 0, 255));
		YELLOW_TEXT_ATTRIBUTES.setForegroundColor(new Color(255, 205, 0));
	}

	public static class MyConsoleViewContentType extends ConsoleViewContentType
	{
		public MyConsoleViewContentType(@NonNls final String name, final TextAttributes textAttributes)
		{
			super(name, textAttributes);
		}
	}

	public RSpecSupportLoader()
	{
		ConsoleViewContentType.registerNewConsoleViewType(RED_TEXT, RSPEC_RED_TEXT_TYPE);
		ConsoleViewContentType.registerNewConsoleViewType(GREEN_TEXT, RSPEC_GREEN_TEXT_TYPE);
		ConsoleViewContentType.registerNewConsoleViewType(MAGENTA_TEXT, RSPEC_MAGENTA_TEXT_TYPE);
		ConsoleViewContentType.registerNewConsoleViewType(BLUE_TEXT, RSPEC_BLUE_TEXT_TYPE);
		ConsoleViewContentType.registerNewConsoleViewType(YELLOW_TEXT, RSPEC_YELLOW_TEXT_TYPE);
	}
}
