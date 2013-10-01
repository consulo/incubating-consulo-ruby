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

package org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import com.intellij.openapi.components.ServiceManager;

/**
 * @author yole
 */
public class ParamDefManager
{
	public static ParamDefManager getInstance()
	{
		return ServiceManager.getService(ParamDefManager.class);
	}

	private Map<String, ParamDef[]> myParamDefs = new HashMap<String, ParamDef[]>();

	public void registerParamDef(String methodFQN, ParamDef[] paramDefs)
	{
		myParamDefs.put(methodFQN, paramDefs);
	}

	public ParamDef[] getParamDefs(Symbol method)
	{
		String path = SymbolUtil.getPresentablePath(method);
		return getParamDefs(path);
	}

	public ParamDef[] getParamDefs(final String methodFQN)
	{
		return myParamDefs.get(methodFQN);
	}
}
