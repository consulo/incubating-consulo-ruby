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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.jdom.Element;

import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.LocatableConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTable;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 18.07.2007
 */
public abstract class AbstractRubyRunConfiguration extends RunConfigurationBase implements LocatableConfiguration, AbstractRubyRunConfigurationParams
{
	protected final static String SUGGESTED_NAME = RBundle.message("run.configuration.messages.suggested.name");
	private static final String TO_CLONE_ELEMENT_NAME = "TO_CLONE_ELEMENT_NAME";

	private static final Logger LOG = Logger.getInstance(AbstractRubyRunConfiguration.class.getName());


	private String myModuleName;

	@Nonnull
	private String myWorkingDirectory = TextUtil.EMPTY_STRING;
	private String myRubyArgs = TextUtil.EMPTY_STRING;
	private String mySdkName = TextUtil.EMPTY_STRING;

	private boolean myShouldUseAlternativeSdk; //false

	private Map<String, String> myEnvs = new LinkedHashMap<String, String>();
	private boolean myPassParentEnvs = true;

	protected AbstractRubyRunConfiguration(final Project project, final ConfigurationFactory factory, final String name)
	{
		super(project, factory, name);
	}

	public static void copyParams(final AbstractRubyRunConfigurationParams fromParams, final AbstractRubyRunConfigurationParams toParams)
	{
		toParams.setRubyArgs(fromParams.getRubyArgs());
		toParams.setWorkingDirectory(fromParams.getWorkingDirectory());
		toParams.setModule(fromParams.getModule());
		toParams.setShouldUseAlternativeSdk(fromParams.shouldUseAlternativeSdk());
		toParams.setAlternativeSdk(fromParams.getAlternativeSdk());
		toParams.setEnvs(fromParams.getEnvs());
		toParams.setPassParentEnvs(fromParams.isPassParentEnvs());
	}

	protected abstract AbstractRubyRunConfiguration createInstance();

	@Nonnull
	public Module[] getModules()
	{
		return RModuleUtil.getAllModulesWithRubySupport(getProject());
	}

	@Override
	@Nullable
	public Module getModule()
	{
		return findModuleByName(myModuleName);
	}

	@Nullable
	public String getModuleName()
	{
		return myModuleName;
	}

	@Override
	public String getRubyArgs()
	{
		return myRubyArgs;
	}

	@Nullable
	public Sdk getSdk()
	{
		if(shouldUseAlternativeSdk())
		{
			return getAlternativeSdk();
		}
		final Module module = getModule();
		if(module != null)
		{
			return RModuleUtil.getModuleOrJRubyFacetSdk(module);
		}
		return null;
	}

	@Override
	@Nonnull
	public String getWorkingDirectory()
	{
		return myWorkingDirectory;
	}

	@Override
	public boolean isGeneratedName()
	{
		return getName().equals(SUGGESTED_NAME);
	}

	@Override
	public void setModule(@Nullable final Module module)
	{
		setModuleName(module != null ? module.getName() : null);
	}

	public void setModuleName(@Nullable String moduleName)
	{
		myModuleName = TextUtil.getAsNotNull(moduleName);
	}

	@Override
	public void setRubyArgs(@Nullable String myRubyArgs)
	{
		this.myRubyArgs = TextUtil.getAsNotNull(myRubyArgs);
	}

	@Override
	public void setWorkingDirectory(@Nullable final String dir)
	{
		this.myWorkingDirectory = TextUtil.getAsNotNull(dir);
	}

	@Override
	public String suggestedName()
	{
		return SUGGESTED_NAME;
	}

	@Nullable
	private Module findModuleByName(@Nullable final String name)
	{
		if(name == null)
		{
			return null;
		}

		final Module module = ModuleManager.getInstance(getProject()).findModuleByName(name);
		return module != null && !module.isDisposed() ? module : null;
	}

	public String getAlternativeSdkName()
	{
		return mySdkName;
	}

	@Override
	public Sdk getAlternativeSdk()
	{
		if(mySdkName == null)
		{
			return null;
		}
		final Sdk sdk = SdkTable.getInstance().findSdk(mySdkName);
		return RubySdkUtil.isSDKValid(sdk) ? sdk : null;
	}

	@Override
	public void setAlternativeSdk(@Nullable final Sdk sdk)
	{
		setAlternativeSdkName(sdk != null ? sdk.getName() : null);
	}

	public void setAlternativeSdkName(@Nullable final String sdkName)
	{
		mySdkName = TextUtil.getAsNotNull(sdkName);
	}

	protected abstract void validateConfiguration(final boolean isExecution) throws Exception;

	@Override
	public void checkConfiguration() throws RuntimeConfigurationException
	{
		try
		{
			validateConfiguration(false);
		}
		catch(RuntimeConfigurationException ee)
		{
			throw ee;
		}
		catch(Exception e)
		{
			throw new RuntimeConfigurationException(e.getMessage());
		}
	}

	@Override
	public boolean shouldUseAlternativeSdk()
	{
		return myShouldUseAlternativeSdk;
	}

	@Override
	public void setShouldUseAlternativeSdk(final boolean shouldUseAlternativeSdk)
	{
		myShouldUseAlternativeSdk = shouldUseAlternativeSdk;
	}

	@Override
	public Map<String, String> getEnvs()
	{
		return myEnvs;
	}

	@Override
	public void setEnvs(@Nonnull final Map<String, String> envs)
	{
		myEnvs = envs;
	}

	@Override
	public boolean isPassParentEnvs()
	{
		return myPassParentEnvs;
	}

	@Override
	public void setPassParentEnvs(final boolean passParentEnvs)
	{
		this.myPassParentEnvs = passParentEnvs;
	}

	@Override
	public AbstractRubyRunConfiguration clone()
	{
		final Element element = new Element(TO_CLONE_ELEMENT_NAME);
		try
		{
			writeExternal(element);
			final AbstractRubyRunConfiguration configuration = createInstance();
			configuration.readExternal(element);
			return configuration;
		}
		catch(InvalidDataException e)
		{
			LOG.error(e);
			return null;
		}
		catch(WriteExternalException e)
		{
			LOG.error(e);
			return null;
		}
	}

	public enum TestType
	{
		ALL_IN_FOLDER,
		TEST_SCRIPT,
		TEST_CLASS,
		TEST_METHOD,
	}
}
