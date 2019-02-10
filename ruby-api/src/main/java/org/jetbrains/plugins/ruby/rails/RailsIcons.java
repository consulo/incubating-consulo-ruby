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

package org.jetbrains.plugins.ruby.rails;

import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import com.intellij.openapi.util.IconLoader;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: 20.09.2006
 */
public interface RailsIcons
{
	final Image RAILS_SMALL = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/rails_small.png");

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Module
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//TODO Remove me after JRails UI will be completed!
	@Deprecated
	final Image RAILS_ADD_MODULE = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/add_rails_modulewizard.png");
	@Deprecated
	final Image RAILS_MODULE_NODE = RAILS_SMALL;
	@Deprecated
	final Image RAILS_MODULE_OPENED = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/rails_module_opened.png");
	@Deprecated
	final Image RAILS_MODULE_CLOSED = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/rails_module_closed.png");

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Rails project view
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	final Image RAILS_PROJECT_VIEW = RAILS_MODULE_NODE;
	final Image RAILS_APPlICON_NODES = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/modelesNode.png");
	//    final Icon RAILS_COMPONENTS_NODES =     IconLoader.findIcon("/modules/globalResources.png");
	final Image RAILS_CONTROLERS_NODES = IconLoader.findIcon("/nodes/keymapTools.png");
	final Image RAILS_HELPER_NODE = RubyIcons.RUBY_MODULE_NODE;
	final Image RAILS_HELPERS_NODES = IconLoader.findIcon("/nodes/unknownJdkClosed.png");
	//    final Icon RAILS_LIBS_NODES =           IconLoader.findIcon("/objectBrowser/showLibraryContents.png");
	final Image RAILS_MODEL_NODES = IconLoader.findIcon("/javaee/webModuleGroup.png");
	final Image RAILS_MODEL_NODE = IconLoader.findIcon("/javaee/webModuleGroup.png");
	final Image RAILS_FOLDER_OPENED = IconLoader.findIcon("/nodes/folderOpen.png");
	final Image RAILS_FOLDER_CLOSED = IconLoader.findIcon("/nodes/folder.png");
	final Image RAILS_PARTIALS_OPEN = IconLoader.findIcon("/nodes/webFolderOpen.png");
	final Image RAILS_PARTIALS_CLOSED = IconLoader.findIcon("/nodes/webFolderClosed.png");
	final Image RAILS_MIGRATIONS_OPEN = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/migrationsOpen.png");
	final Image RAILS_MIGRATIONS_CLOSED = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/migrationsClosed.png");
	final Image RAILS_SCHEMA_FILE = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/schema.png");
	//    final Icon RAILS_MODELS_NODES =      IconLoader.findIcon(DATA_PATH+"modelesNode.png");
	//    final Icon RAILS_TESTS_NODES =      IconLoader.findIcon(DATA_PATH +"testsFolder.png");
	//    final Icon RAILS_TESTS_NODES =          IconLoader.findIcon("/modules/testSourceClosed.png");
	//    final Icon RAILS_TESTS_NODES_OPENED = IconLoader.findIcon("/modules/testSourceOpened.png");
	//    final Icon RAILS_TESTS_NODES_CLOSED = IconLoader.findIcon("/modules/testSourceClosed.png");
	//    final Icon RAILS_VIEW_NODES =           IconLoader.findIcon("/nodes/webFolderClosed.png");

	final Image RAILS_ACTION_NODE = RubyIcons.RUBY_METHOD_NODE;
	final Image RAILS_CONTROLLER_NODE = IconLoader.findIcon("/org/jetbrains/plugins/ruby/ruby/nodes/controllernode.png");
	//    final Icon RAILS_PACKAGE_MODULE_NODE =  IconLoader.findIcon("/nodes/packageClosed.png");

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Rake
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	final Image RAKE_TASK_ICON = RAILS_MODULE_NODE;
	final Image RAKE_GROUP_ICON = RAILS_FOLDER_CLOSED;
	final Image RAKE_TASKS_ROOT_ICON = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/rake/rakeTasksRoot.png");

	final Image RAKE_PARAMS_TRACE = IconLoader.findIcon("/debugger/console.png");
	//    final Icon RAKE_PARAMS_LIBDIR =         IconLoader.findIcon("/modules/libraries.png");
	final Image RAKE_PARAMS_PREREQS = IconLoader.findIcon("/modules/dependencies.png");
	//    final Icon RAKE_PARAMS_USAGE =          IconLoader.findIcon("/actions/find.png");
	final Image RAKE_PARAMS_DRY_RUN = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/rake/dryRun.png");
	final Image RAKE_PARAMS_NOSEARCH = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/rake/nosearch.png");
	//    final Icon RAKE_PARAMS_MODULE =         IconLoader.findIcon(DATA_ROOT + "rails/rake/module.png");
	//    final Icon RAKE_PARAMS_AUTOIMPORT =     IconLoader.findIcon("/nodes/aspect.png");

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Misc
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	final Image RAILS_RUN_CONFIGURATION_FOLDER = RAILS_SMALL;

	final Image RAILS_ACTION_TO_VIEW_MARKER = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/actions/navigation/gutter/actionToView.png");
	final Image RAILS_VIEW_TO_ACTION_MARKER = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/actions/navigation/gutter/viewToAction.png");
	final Image RAILS_VIEW_TO_CONTROLLER_MARKER = RAILS_VIEW_TO_ACTION_MARKER;

	final Image RJS_ICON = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/rjs.png");
	final Image RHTML_ICON = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/rhtml.png");
	final Image RXTML_ICON = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/rxml.png");

	final Image GENERATOR_ICON = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/generators/generator.png");
	final Image GENERATORS_ROOT_ICON = IconLoader.findIcon("/org/jetbrains/plugins/ruby/rails/generators/generatorsRoot.png");

	final Image WARINIG_ICON = IconLoader.findIcon("/nodes/warningIntroduction.png");
}
