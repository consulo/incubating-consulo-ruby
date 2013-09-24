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

package org.jetbrains.plugins.ruby.addins.rspec.run.configuration;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.JavaProgramRunner;
import com.intellij.execution.runners.RunnerInfo;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecModuleSettings;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.jruby.JRubyUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.AbstractRubyRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUtil;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkType;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 18, 2007
 */
public class RSpecRunConfiguration extends AbstractRubyRunConfiguration implements RSpecRunConfigurationParams {
    public static final String DEFAULT_TESTS_SEARCH_MASK = "**/*_spec." + RubyFileType.RUBY.getDefaultExtension();
    public static final String DEFAULT_SPEC_ARGS = "-fs";

    private String myTestsFolderPath = TextUtil.EMPTY_STRING;
    private String myTestScriptPath = TextUtil.EMPTY_STRING;
    private String myTestFileMask = TextUtil.EMPTY_STRING;
    private TestType myTestType = TestType.TEST_SCRIPT;
    private String mySpecArgs = TextUtil.EMPTY_STRING;

    private boolean myUseColoredOutput = true; //by default
    private boolean myUseCustomSpecRunner; //by default

    public static String DEFAULT_CUSTOM_SPEC_RUNNER = RBundle.message("run.configuration.messages.none");
    private String myCustomSpecsRunnerPath = DEFAULT_CUSTOM_SPEC_RUNNER;
    private boolean myShouldRunSpecSeparately;

    public RSpecRunConfiguration(final Project project,
                                 final ConfigurationFactory factory,
                                 final String name) {
        super(project, factory, name);

        setRubyArgs(RubyRunConfigurationUtil.collectArguments(RubyUtil.RUN_IN_CONSOLE_HACK_ARGUMENTS));
    }

    public static void copyParams(final RSpecRunConfigurationParams fromParams,
                                  final RSpecRunConfigurationParams toParams) {
        AbstractRubyRunConfiguration.copyParams(fromParams, toParams);

        toParams.setTestType(fromParams.getTestType());

        toParams.setTestsFolderPath(fromParams.getTestsFolderPath());
        toParams.setTestScriptPath(fromParams.getTestScriptPath());
        toParams.setTestFileMask(fromParams.getTestFileMask());

        toParams.setShouldUseCustomSpecRunner(fromParams.shouldUseCustomSpecRunner());
        toParams.setShouldRunSpecSeparately(fromParams.shouldRunSpecSeparately());
        toParams.setSpecArgs(fromParams.getSpecArgs());
        toParams.setCustomSpecsRunnerPath(fromParams.getCustomSpecsRunnerPath());

        toParams.setShouldUseColoredOutput(fromParams.shouldUseColoredOutput());
    }

    protected RSpecRunConfiguration createInstance() {
        return new RSpecRunConfiguration(getProject(), getFactory(), getName());
    }

