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

package org.jetbrains.plugins.ruby.rails.actions.rake;

import com.intellij.execution.filters.Filter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.gems.GemsRunner;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTask;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTaskSerializableImpl;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfiguration;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfigurationImpl;
import org.jetbrains.plugins.ruby.ruby.actions.DataContextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.Output;
import org.jetbrains.plugins.ruby.ruby.run.Runner;
import org.jetbrains.plugins.ruby.ruby.run.filters.RFileLinksFilter;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.support.utils.IdeaInternalUtil;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 22.08.2006
 */
public class RakeUtil {
    // Rake output format
    /*
        (in C:/Documents and Settings/oleg/IdeaProjects/untitled/Unnamed)
        rake db:fixtures:load          # Load fixtures into the current environment's database.  Load specific fixtures using FIXTURES=x,y
        rake db:migrate                # Migrate the database through scripts in db/migrate. Target specific version with VERSION=x
        rake db:schema:dump            # Create a db/schema.rb file that can be portably used against any DB supported by AR
    */
    @NonNls
    public static final String LIB = "lib";
    @NonNls
    public static final String TASKS = "tasks";
    @NonNls
    public static final String RAKE_FILE = "Rakefile";

    @NonNls
    public static final String RAKE = "rake";
    @NonNls
    private static final String RAKE_COMMAND = RAKE;
    @NonNls
    private static final String RAKE_COMMENT_DELIMITER = "#";
    @NonNls
    private static final String ALL_RAKE_TASKS_FLAG = "--tasks";

    private final static Logger LOG = Logger.getInstance(RakeUtil.class.getName());


    /**
     * @param out Output by rake
     * @return List of RakeCommands
     */
    @NotNull
    public static List<RakeCommand> getCommands(@Nullable final Output out) {
        ArrayList<RakeCommand> comamnds = new ArrayList<RakeCommand>();
        if (out == null) {
            return comamnds;
        }
        if (!TextUtil.isEmpty(out.getStderr())) {
            LOG.warn(RBundle.message("execution.ruby.script.get.available.raketasks",
                    out.getStderr()));
        }
        String output[] = TextUtil.splitByLines(out.getStdout());
        for (int i = 1; i < output.length; i++) {
            String line = output[i];
            int pos = line.lastIndexOf(RAKE_COMMENT_DELIMITER);
            if (pos != -1) {
                String descr = line.substring(pos + 1).trim();
                String command = line.substring(0, pos).replace(RAKE_COMMAND, "").trim();
                comamnds.add(new RakeCommand(command, descr));
            }
        }
        return comamnds;
    }

    /**
     * Generates list of RakeCommands
     *
     * @param sdk         Ruby SDK
     * @param project     Project
     * @param contentRoot Location, where rake commands are avaible
     * @param errorTitle  Title for Message Tab
     * @return List of Rake Commamd
     */

    @Nullable
    public static Output getRakeGemAvailableTasksOutput(final Sdk sdk, final Project project,
                                                        final String errorTitle,
                                                        final String contentRoot) {
        final Runner.ExecutionMode mode = new Runner.SameThreadMode();

        return GemsRunner.runGemsExecutableScript(sdk, project, RAKE, contentRoot, mode,
                                                  true, errorTitle, ALL_RAKE_TASKS_FLAG);
    }

    public static RakeTask findTaksByFullCmd(@NotNull final RakeTask rakeTask,
                                             @NotNull final String fullCmd) {
        final String[] ids = fullCmd.split(RakeTask.RAKE_COMMAND_DELIMITER);
        RakeTask curr = rakeTask;
        for (int i = 0; i < ids.length; i++) {
            curr = RakeUtil.findSubTaskById(ids[i], i != ids.length - 1, curr);
            if (curr == null) {
                return null;
            }
        }
        return curr;
    }

    @Nullable
    /**
     * Find Rake Task by Id, i.e. "db" or "app"
     * @param id Task id
     * @return RakeTask object
     */
    public static RakeTask findSubTaskById(final String id, final boolean isGroup,
                                           final RakeTask parentTask) {
        for (RakeTask task : parentTask.getSubTasks()) {
            if (task.getId().equals(id) && task.isGroup() == isGroup) {
                return task;
            }
        }
        return null;
    }

