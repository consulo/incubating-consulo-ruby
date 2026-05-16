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

package org.jetbrains.plugins.ruby.ruby.run.confuguration;

import consulo.application.Application;
import consulo.content.bundle.Sdk;
import consulo.execution.CantRunException;
import consulo.execution.RuntimeConfigurationException;
import consulo.execution.configuration.CommandLineState;
import consulo.execution.process.ProcessTerminatedListener;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.module.Module;
import consulo.module.content.ModuleRootManager;
import consulo.module.content.layer.OrderEnumerator;
import consulo.process.ExecutionException;
import consulo.process.ProcessHandler;
import consulo.process.ProcessHandlerBuilder;
import consulo.process.ProcessHandlerBuilderFactory;
import consulo.process.cmd.GeneralCommandLine;
import jakarta.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.run.Runner;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.ruby.sdk.jruby.JRubySdkType;
import org.jetbrains.plugins.ruby.settings.RApplicationSettings;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Nov 7, 2007
 */
public abstract class ColouredCommandLineState extends CommandLineState {
    protected ColouredCommandLineState(ExecutionEnvironment environment) {
        super(environment);
    }

    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        final GeneralCommandLine cmdLine = createCommandLine();
        final ProcessHandler processHandler = createOSProcessHandler(cmdLine);
        ProcessTerminatedListener.attach(processHandler);
        return processHandler;
    }

    public GeneralCommandLine createCommandLine() throws ExecutionException {
        return createGeneralDefaultCmdLine((AbstractRubyRunConfiguration) getEnvironment().getRunProfile());
    }

    public static ProcessHandler createOSProcessHandler(GeneralCommandLine cmd) throws ExecutionException {
        ProcessHandlerBuilderFactory builderFactory = Application.get().getInstance(ProcessHandlerBuilderFactory.class);

        ProcessHandlerBuilder builder = builderFactory.newBuilder(cmd);
        if (RApplicationSettings.getInstance().useConsoleColorMode) {
            builder.colored();
        }
        return builder.build();
    }

    protected GeneralCommandLine createGeneralDefaultCmdLine(@Nonnull final AbstractRubyRunConfiguration config) throws CantRunException {
        checkConfiguration(config);

        final Sdk sdk = config.getSdk();
        assert (sdk != null);


        final String workingDir = config.getWorkingDirectory().trim().length() != 0 ? config.getWorkingDirectory() : null;

        /* Uncomment to set working directory by ruby itself
            commandLine.addParameter("-C");
            commandLine.addParameter(myConfig.getWORK_DIR());
        */

        final Module module = config.getModule();
        final String classPath;
        if (module == null || !JRubySdkType.isJRubySDK(sdk)) {
            classPath = null;
        }
        else {
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);

            OrderEnumerator orderEnumerator = moduleRootManager.orderEntries();
            orderEnumerator = orderEnumerator.withoutSdk().recursively();

            classPath = orderEnumerator.getSourcePathsList().getPathsString();
        }

        return Runner.createAndSetupCmdLine(null, workingDir, classPath, config.getEnvs(), config.isPassParentEnvs(), RubySdkUtil.getVMExecutablePath(sdk));
    }

    protected void checkConfiguration(@Nonnull final AbstractRubyRunConfiguration config) throws CantRunException {
        try {
            config.checkConfiguration();
        }
        catch (RuntimeConfigurationException e) {
            throw new CantRunException(e.getMessage());
        }
    }

    protected void attachCompilerForJRuby(final AbstractRubyRunConfiguration config) {
      /*  if (JRubySdkType.isJRubySDK(config.getSdk())) {
            setModuleToCompile(config.getModule());
        }     */
    }
}
