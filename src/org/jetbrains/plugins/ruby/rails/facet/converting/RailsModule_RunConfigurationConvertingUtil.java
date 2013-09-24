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

package org.jetbrains.plugins.ruby.rails.facet.converting;

import com.intellij.ide.impl.convert.JDomConvertingUtil;
import com.intellij.openapi.util.Condition;
import org.jdom.Element;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.addins.rspec.run.configuration.RSpecRunConfigurationExternalizer;
import org.jetbrains.plugins.ruby.rails.run.configuration.server.RailsServerRunConfiguration;
import org.jetbrains.plugins.ruby.rails.run.configuration.server.RailsServerRunConfigurationExternalizer;
import static org.jetbrains.plugins.ruby.rails.run.configuration.server.RailsServerRunConfigurationExternalizer.ENVIRONMENT_TYPE;
import static org.jetbrains.plugins.ruby.rails.run.configuration.server.RailsServerRunConfigurationExternalizer.SERVER_TYPE;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunConfigurationExternalizer;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.RTestRunConfigurationExternalizer;
import org.jetbrains.plugins.ruby.settings.SettingsExternalizer;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jun 9, 2008
 */
public class RailsModule_RunConfigurationConvertingUtil {
    public static void convertWorkspace(final Element root, RailsModule_ConvertingContext context) {
        final Element runManager = JDomConvertingUtil.findComponent(root, RailsModule_JDomConstants.RUN_MANAGER_COMPONENT_NAME);
        if (runManager == null) return;

        //noinspection unchecked
        final List<Element> list = runManager.getChildren(RailsModule_JDomConstants.CONFIGURATION_ELEMENT);
        for (Element e : list) {
            convertRunConfiguration(e, context);
        }
    }

    private static RunConfFactories getFactoryTypeBy(final Element runConfig) {
        final String factoryName = runConfig.getAttributeValue("factoryName");

        if (RunConfFactories.RSPEC.getAttrName().equals(factoryName)) {
            return RunConfFactories.RSPEC;
        }

        if (RunConfFactories.SERVER.getAttrName().equals(factoryName)) {
            return RunConfFactories.SERVER;
        }

        if (RunConfFactories.RUBY_SCRIPT.getAttrName().equals(factoryName)) {
            return RunConfFactories.RUBY_SCRIPT;
        }

        if (RunConfFactories.RUBY_TEST.getAttrName().equals(factoryName)) {
            return RunConfFactories.RUBY_TEST;
        }

        return null;
    }

    public static void convertRunConfiguration(final Element runConfig,
                                               final RailsModule_ConvertingContext context) {
        final RunConfFactories confFactoryType = getFactoryTypeBy(runConfig);
        if (confFactoryType == null) {
            return;
        }

        final Element passEnvs = new Element(confFactoryType.getTagName());
        passEnvs.setAttribute(SettingsExternalizer.NAME, RailsModule_JDomConstants.PASS_PAREN_ENVS);
        passEnvs.setAttribute(SettingsExternalizer.VALUE, Boolean.TRUE.toString());

        final Element envs = new Element(RailsModule_JDomConstants.ENVS_TAG);

        runConfig.addContent(passEnvs);
        runConfig.addContent(envs);

        if (confFactoryType == RunConfFactories.SERVER) {
            runConfig.setAttribute("type", RComponents.RAILS_RUN_CONFIGURATION_TYPE);
            // server type
            final Element serverType = JDomConvertingUtil.findChild(runConfig, new Condition<Element>() {
                public boolean value(final Element element) {
                    return confFactoryType.getTagName().equals(element.getName())
                            && RailsModule_JDomConstants.OLD_SERVER_TYPE.equals(element.getAttributeValue(SettingsExternalizer.NAME));
                }
            });

            final Element newServerType = new Element(confFactoryType.getTagName());
            newServerType.setAttribute(SettingsExternalizer.NAME,
                    SERVER_TYPE);
            newServerType.setAttribute(SettingsExternalizer.VALUE,
                    RailsServerRunConfiguration.DEFAULT_SERVER);
            runConfig.removeContent(serverType);
            runConfig.addContent(newServerType);

            //environment
            final Element environmentType = new Element(confFactoryType.getTagName());
            environmentType.setAttribute(SettingsExternalizer.NAME,
                    RailsServerRunConfigurationExternalizer.ENVIRONMENT_TYPE);
            environmentType.setAttribute(SettingsExternalizer.VALUE,
                    RailsServerRunConfiguration.RailsEnvironmentType.DEVELOPMENT.toString());

            runConfig.addContent(environmentType);
        }
        else if (confFactoryType == RunConfFactories.RSPEC) {
            runConfig.setAttribute("type", RComponents.RSPEC_RUN_CONFIGURATION_TYPE);
        }
    }

