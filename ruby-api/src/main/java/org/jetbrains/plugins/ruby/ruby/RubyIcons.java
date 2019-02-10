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

package org.jetbrains.plugins.ruby.ruby;

import com.intellij.openapi.util.IconLoader;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 * User: oleg, Roman.Chernyatchik
 * Date: 20.09.2006
 */
public interface RubyIcons
{

	final Image RUBY_ICON = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/ruby.png");

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Nodes
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	final Image RUBY_CLASS_NODE = IconLoader.findIcon("/nodes/class.png");
	final Image RUBY_METHOD_NODE = IconLoader.findIcon("/nodes/method.png");
	final Image RUBY_REQUIRE_NODE = IconLoader.findIcon("/nodes/aspect.png");
	final Image RUBY_PARAMETER_NODE = IconLoader.findIcon("/nodes/parameter.png");
	final Image RUBY_VARIABLE_NODE = IconLoader.findIcon("/nodes/variable.png");
	final Image RUBY_MODULE_NODE = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/nodes/module.png");
	final Image RUBY_CONSTANT_NODE = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/nodes/constant.png");
	final Image RUBY_NOT_DEFINED_NODE = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/nodes/not_defined.png");

	final Image RUBY_OBJECT_CLASS_NODE = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/nodes/include.png");
	final Image RUBY_GLOBAL_VAR_NODE = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/nodes/variable$.png");
	final Image RUBY_FIELD_NODE = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/nodes/variable@.png");

	final Image RUBY_INCLUDE_NODE = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/nodes/include.png");
	final Image RUBY_ALIAS_NODE = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/nodes/alias.png");

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Node attributes
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	final Image RUBY_ATTR_NODE = IconLoader.findIcon("/nodes/annotationtype.png");
	final Image RUBY_ATTR_PUBLIC = IconLoader.findIcon("/nodes/c_public.png");
	final Image RUBY_ATTR_PRIVATE = IconLoader.findIcon("/nodes/c_private.png");
	final Image RUBY_ATTR_PROTECTED = IconLoader.findIcon("/nodes/c_protected.png");
	final Image RUBY_ATTR_STATIC = IconLoader.findIcon("/nodes/staticMark.png");

	final Image RUBY_ATTR_READER = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/reader.png");
	final Image RUBY_ATTR_WRITER = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/writer.png");

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Module
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	final Image RUBY_MODULE_BIG = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/rubymedium.png");

	final Image RUBY_MODULE_OPENED = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/ruby_module_opened.png");
	final Image RUBY_MODULE_CLOSED = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/ruby_module_closed.png");

	final Image RUBY_ADD_MODULE = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/add_ruby_modulewizard.png");

	final Image RUBY_MODULE_SETTINGS_LOADPATH = IconLoader.findIcon("/modules/classpath.png");
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Sdk
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	final Image RUBY_SDK = RUBY_MODULE_CLOSED;

	final Image RUBY_SDK_ADD_ICON = RUBY_ICON;


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Run configurations
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	final Image RUBY_RUN_CONFIGURATION_FOLDER = RUBY_ICON;

	final Image RUBY_RUN_CONFIGURATION_SCRIPT = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/configuration/ruby_script.png");
	final Image RTEST_RUN_CONFIGURATION = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/configuration/ruby_test_unit.png");
	final Image RAILS_SERVER_RUN_CONFIGURATION = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/configuration/run_conf_server.png");


	final Image RUBY_RUNNER_SHOW_CMDLINE = IconLoader.findIcon("/actions/showViewer.png");

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Misc
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	final Image RUBY_COLOR_PAGE = RUBY_ICON;

	final Image RUBY_GUTTER_OVERRIDING = IconLoader.findIcon("/gutter/overridingMethod.png");
	final Image RUBY_GUTTER_IMPLEMENTING = IconLoader.findIcon("/gutter/implementingMethod.png");

	final Image RI_ICON = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/ri.png");
}