    @SuppressWarnings({"deprecation"})
    public JDOMExternalizable createRunnerSettings(final ConfigurationInfoProvider provider) {
        return null;
    }

    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new RSpecRunConfigurationEditor(getProject(), this);
    }

    @SuppressWarnings({"deprecation"})
    public SettingsEditor<JDOMExternalizable> getRunnerSettingsEditor(final JavaProgramRunner runner) {
        return null;
    }

    public RunProfileState getState(final DataContext context, final RunnerInfo runnerInfo, final RunnerSettings runnerSettings, final ConfigurationPerRunnerSettings configurationSettings) throws ExecutionException {
        try {
            validateConfiguration(true);
        } catch (ExecutionException ee) {
            throw ee;
        } catch (Exception e) {
            throw new ExecutionException(e.getMessage(), e);
        }


        return new RSpecRunCommandLineState(this, runnerSettings, configurationSettings);
    }

    protected void validateConfiguration(boolean isExecution) throws Exception {
        RubyRunConfigurationUtil.inspectSDK(this, isExecution);
        RubyRunConfigurationUtil.inspectWorkingDirectory(this, isExecution);

        switch (getTestType()) {
            case ALL_IN_FOLDER:
                //testfolder
                inspectSpecsFolder(isExecution);
                break;
            case TEST_SCRIPT:
                //script
                inspectSpecScript(isExecution);
                break;
        }

        //if use custom runner
        if (shouldUseCustomSpecRunner()) {
            inspectCustomRunnerScript(isExecution);
        } else {
            //If alternatie SDK is choosen we had to use gem
            inspectRSpecGemInAlternativeSDK(isExecution);

            //If module
            inspectRSpecInModule(isExecution);
        }

        final String args = getSpecArgs();
        if (shouldUseColoredOutput()
                || (args != null && args.contains(RSpecUtil.COLOURED_COMMAND_LINE_ARG))) {
            if (SystemInfo.isWindows) {
                final List<String> gemsRootUrls = RubySdkType.getGemsRootUrls(getSdk());
                if (gemsRootUrls != null) {
                    for (String gemsRootUrl : gemsRootUrls) {
                        final VirtualFile gemsRoot = VirtualFileManager.getInstance().findFileByUrl(gemsRootUrl);
                        if (gemsRoot != null) {
                            final VirtualFile[] files = gemsRoot.getChildren();
                            for (VirtualFile file : files) {
                                if (file.isDirectory() && file.getName().startsWith(RSpecUtil.WIN_32_CONSOLE_GEM)) {
                                    final String msg = RBundle.message("rspec.run.configuration.test.plugin.doesnt.support.win32console.gem");
                                    RubyRunConfigurationUtil.throwExecutionOrRuntimeException(msg, isExecution);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void inspectRSpecInModule(boolean isExecution) throws Exception {
        if (!shouldUseAlternativeSdk() && !shouldUseCustomSpecRunner()) {
            final Module module = getModule();
            assert module != null;

            final ProjectJdk sdk = getSdk();
            assert sdk != null;

            if (RailsFacetUtil.hasRailsSupport(module)) {
                final boolean useRSpecPlugin = RSpecModuleSettings.getInstance(module).getRSpecSupportType() == RSpecModuleSettings.RSpecSupportType.RAILS_PLUGIN;
                if (useRSpecPlugin) {
                    //chec plugin is installed
                    final String railsAppHomeUrl = RailsFacetUtil.getRailsAppHomeDirPathUrl(module);
                    assert railsAppHomeUrl != null;

                    if (!RSpecUtil.isSpecScriptSupportInstalledInRailsProject(railsAppHomeUrl)) {
                        final String msg = RBundle.message("rspec.run.configuration.test.plugin.not.installed.install.or.change.to.gem");
                        RubyRunConfigurationUtil.throwExecutionOrRuntimeException(msg, isExecution);
                    }
                    return;
                }
                assertRSpecGemIsInstalled(isExecution, sdk, false);
                return;
            } else if (RubyUtil.isRubyModuleType(module) || JRubyUtil.hasJRubySupport(module)) {
                assertRSpecGemIsInstalled(isExecution, sdk, false);
                return;
            }
            final String msg = RBundle.message("rspec.run.configuration.test.module.should.be.ror");
            RubyRunConfigurationUtil.throwExecutionOrRuntimeException(msg, isExecution);
        }
    }

    private void inspectRSpecGemInAlternativeSDK(boolean isExecution) throws Exception {
        if (shouldUseAlternativeSdk() && !shouldUseCustomSpecRunner()) {
            final ProjectJdk sdk = getAlternativeSdk();
            assert sdk != null;

            assertRSpecGemIsInstalled(isExecution, sdk, true);
        }
    }

    private void assertRSpecGemIsInstalled(boolean isExecution, ProjectJdk sdk, final boolean isAlternativeSDK) throws Exception {
        if (!RSpecUtil.checkIfRSpecGemExists(sdk)) {
            final String msg = isAlternativeSDK
                    ? RBundle.message("rspec.run.configuration.test.no.gem.in.alternative.sdk")
                    : RBundle.message("rspec.run.configuration.test.no.gem.in.module.sdk");

            RubyRunConfigurationUtil.throwExecutionOrRuntimeException(msg, isExecution);
        }
    }

    private void inspectSpecScript(final boolean isExecution) throws Exception {
        final String scriptPath = getTestScriptPath().trim();
        final VirtualFile script = LocalFileSystem.getInstance().findFileByPath(scriptPath);
        if (TextUtil.isEmpty(scriptPath) || script == null || !script.exists()) {
            RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message("rspec.run.configuration.test.script.not.exists"), isExecution);
        }

        //noinspection ConstantConditions
        if (script.isDirectory()) {
            RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message("rspec.run.configuration.test.script.is.dir"), isExecution);
        }
    }

    private void inspectCustomRunnerScript(final boolean isExecution) throws Exception {
        final String scriptPath = getCustomSpecsRunnerPath().trim();
        if (TextUtil.isEmpty(scriptPath) || DEFAULT_CUSTOM_SPEC_RUNNER.equals(scriptPath)) {
            RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message("rspec.run.configuration.test.custom.runner.not.exists"), isExecution);
        }
        
        final VirtualFile script = LocalFileSystem.getInstance().findFileByPath(scriptPath);
        if (script == null || !script.exists()) {
            RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message("rspec.run.configuration.test.custom.runner.not.exists"), isExecution);
        }

        //noinspection ConstantConditions
        if (script.isDirectory()) {
            RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message("rspec.run.configuration.test.script.is.dir"), isExecution);
        }
    }

    private void inspectSpecsFolder(final boolean isExecution) throws Exception {
        final String folderPath = getTestsFolderPath().trim();
        File folder = new File(folderPath);
        if (!folder.exists()) {
            RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message("rspec.run.configuration.test.folder.not.exists"), isExecution);
        }

        if (!folder.isDirectory()) {
            RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message("rspec.run.configuration.test.folder.not.dir"), isExecution);
        }
    }

    public String getTestsFolderPath() {
        return myTestsFolderPath;
    }

    public String getTestScriptPath() {
        return myTestScriptPath;
    }

    public TestType getTestType() {
        return myTestType;
    }

    public String getTestFileMask() {
        return myTestFileMask;
    }

    /**
     * Path to folder
     *
     * @param path Path should contains only ruby style path separator: "/"
     */
    public void setTestsFolderPath(final String path) {
        myTestsFolderPath = TextUtil.getAsNotNull(path);
    }

    public void setTestScriptPath(final String pathOrMask) {
        myTestScriptPath = TextUtil.getAsNotNull(pathOrMask);
    }

    public void setTestType(@NotNull final TestType testType) {
        myTestType = testType;
    }

    public void setTestFileMask(final String testFileMask) {
        myTestFileMask = TextUtil.getAsNotNull(testFileMask);
    }

    public void readExternal(final Element element) throws InvalidDataException {
        RSpecRunConfigurationExternalizer.getInstance().readExternal(this, element);
    }

    public void writeExternal(final Element element) throws WriteExternalException {
        RSpecRunConfigurationExternalizer.getInstance().writeExternal(this, element);
    }

    public boolean shouldUseColoredOutput() {
        return myUseColoredOutput;
    }

    public void setShouldUseColoredOutput(final boolean enabled) {
        myUseColoredOutput = enabled;
    }

    public String getSpecArgs() {
        return mySpecArgs;
    }

    public void setSpecArgs(final String specArgs) {
        mySpecArgs = TextUtil.getAsNotNull(specArgs);
    }

    public String getCustomSpecsRunnerPath() {
        return TextUtil.getAsNotNull(myCustomSpecsRunnerPath).trim();
    }

    public void setCustomSpecsRunnerPath(final String specsRunnerPath) {
        myCustomSpecsRunnerPath = TextUtil.getAsNotNull(specsRunnerPath);
    }

    public void setShouldUseCustomSpecRunner(boolean useCustomSpecRunner) {
        myUseCustomSpecRunner = useCustomSpecRunner;
    }

    public boolean shouldUseCustomSpecRunner() {
        return myUseCustomSpecRunner;
    }

    public boolean shouldRunSpecSeparately() {
        return myShouldRunSpecSeparately;
    }

    public void setShouldRunSpecSeparately(final boolean shouldRunSpecSeparately) {
        myShouldRunSpecSeparately = shouldRunSpecSeparately;
    }
}
