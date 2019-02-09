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

package org.jetbrains.plugins.ruby.ruby.lang.psi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RFieldConstantContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RGlobalVarHolder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.psi.PsiFile;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.07.2006
 */
public interface RFile extends RVirtualFile, RFieldConstantContainer, RGlobalVarHolder, PsiFile
{

	@Nonnull
	public RCompoundStatement getCompoundStatement();

	@Override
	@Nonnull
	public RVirtualFile createVirtualCopy(@Nullable RVirtualContainer virtualParent, @Nonnull RFileInfo fileInfo);

	@Override
	@Nullable
	public RContainer getParentContainer();

	@Override
	@Nullable
	public RFileInfo getContainingFileInfo();

	@Nullable
	public Module getModule();

	/**
	 * @return Sdk, for given file. It`s module sdk if file is a part of any module,
	 *         sdk if the file if a part of this sdk or null otherwise
	 */
	@Nullable
	public Sdk getSdk();

	@Nullable
	public FileSymbol getFileSymbol();

	public boolean isJRubyEnabled();
}
