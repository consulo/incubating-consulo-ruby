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

package org.jetbrains.plugins.ruby.jruby.facet;

import java.util.Map;

import javax.annotation.Nonnull;

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.settings.SettingsExternalizer;

/**
 * Created by IntelliJ IDEA.
 * User: oleg, Roman.Chernyatchik
 * Date: Sep 11, 2007
 */
public class JRubyFacetExternalizer extends SettingsExternalizer
{
	private static JRubyFacetExternalizer myInstance = new JRubyFacetExternalizer();

	@NonNls
	private static final String JRUBY_SDK_NAME = "JRUBY_SDK_NAME";
	@NonNls
	private static final String JRUBY_FACET_CONFIG_ID = "JRUBY_FACET_CONFIG_ID";
	@NonNls
	private static final String USE_TEST_UNIT_FRAMEWORK = "USE_TEST_UNIT_FRAMEWORK";
	@NonNls
	private static final String TEST_UNIT_FRAMEWORK_ROOT_URL = "TEST_UNIT_FRAMEWORK_ROOT_URL";

	public void writeExternal(@Nonnull final RSupportPerModuleSettingsImpl config, @Nonnull final Element elem)
	{

		writeOption(USE_TEST_UNIT_FRAMEWORK, Boolean.toString(config.shouldUseTestUnitTestFramework()), elem);
		config.getLoadPathDirs().writeCheckableDirectores(elem, this);
		writeOption(TEST_UNIT_FRAMEWORK_ROOT_URL, config.getUnitTestsRootUrl(), elem);
	}

	public void readExternal(@Nonnull final RSupportPerModuleSettingsImpl config, @Nonnull final Element elem)
	{
		//noinspection unchecked
		final Map<String, String> optionsByName = buildOptionsByElement(elem);
		//config.setSdkByName(optionsByName.get(JRUBY_SDK_NAME));
		config.getLoadPathDirs().loadCheckableDirectores(optionsByName);

		final String shouldUseTestFrStr = optionsByName.get(USE_TEST_UNIT_FRAMEWORK);
		config.setShouldUseTestUnitTestFramework(shouldUseTestFrStr != null && Boolean.valueOf(shouldUseTestFrStr));

		final String testUnitUrl = optionsByName.get(TEST_UNIT_FRAMEWORK_ROOT_URL);
		config.setUnitTestsRootUrl(TextUtil.isEmpty(testUnitUrl) ? null : testUnitUrl);
	}

	public static JRubyFacetExternalizer getInstance()
	{
		return myInstance;
	}

	@Override
	public String getID()
	{
		return JRUBY_FACET_CONFIG_ID;
	}
}
