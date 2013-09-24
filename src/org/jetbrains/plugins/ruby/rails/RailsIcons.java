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

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * @author: oleg, Roman Chernyatchik
 * @date: 20.09.2006
 */
public interface RailsIcons {
    /**
     * Please ensure that DATA_PATH corresponds with real resources myPagePath
     */
    @NonNls
    final String DATA_ROOT = "/org/jetbrains/plugins/ruby/";
    @NonNls
    final String DATA_PATH = DATA_ROOT + "rails/";

    final Icon RAILS_SMALL =                IconLoader.findIcon(DATA_PATH + "rails_small.png");
    final Icon RAILS_LARGE =                IconLoader.findIcon(DATA_PATH + "rails_large.png");

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// Module
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //TODO Remove me after JRails UI will be completed!
    @Deprecated
    final Icon RAILS_ADD_MODULE =           IconLoader.findIcon(DATA_PATH + "add_rails_modulewizard.png");
    @Deprecated
    final Icon RAILS_MODULE_NODE =          RAILS_SMALL;
    @Deprecated
    final Icon RAILS_MODULE_OPENED =        IconLoader.findIcon(DATA_PATH + "rails_module_opened.png");
    @Deprecated
    final Icon RAILS_MODULE_CLOSED =        IconLoader.findIcon(DATA_PATH + "rails_module_closed.png");
    @Deprecated
    final Icon RAILS_MODULE_BIG =           RAILS_LARGE;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// Rails project view
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    final Icon RAILS_PROJECT_VIEW =         RAILS_MODULE_NODE;
    final Icon RAILS_APPlICON_NODES =       IconLoader.findIcon(DATA_PATH + "modelesNode.png");
//    final Icon RAILS_COMPONENTS_NODES =     IconLoader.findIcon("/modules/globalResources.png");
    final Icon RAILS_CONTROLERS_NODES =     IconLoader.findIcon("/nodes/keymapTools.png");
    final Icon RAILS_HELPER_NODE =          RubyIcons.RUBY_MODULE_NODE;
    final Icon RAILS_HELPERS_NODES =        IconLoader.findIcon("/nodes/unknownJdkClosed.png");
//    final Icon RAILS_LIBS_NODES =           IconLoader.findIcon("/objectBrowser/showLibraryContents.png");
    final Icon RAILS_MODEL_NODES =          IconLoader.findIcon("/javaee/webModuleGroup.png");
    final Icon RAILS_MODEL_NODE =           IconLoader.findIcon("/javaee/webModuleGroup.png");
    final Icon RAILS_FOLDER_OPENED =        IconLoader.findIcon("/nodes/folderOpen.png");
    final Icon RAILS_FOLDER_CLOSED =        IconLoader.findIcon("/nodes/folder.png");
    final Icon RAILS_PARTIALS_OPEN =        IconLoader.findIcon("/nodes/webFolderOpen.png");
    final Icon RAILS_PARTIALS_CLOSED =      IconLoader.findIcon("/nodes/webFolderClosed.png");
    final Icon RAILS_MIGRATIONS_OPEN =      IconLoader.findIcon(DATA_ROOT + "rails/migrationsOpen.png");
    final Icon RAILS_MIGRATIONS_CLOSED =    IconLoader.findIcon(DATA_ROOT + "rails/migrationsClosed.png");
    final Icon RAILS_SCHEMA_FILE =          IconLoader.findIcon(DATA_ROOT + "rails/schema.png");
//    final Icon RAILS_MODELS_NODES =      IconLoader.findIcon(DATA_PATH+"modelesNode.png");
//    final Icon RAILS_TESTS_NODES =      IconLoader.findIcon(DATA_PATH +"testsFolder.png");
//    final Icon RAILS_TESTS_NODES =          IconLoader.findIcon("/modules/testSourceClosed.png");
//    final Icon RAILS_TESTS_NODES_OPENED = IconLoader.findIcon("/modules/testSourceOpened.png");
//    final Icon RAILS_TESTS_NODES_CLOSED = IconLoader.findIcon("/modules/testSourceClosed.png");
//    final Icon RAILS_VIEW_NODES =           IconLoader.findIcon("/nodes/webFolderClosed.png");

    final Icon RAILS_ACTION_NODE =          RubyIcons.RUBY_METHOD_NODE;
    final Icon RAILS_CONTROLLER_NODE =      IconLoader.findIcon(DATA_ROOT + "ruby/nodes/controllernode.png");
//    final Icon RAILS_PACKAGE_MODULE_NODE =  IconLoader.findIcon("/nodes/packageClosed.png");

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// Rake
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    final Icon RAKE_TASK_ICON =             RAILS_MODULE_NODE;
    final Icon RAKE_GROUP_ICON =            RAILS_FOLDER_CLOSED;
    final Icon RAKE_TASKS_ROOT_ICON =       IconLoader.findIcon(DATA_ROOT + "rails/rake/rakeTasksRoot.png");

    final Icon RAKE_PARAMS_TRACE =          IconLoader.findIcon("/debugger/console.png");
//    final Icon RAKE_PARAMS_LIBDIR =         IconLoader.findIcon("/modules/libraries.png");
    final Icon RAKE_PARAMS_PREREQS =        IconLoader.findIcon("/modules/dependencies.png");
//    final Icon RAKE_PARAMS_USAGE =          IconLoader.findIcon("/actions/find.png");
    final Icon RAKE_PARAMS_DRY_RUN =        IconLoader.findIcon(DATA_ROOT + "rails/rake/dryRun.png");
    final Icon RAKE_PARAMS_NOSEARCH =       IconLoader.findIcon(DATA_ROOT + "rails/rake/nosearch.png");
//    final Icon RAKE_PARAMS_MODULE =         IconLoader.findIcon(DATA_ROOT + "rails/rake/module.png");
//    final Icon RAKE_PARAMS_AUTOIMPORT =     IconLoader.findIcon("/nodes/aspect.png");

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// Misc
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    final Icon RAILS_RUN_CONFIGURATION_FOLDER = RAILS_SMALL;

    final Icon RAILS_ACTION_TO_VIEW_MARKER =IconLoader.findIcon(DATA_ROOT + "rails/actions/navigation/gutter/actionToView.png");
    final Icon RAILS_VIEW_TO_ACTION_MARKER =IconLoader.findIcon(DATA_ROOT + "rails/actions/navigation/gutter/viewToAction.png");
    final Icon RAILS_VIEW_TO_CONTROLLER_MARKER = RAILS_VIEW_TO_ACTION_MARKER;

    final Icon RJS_ICON =                   IconLoader.findIcon(DATA_ROOT + "rails/rjs.png");
    final Icon RHTML_ICON =                 IconLoader.findIcon(DATA_ROOT + "rails/rhtml.png");
    final Icon RXTML_ICON =                 IconLoader.findIcon(DATA_ROOT + "rails/rxml.png");

    final Icon GENERATOR_ICON =             IconLoader.findIcon(DATA_ROOT + "rails/generators/generator.png");
    final Icon GENERATORS_ROOT_ICON =       IconLoader.findIcon(DATA_ROOT + "rails/generators/generatorsRoot.png");

    final Icon WARINIG_ICON =               IconLoader.findIcon("/nodes/warningIntroduction.png");
    final Icon YAML_ICON =                  IconLoader.findIcon("/nodes/DataTables.png");
}
