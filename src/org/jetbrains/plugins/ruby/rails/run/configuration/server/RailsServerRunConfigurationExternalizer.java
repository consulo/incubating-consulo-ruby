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

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunConfigurationExternalizer;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 04.08.2007
 */
public class RailsServerRunConfigurationExternalizer extends RubyRunConfigurationExternalizer {
    private static RailsServerRunConfigurationExternalizer myInstance = new RailsServerRunConfigurationExternalizer();

    @NonNls
    public static final String RAILS_SERVER_CONFIG_SETTINGS_ID = "RAILS_SERVER_CONFIG_SETTINGS_ID";

    @NonNls
    private static final String CHOOSE_MANUALLY = "CHOOSE_MANUALLY";
    @NonNls
    private static final String PORT = "PORT";
    @NonNls
    private static final String IP = "IP";
    @NonNls
    public static final String SERVER_TYPE = "RAILS_SERVER_TYPE";
    @NonNls
    public static final String ENVIRONMENT_TYPE = "ENVIRONMENT_TYPE";

    @Override
	public void writeExternal(final RubyRunConfiguration config, final Element elem) {
        super.writeExternal(config, elem);

        final RailsServerRunConfiguration conf = (RailsServerRunConfiguration)config;
        writeOption(PORT,  conf.getPort(), elem);
        writeOption(IP,  conf.getIPAddr(), elem);
        writeOption(CHOOSE_MANUALLY,  String.valueOf(conf.isChoosePortManually()), elem);
        writeOption(SERVER_TYPE, conf.getServerType(), elem);
        writeOption(ENVIRONMENT_TYPE,  conf.getRailsEnvironmentType().toString(), elem);
    }

    @Override
	public void readExternal(final RubyRunConfiguration config, final Element elem) {
        super.readExternal(config, elem);

        final RailsServerRunConfiguration conf = (RailsServerRunConfiguration)config;

        //noinspection unchecked
        Map<String, String> optionsByName = buildOptionsByElement(elem);

        conf.setPort(optionsByName.get(PORT));
        conf.setIPAddr(optionsByName.get(IP));
        conf.setChoosePortManually(Boolean.valueOf(optionsByName.get(CHOOSE_MANUALLY)));

        final String serverType = optionsByName.get(SERVER_TYPE);
        if (serverType != null) {
            conf.setServerType(serverType);
        }
        final String envType = optionsByName.get(ENVIRONMENT_TYPE);
        if (envType != null) {
            conf.setRailsEnvironmentType(Enum.valueOf(RailsServerRunConfiguration.RailsEnvironmentType.class,
                                          envType));
        }
    }

    public static RailsServerRunConfigurationExternalizer getInstance(){
        return myInstance;
    }

    @Override
	public String getID() {
        return RAILS_SERVER_CONFIG_SETTINGS_ID;
    }
}
