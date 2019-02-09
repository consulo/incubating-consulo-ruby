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

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunConfigurationParams;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: May 22, 2008
 */
public interface RailsServerRunConfigurationParams extends RubyRunConfigurationParams
{

	public String getPort();

	public boolean isChoosePortManually();

	@Nonnull
	public String getIPAddr();

	@Nonnull
	public String getServerType();

	public RailsServerRunConfiguration.RailsEnvironmentType getRailsEnvironmentType();

	public void setPort(String port);

	public void setChoosePortManually(boolean choosePortManually);

	public void setIPAddr(String ip);

	public void setServerType(@Nonnull String type);

	public void setRailsEnvironmentType(RailsServerRunConfiguration.RailsEnvironmentType railsEnvironmentType);
}
