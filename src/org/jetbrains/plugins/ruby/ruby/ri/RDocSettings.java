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

package org.jetbrains.plugins.ruby.ruby.ri;

import org.consulo.lombok.annotations.ApplicationService;
import org.jdom.Element;
import org.jetbrains.plugins.ruby.support.ui.checkableDir.CheckableDirectoriesContainer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 8, 2006
 */
@ApplicationService
public class RDocSettings implements JDOMExternalizable
{
	private boolean doUseDefaults;

	private final CheckableDirectoriesContainer docDirs;

	public RDocSettings()
	{
		doUseDefaults = true;
		docDirs = new CheckableDirectoriesContainer();
	}

	@Override
	public void readExternal(Element element) throws InvalidDataException
	{
		RDocSettingsExternalizer.getInstance().readExternal(this, element);
	}

	@Override
	public void writeExternal(Element element) throws WriteExternalException
	{
		RDocSettingsExternalizer.getInstance().writeExternal(this, element);
	}

	public void setUseDefaults(final boolean b)
	{
		doUseDefaults = b;
	}

	public boolean doUseDefaults()
	{
		return doUseDefaults;
	}

	public CheckableDirectoriesContainer getDocDirs()
	{
		return docDirs;
	}
}
