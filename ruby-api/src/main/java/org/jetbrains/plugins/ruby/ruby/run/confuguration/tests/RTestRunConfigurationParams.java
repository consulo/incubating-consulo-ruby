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

package org.jetbrains.plugins.ruby.ruby.run.confuguration.tests;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.run.confuguration.AbstractRubyRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.AbstractRubyRunConfigurationParams;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: May 22, 2008
 */
public interface RTestRunConfigurationParams extends AbstractRubyRunConfigurationParams
{
	public String getTestsFolderPath();

	public String getTestScriptPath();

	public String getTestMethodName();

	public AbstractRubyRunConfiguration.TestType getTestType();

	public String getTestQualifiedClassName();

	public String getTestFileMask();

	public boolean isInheritanceCheckDisabled();

	public void setTestsFolderPath(String path);

	public void setTestScriptPath(String pathOrMask);

	public void setTestMethodName(@Nullable String name);

	public void setTestType(@Nonnull AbstractRubyRunConfiguration.TestType testType);

	public void setTestQualifiedClassName(@Nullable String testClassName);

	public void setTestFileMask(String testFileMask);

	public void setInheritanceCheckDisabled(boolean disabled);
}
