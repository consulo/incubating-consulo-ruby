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

import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ruby.api.icon.RubyApiIconGroup;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 * User: oleg, Roman.Chernyatchik
 * Date: 20.09.2006
 */
public interface RubyIcons
{
	final Image RUBY_ICON = RubyApiIconGroup.rubyRuby();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Nodes
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	final Image RUBY_CLASS_NODE = PlatformIconGroup.nodesClass();
	final Image RUBY_METHOD_NODE = PlatformIconGroup.nodesMethod();
	final Image RUBY_REQUIRE_NODE = PlatformIconGroup.nodesAspect();
	final Image RUBY_PARAMETER_NODE = PlatformIconGroup.nodesParameter();
	final Image RUBY_VARIABLE_NODE = PlatformIconGroup.nodesVariable();
	final Image RUBY_MODULE_NODE = RubyApiIconGroup.rubyNodesModule();
	final Image RUBY_CONSTANT_NODE = RubyApiIconGroup.rubyNodesConstant();
	final Image RUBY_NOT_DEFINED_NODE = RubyApiIconGroup.rubyNodesNot_defined();

	final Image RUBY_OBJECT_CLASS_NODE = RubyApiIconGroup.rubyNodesInclude();
	final Image RUBY_GLOBAL_VAR_NODE = RubyApiIconGroup.rubyNodesVariableDollar();
	final Image RUBY_FIELD_NODE = RubyApiIconGroup.rubyNodesVariableAt();

	final Image RUBY_INCLUDE_NODE = RubyApiIconGroup.rubyNodesInclude();
	final Image RUBY_ALIAS_NODE = RubyApiIconGroup.rubyNodesAlias();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Node attributes
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	final Image RUBY_ATTR_NODE = PlatformIconGroup.nodesAnnotationtype();
	final Image RUBY_ATTR_PUBLIC = PlatformIconGroup.nodesC_public();
	final Image RUBY_ATTR_PRIVATE = PlatformIconGroup.nodesC_private();
	final Image RUBY_ATTR_PROTECTED = PlatformIconGroup.nodesC_protected();
	final Image RUBY_ATTR_STATIC = PlatformIconGroup.nodesStaticMark();

	final Image RUBY_ATTR_READER = RubyApiIconGroup.rubyReader();
	final Image RUBY_ATTR_WRITER = RubyApiIconGroup.rubyWriter();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Run configurations
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	final Image RUBY_RUN_CONFIGURATION_FOLDER = RUBY_ICON;

	final Image RUBY_RUN_CONFIGURATION_SCRIPT = RubyApiIconGroup.rubyConfigurationRuby_script();
	final Image RTEST_RUN_CONFIGURATION = RubyApiIconGroup.rubyConfigurationRuby_test_unit();
	final Image RAILS_SERVER_RUN_CONFIGURATION = RubyApiIconGroup.rubyConfigurationRun_conf_server();


	final Image RUBY_RUNNER_SHOW_CMDLINE = PlatformIconGroup.actionsShowViewer();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Misc
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	final Image RUBY_COLOR_PAGE = RUBY_ICON;

	final Image RUBY_GUTTER_OVERRIDING = PlatformIconGroup.gutterOverridingMethod();
	final Image RUBY_GUTTER_IMPLEMENTING = PlatformIconGroup.gutterImplementingMethod();

	final Image RI_ICON = RubyApiIconGroup.rubyRi();
}