    /**
     * Loads rake tasks tree form cache. If file with settings for rake tasks
     * doesn't exist method generates rake tasks tree and saves it in file system.
     *
     * @param forceRegenerate if true rake tasks list must be regenerated.
     * @param project Projec
     * @param sdk SDK with rails support
     * @param moduleName Facet's module name
     * @param railsFacetConfiguration facet Configuration
     */
    public static void loadRakeTasksTree(final boolean forceRegenerate,
                                         @Nullable final Project project,
                                         @Nullable final Sdk sdk,
                                         @NotNull final String moduleName,
                                         @NotNull final BaseRailsFacetConfiguration railsFacetConfiguration) {
        final String title = RBundle.message("module.rails.create.rake.tasks.title");
        final String railsApplicHomeDirPath = railsFacetConfiguration.getRailsApplicationRootPath();

        final Task task = new Task.Backgroundable(project, title, true) {
            @Override
			public void run(final ProgressIndicator indicator) {

                final RakeTasksExternalizer rakeTasksExt = new RakeTasksExternalizer();
                RakeTask rakeTask = rakeTasksExt.loadRakeTasksTree(railsApplicHomeDirPath);
                if (rakeTask == null || forceRegenerate) {
                    if (!RubySdkUtil.isSDKValid(sdk)) {
                        ((BaseRailsFacetConfigurationImpl)railsFacetConfiguration).setRakeTasks(null);
                        return;
                    }
                    rakeTask = getRakeTasksByOutput(getRakeCommands(project, sdk, moduleName, railsApplicHomeDirPath));
                    rakeTasksExt.saveRakeTasksTree(rakeTask, railsApplicHomeDirPath);
                }
                ((BaseRailsFacetConfigurationImpl)railsFacetConfiguration).setRakeTasks(rakeTask);
            }

            @Override
			public boolean shouldStartInBackground() {
                return true;
            }
        };
        IdeaInternalUtil.runInEventDispatchThread(new Runnable() {
            @Override
			public void run() {
                // Must be executed in EDT
                ProgressManager.getInstance().run(task);
            }
        }, ModalityState.defaultModalityState());
    }

    /**
     * Saves all opened documents and runs rake task
     * @param dataContext DataContext
     * @param task RakeTask
     */
    public static void runRakeTask(@NotNull final DataContext dataContext,
                                   @NotNull final RakeTask task) {
        //Save all opened documents
        FileDocumentManager.getInstance().saveAllDocuments();

        final Module module = DataContextUtil.getModule(dataContext);
        if (module == null || !RailsFacetUtil.hasRailsSupport(module)) {
            return;
        }
        final VirtualFile moduleRoot = RailsFacetUtil.getRailsAppHomeDir(module);
        if (moduleRoot == null) {
            final String msg = RBundle.message("rails.facet.action.rake.run.error.home.dir.not.found");
            Messages.showErrorDialog(module.getProject(), msg, RBundle.message("action.registered.shortcut.execute.disabled.title"));
            return;
        }

        final String workingDir = moduleRoot.getPath();

        // rake command
        final String rakeCmd = task.getFullCommand();
        final String title = RBundle.message("module.rails.generateapp.rake.result",
                             rakeCmd);

        final RakeArgumentsProvider provider =
                new RakeArgumentsProvider(new String[0], new String[]{rakeCmd});
        final Filter[] filters = {new RFileLinksFilter(module, workingDir)};

        final Sdk sdk = RModuleUtil.getModuleOrJRubyFacetSdk(module);
        if (sdk != null) {
            GemsRunner.runGemScriptInConsoleAndRefreshModule(module, sdk, title,
                                                            provider.getActions(),
                                                            true,
                                                            RAKE, workingDir, provider, null, filters, null);
            RailsFacetUtil.refreshRailsAppHomeContent(module);
        } else {
            final String msg = RBundle.message("action.registered.shortcut.execute.disabled.raketasks.msg",
                                               task.getFullCommand(),
                                               module.getName())
                               + " " + RBundle.message("sdk.no.specified");
            Messages.showErrorDialog(module.getProject(), msg, RBundle.message("action.registered.shortcut.execute.disabled.title"));
        }
    }

    /**
     * Generates Rake tasks actions for current module
     *
     * @return List of rake commands for module content root
     * @param project Project
     * @param sdk SDK with rails support
     * @param moduleName Name of facet's module(for UI dialog)
     * @param railsApplicHomeDirPath Rails Application Home Directory
     */
    private static List<RakeCommand> getRakeCommands(@Nullable final Project project,
                                                     @Nullable final Sdk sdk,
                                                     @NotNull final String moduleName,
                                                     @NotNull final String railsApplicHomeDirPath) {
        if (sdk == null) {
            return new ArrayList<RakeCommand>();
        }

        final String errorTitle = RBundle.message("execution.error.title.rake.tasks", moduleName);
        return RakeUtil.getCommands(RakeUtil.getRakeGemAvailableTasksOutput(sdk, project, errorTitle, railsApplicHomeDirPath));
    }

    /**
     * Generates Rake tree by rake script output
     *
     * @param commands - list of Rake command
     * @return RakeTask - the root of RakeTasks Tree
     */
    @NotNull
    private static RakeTask getRakeTasksByOutput(final List<RakeCommand> commands) {
        final RakeTaskSerializableImpl head = new RakeTaskSerializableImpl(RAKE_COMMAND, null, null, true, null);
        for (RakeCommand command : commands) {
            head.registerNewCommand(command);
        }
        return head;
    }
}
