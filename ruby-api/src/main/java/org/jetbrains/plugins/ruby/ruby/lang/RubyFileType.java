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

package org.jetbrains.plugins.ruby.ruby.lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import consulo.ruby.api.icon.RubyApiIconGroup;
import consulo.ui.image.Image;
import org.jetbrains.plugins.ruby.RBundle;

import javax.annotation.Nonnull;


public class RubyFileType extends LanguageFileType
{
	public static final RubyFileType INSTANCE = new RubyFileType();

	public static final String RUBY_EXTENTIONS = "rb;rbw;rake;";

	private RubyFileType()
	{
		super(RubyLanguage.INSTANCE);
	}

	@Override
	@Nonnull
	public String getId()
	{
		return "RUBY";
	}

	@Override
	@Nonnull
	public String getDescription()
	{
		return RBundle.message("filetype.description.rb");
	}

	@Override
	@Nonnull
	public String getDefaultExtension()
	{
		return "rb";
	}

	@Override
	@Nonnull
	public Image getIcon()
	{
		return RubyApiIconGroup.rubyRubyFile();
	}
}

