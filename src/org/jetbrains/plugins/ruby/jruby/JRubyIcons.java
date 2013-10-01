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

package org.jetbrains.plugins.ruby.jruby;

import javax.swing.Icon;

import org.jetbrains.annotations.NonNls;
import com.intellij.openapi.util.IconLoader;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 11, 2007
 */
public interface JRubyIcons
{
	/**
	 * Please ensure that DATA_PATH corresponds with real resources path
	 */
	@NonNls
	final String DATA_PATH = "/org/jetbrains/plugins/ruby/jruby/";

	final Icon JRUBY_ICON = IconLoader.findIcon(DATA_PATH + "jruby.png");
	final Icon JRUBY_SDK_ADD_ICON = JRUBY_ICON;
	final Icon JAVA_ICON = IconLoader.findIcon("/general/addJdk.png");

	Icon JRUBY_SDK_ICON_OPEN = IconLoader.findIcon("/org/jetbrains/plugins/ruby/jruby/jruby_sdk_open.png");
	Icon JRUBY_SDK_ICON_CLOSED = IconLoader.findIcon("/org/jetbrains/plugins/ruby/jruby/jruby_sdk_closed.png");
}
