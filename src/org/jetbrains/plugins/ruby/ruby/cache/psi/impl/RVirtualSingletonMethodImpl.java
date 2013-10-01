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

package org.jetbrains.plugins.ruby.ruby.cache.psi.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualSingletonMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 2, 2006
 */
public class RVirtualSingletonMethodImpl extends RVirtualMethodImpl implements RVirtualSingletonMethod
{
	public RVirtualSingletonMethodImpl(@NotNull final RVirtualContainer parentContainer, @NotNull final RVirtualName virtualName, @NotNull final List<ArgumentInfo> argumentInfos, final AccessModifier defaultChildAccessModifier, final RFileInfo containingFileInfo)
	{
		super(parentContainer, virtualName, argumentInfos, defaultChildAccessModifier, containingFileInfo);
	}

	@Override
	public void accept(@NotNull RubyVirtualElementVisitor visitor)
	{
		visitor.visitRVirtualSingletonMethod(this);
	}

	@Override
	public StructureType getType()
	{
		return StructureType.SINGLETON_METHOD;
	}
}
