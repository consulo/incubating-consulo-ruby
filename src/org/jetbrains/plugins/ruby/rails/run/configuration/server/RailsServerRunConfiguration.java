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
import java.net.ServerSocket;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.rails.RailsUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.run.configuration.RailsRunConfigurationUtil;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunConfiguration;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.text.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 04.08.2007
 */
public class RailsServerRunConfiguration extends RubyRunConfiguration implements RailsServerRunConfigurationParams
{
	public static final String DEFAULT_SERVER = "Default";

	private boolean myChoosePortManually;
	@NotNull
	private String myServerType = DEFAULT_SERVER;
	@NotNull
	private RailsEnvironmentType myEnvironmentType = RailsEnvironmentType.DEVELOPMENT;
	private String myPort = TextUtil.EMPTY_STRING;
	private String myIP = TextUtil.EMPTY_STRING;

	public RailsServerRunConfiguration(final Project project, final ConfigurationFactory factory, final String name)
	{
		super(project, factory, name);
	}

	@Override
	protected RailsServerRunConfiguration createInstance()
	{
		return new RailsServerRunConfiguration(getProject(), getFactory(), getName());
	}

	public static void copyParams(final RailsServerRunConfigurationParams fromParams, final RailsServerRunConfigurationParams toParams)
	{
		RubyRunConfiguration.copyParams(fromParams, toParams);

		toParams.setIPAddr(fromParams.getIPAddr());
		toParams.setPort(fromParams.getPort());
		toParams.setChoosePortManually(fromParams.isChoosePortManually());
		toParams.setServerType(fromParams.getServerType());
		toParams.setRailsEnvironmentType(fromParams.getRailsEnvironmentType());
	}

	/**
	 * @param module Rails or Java Module
	 * @return Work directory if Rails Application Home Directory or null(if no rails in module)
	 */
	@Nullable
	public static String getRailsWorkDirByModule(@NotNull final Module module)
	{
		return RailsFacetUtil.getRailsAppHomeDirPath(module);
	}

	/**
	 * @param module Ruby or Java Module
	 * @return Server script path corresponding to specified module : {rails application home dir}/script/server
	 */
	@Nullable
	public static String getServerScriptPathByModule(final Module module)
	{
		final String homeDir = RailsFacetUtil.getRailsAppHomeDirPath(module);
		if(homeDir == null)
		{
			return null;
		}
		return VirtualFileUtil.buildSystemIndependentPath(homeDir, RailsConstants.SERVER_SCRIPT);
	}

	@Override
	public boolean isChoosePortManually()
	{
		return myChoosePortManually;
	}

	@Override
	public void setChoosePortManually(final boolean choosePortManually)
	{
		myChoosePortManually = choosePortManually;
	}

	@Override
	public String getPort()
	{
		return myPort;
	}

	@Override
	public void setPort(final String port)
	{
		myPort = (port == null ? "0" : port);
	}

	@Override
	@NotNull
	public String getIPAddr()
	{
		return myIP;
	}

	@Override
	@NotNull
	public String getServerType()
	{
		return myServerType;
	}

	@Override
	public void setServerType(@NotNull final String type)
	{
		myServerType = type;
	}

	@Override
	public void setIPAddr(final String ip)
	{
		myIP = TextUtil.getAsNotNull(ip);
	}

	/**
	 * Valid Modules for Rails Server configuration should contain Rails/JRails facet
	 *
	 * @return valid rails modules
	 */
	@Override
	@NotNull
	public Module[] getModules()
	{
		return RailsUtil.getAllModulesWithRailsSupport(getProject());
	}

	@Override
	public SettingsEditor<? extends RunConfiguration> getConfigurationEditor()
	{
		return new RailsServerRunConfigurationEditor(getProject(), this);
	}

	@Override
	public void readExternal(Element element) throws InvalidDataException
	{
		RailsServerRunConfigurationExternalizer.getInstance().readExternal(this, element);
	}

	@Override
	public void writeExternal(Element element) throws WriteExternalException
	{
		RailsServerRunConfigurationExternalizer.getInstance().writeExternal(this, element);
	}

	@Nullable
	@Override
	public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException
	{
		try
		{
			validateConfiguration(true);
		}
		catch(ExecutionException ee)
		{
			throw ee;
		}
		catch(Exception e)
		{
			throw new ExecutionException(e.getMessage(), e);
		}


		return new RailsServerRunCommandLineState(this, executionEnvironment);

	}

	@Override
	protected void validateConfiguration(final boolean isExecution) throws Exception
	{
		super.validateConfiguration(isExecution);

		RailsRunConfigurationUtil.inspectRailsSDK(this, isExecution);
		inspectPort(isExecution);
		inspectIPAddr(isExecution);
	}

	private void inspectIPAddr(final boolean isExecution) throws Exception
	{
		final String msg = "run.configuration.script.ip.bad.format";
		final String ip_str = getIPAddr();
		final StringTokenizer st = new StringTokenizer(ip_str, ".", true);
		if(st.countTokens() != 7)
		{
			RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message(msg), isExecution);
		}
		// first byte
		checkNumberFormat(isExecution, st.nextToken(), true, msg);
		checkPoint(isExecution, st.nextToken());
		// secont byte
		checkNumberFormat(isExecution, st.nextToken(), true, msg);
		checkPoint(isExecution, st.nextToken());
		// third byte
		checkNumberFormat(isExecution, st.nextToken(), true, msg);
		checkPoint(isExecution, st.nextToken());
		// fourth byte
		checkNumberFormat(isExecution, st.nextToken(), true, msg);
	}

	private void inspectPort(final boolean isExecution) throws Exception
	{
		if(isChoosePortManually())
		{
			final String port_str = getPort();

			checkNumberFormat(isExecution, port_str, false, "run.configuration.script.port.bad.format");

			try
			{
				final ServerSocket s = new ServerSocket(Integer.valueOf(port_str));
				s.close();
			}
			catch(IOException e)
			{
				throw new ExecutionException(RBundle.message("run.configuration.script.port.is.busy", port_str));
			}
		}
	}

	private void checkNumberFormat(final boolean isExecution, final String port_str, final boolean isByte, final String msg) throws Exception
	{
		Integer port = null;
		try
		{
			port = Integer.valueOf(port_str);
		}
		catch(NumberFormatException e)
		{
			RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message(msg), isExecution);
		}
		assert port != null;
		if(port < 0 || port > (isByte ? 255 : 65535))
		{
			RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message(msg), isExecution);
		}
	}

	private void checkPoint(final boolean isExecution, final String port_str) throws Exception
	{
		if(port_str.equals("."))
		{
			return;
		}
		RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message("run.configuration.script.ip.bad.format"), isExecution);
	}

	@Override
	public RailsEnvironmentType getRailsEnvironmentType()
	{
		return myEnvironmentType;
	}

	@Override
	public void setRailsEnvironmentType(final RailsEnvironmentType railsEnvironmentType)
	{
		myEnvironmentType = railsEnvironmentType;
	}

	public enum RailsEnvironmentType
	{
		DEVELOPMENT,
		PRODUCTION,
		TEST;

		public String getParamName()
		{
			return super.toString().toLowerCase();
		}
	}
}
