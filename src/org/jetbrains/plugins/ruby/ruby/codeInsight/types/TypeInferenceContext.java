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

package org.jetbrains.plugins.ruby.ruby.codeInsight.types;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.RControlFlowOwner;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import com.intellij.util.containers.WeakHashMap;

/**
 * @author: oleg
 */
public class TypeInferenceContext
{
	public static final int MAX_DEPTH = 3;

	public TypeInferenceContext(final FileSymbol fs)
	{
		fileSymbol = fs;
		localVariablesTypesCache = new WeakHashMap<RControlFlowOwner, Map<RIdentifier, RType>>();
		methodsBeingInferred = new HashSet<RControlFlowOwner>();
		expressionsBeingInferred = new HashSet<RExpression>();
		depth = 0;
	}

	public FileSymbol fileSymbol;
	public WeakHashMap<RControlFlowOwner, Map<RIdentifier, RType>> localVariablesTypesCache;

	// Use these to prevent infinite execution of type inference mechanism
	public Set<RControlFlowOwner> methodsBeingInferred;
	public Set<RExpression> expressionsBeingInferred;

	public int depth;
}
