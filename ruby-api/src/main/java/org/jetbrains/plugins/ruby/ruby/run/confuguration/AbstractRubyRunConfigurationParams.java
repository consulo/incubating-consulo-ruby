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

import java.util.Map;

import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: May 22, 2008
 */
public interface AbstractRubyRunConfigurationParams
{
	@Nullable
	public Module getModule();

	public String getRubyArgs();

	public String getWorkingDirectory();

	@Nullable
	public Sdk getAlternativeSdk();

	public Map<String, String> getEnvs();

	public boolean isPassParentEnvs();

	public boolean shouldUseAlternativeSdk();

	public void setModule(@Nullable final Module module);

	public void setRubyArgs(@Nullable String myRubyArgs);

	public void setWorkingDirectory(@Nullable String dir);

	public void setAlternativeSdk(@Nullable final Sdk sdk);

	public void setShouldUseAlternativeSdk(boolean shouldUseAlternativeSdk);

	public void setEnvs(Map<String, String> envs);

	public void setPassParentEnvs(boolean passParentEnvs);
}
