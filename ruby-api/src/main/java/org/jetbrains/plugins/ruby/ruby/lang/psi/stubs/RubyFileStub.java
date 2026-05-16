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

package org.jetbrains.plugins.ruby.ruby.lang.psi.stubs;

import consulo.language.psi.stub.PsiFileStub;
import jakarta.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;

import java.util.List;

public interface RubyFileStub extends PsiFileStub<RFile>
{
	/**
	 * URLs gathered from all {@code require '...'} / {@code load '...'} calls in this file.
	 * Stored in the stub so symbol resolution can avoid loading the AST.
	 */
	@Nonnull
	List<String> getRequiredUrls();
}
