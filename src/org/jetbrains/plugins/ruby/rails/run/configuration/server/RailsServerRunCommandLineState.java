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

package org.jetbrains.plugins.ruby.rails.run.configuration.server;

import java.io.IOException;

import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunCommandLineState;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.util.net.NetUtils;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 04.08.2007
 */
public class RailsServerRunCommandLineState extends RubyRunCommandLineState {

    public RailsServerRunCommandLineState(final RailsServerRunConfiguration config,
			ExecutionEnvironment executionEnvironment) {
        super(config, executionEnvironment);
    }

    public GeneralCommandLine createCommandLine() throws ExecutionException {
        final GeneralCommandLine commandLine = createGeneralDefaultCmdLine(myConfig);
        final RailsServerRunConfiguration config = (RailsServerRunConfiguration)myConfig;

        int port_int;
        if (config.isChoosePortManually()) {
            port_int = Integer.valueOf(config.getPort());
        } else {
            try {
                port_int = NetUtils.findAvailableSocketPort();
            } catch (IOException e) {
                throw new ExecutionException("Error occured while searching for free port");
            }
        }

        //interpretator params
        addParams(commandLine, myConfig.getRubyArgs());
        //scrtipt/server
        commandLine.addParameter(myConfig.getScriptPath());

        //server
        final String server = config.getServerType();
        commandLine.addParameter(server);

        //server port
        commandLine.addParameter(RailsConstants.PARAM_SERVER_PORT);
        commandLine.addParameter(String.valueOf(port_int));

        //server ip address
        commandLine.addParameter(RailsConstants.PARAM_SERVER_IP);
        commandLine.addParameter(config.getIPAddr());

        //server environment
        commandLine.addParameter(RailsConstants.PARAM_SERVER_ENVIRONMENT);
        commandLine.addParameter(config.getRailsEnvironmentType().getParamName());

        //server args
        addParams(commandLine, myConfig.getScriptArgs());

        return commandLine;
    }
}
