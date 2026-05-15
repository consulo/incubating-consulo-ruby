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

package org.jetbrains.plugins.ruby.ruby.lang.commenter;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Commenter;
import consulo.language.Language;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;


@ExtensionImpl
public class RubyCommenter implements Commenter
{
	private static final String LINE_COMMENT_PREFIX = "#";
	private static final String BLOCK_COMMENT_PREFIX = "\n=begin\n";
	private static final String BLOCK_COMMAND_SUFFIX = "\n=end\n";

	@Override
	@Nullable
	public String getLineCommentPrefix()
	{
		return LINE_COMMENT_PREFIX;
	}

	@Override
	@Nullable
	public String getBlockCommentPrefix()
	{
		return BLOCK_COMMENT_PREFIX;
	}

	@Override
	@Nullable
	public String getBlockCommentSuffix()
	{
		return BLOCK_COMMAND_SUFFIX;
	}

	@Nullable
	@Override
	public String getCommentedBlockCommentPrefix()
	{
		return null;
	}

	@Nullable
	@Override
	public String getCommentedBlockCommentSuffix()
	{
		return null;
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return RubyLanguage.INSTANCE;
	}
}