    public static void convertWorkspaceBackward(Element root) {
        final Element runManager = JDomConvertingUtil.findComponent(root, RailsModule_JDomConstants.RUN_MANAGER_COMPONENT_NAME);
        if (runManager == null) return;

        //noinspection unchecked
        final List<Element> list = runManager.getChildren(RailsModule_JDomConstants.CONFIGURATION_ELEMENT);
        for (Element e : list) {
            convertRunConfigurationBackward(e);
        }
    }

    public static void convertRunConfigurationBackward(Element runConfig) {
        final RunConfFactories confFactoryType = getFactoryTypeBy(runConfig);
        if (confFactoryType == null) {
            return;
        }

        runConfig.setAttribute("type", RComponents.RUBY_RUN_CONFIGURATION_TYPE);
        final Element passEnvs = JDomConvertingUtil.findChild(runConfig, new Condition<Element>() {
            public boolean value(final Element element) {
                return confFactoryType.getTagName().equals(element.getName())
                        && RailsModule_JDomConstants.PASS_PAREN_ENVS.equals(element.getAttributeValue(SettingsExternalizer.NAME));
            }
        });

        runConfig.removeContent(passEnvs);
        runConfig.removeChildren(RailsModule_JDomConstants.ENVS_TAG);

        if (confFactoryType == RunConfFactories.SERVER) {
            // server type
            final Element serverType = JDomConvertingUtil.findChild(runConfig, new Condition<Element>() {
                public boolean value(final Element element) {
                    return confFactoryType.getTagName().equals(element.getName())
                            && SERVER_TYPE.equals(element.getAttributeValue(SettingsExternalizer.NAME));
                }
            });

            final Element oldServerType = new Element(confFactoryType.getTagName());
            oldServerType.setAttribute(SettingsExternalizer.NAME, RailsModule_JDomConstants.OLD_SERVER_TYPE);
            oldServerType.setAttribute(SettingsExternalizer.VALUE, RailsModule_JDomConstants.OLD_SERVER_DEFAULT_NAME);

            runConfig.removeContent(serverType);
            runConfig.addContent(oldServerType);

            //environment
            final Element environmentType = JDomConvertingUtil.findChild(runConfig, new Condition<Element>() {
                public boolean value(final Element element) {
                    return confFactoryType.getTagName().equals(element.getName())
                            && ENVIRONMENT_TYPE.equals(element.getAttributeValue(SettingsExternalizer.NAME));
                }
            });
            runConfig.removeContent(environmentType);
        }
    }

    public enum RunConfFactories {
        RUBY_SCRIPT("Ruby script", RubyRunConfigurationExternalizer.RUBY_RUN_CONFIG_SETTINGS_ID),
        RUBY_TEST("Ruby test", RTestRunConfigurationExternalizer.RTEST_RUN_CONFIG_SETTINGS_ID),
        RSPEC("RSpec", RSpecRunConfigurationExternalizer.RSPEC_RUN_CONFIG_SETTINGS_ID),
        SERVER("Server", RailsServerRunConfigurationExternalizer.RAILS_SERVER_CONFIG_SETTINGS_ID);


        private final String myAttrName;
        private final String myTagName;

        RunConfFactories(final String attrName, final String tagName) {
            myAttrName = attrName;
            myTagName = tagName;
        }

        public String getAttrName() {
            return myAttrName;
        }

        public String getTagName() {
            return myTagName;
        }
    }
}
