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

package org.jetbrains.plugins.ruby.rails.langs;

import javax.swing.Icon;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 21.03.2007
 */
public class RJSFileType extends LanguageFileType
{
	public static final RJSFileType RJS = new RJSFileType();
	@NonNls
	private static final String DESCRIPTION = RBundle.message("filetype.description.rjs");
	@NonNls
	private static final String DEFAULT_EXTENSION = "rjs";
	@NonNls
	private static final String NAME = "Rjs";
	private static final Image ICON = RailsIcons.RJS_ICON;

	private RJSFileType()
	{
		super(RubyFileType.INSTANCE.getLanguage());
	}

	@Override
	@NotNull
	public String getName()
	{
		return NAME;
	}

	@Override
	@NotNull
	public String getDescription()
	{
		return DESCRIPTION;
	}

	@Override
	@NotNull
	public String getDefaultExtension()
	{
		return DEFAULT_EXTENSION;
	}

	@Override
	@Nullable
	public Image getIcon()
	{
		return ICON;
	}
}
