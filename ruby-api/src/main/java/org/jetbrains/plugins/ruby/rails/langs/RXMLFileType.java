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

import com.intellij.openapi.fileTypes.LanguageFileType;
import consulo.localize.LocalizeValue;
import consulo.ruby.api.localize.RubyApiLocalize;
import consulo.ui.image.Image;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 17.10.2006
 */
public class RXMLFileType extends LanguageFileType
{
	public static final String VALID_EXTENTIONS = "rxml;builder;";

	public static final RXMLFileType RXML = new RXMLFileType();

	private static final String BUILDER_EXTENSION = "builder";

	private RXMLFileType()
	{
		super(RubyFileType.INSTANCE.getLanguage());
	}

	@Override
	@Nonnull
	public String getId()
	{
		return "RXML";
	}

	@Override
	@Nonnull
	public LocalizeValue getDescription()
	{
		return RubyApiLocalize.filetypeDescriptionRxml();
	}

	@Override
	@Nonnull
	public String getDefaultExtension()
	{
		return "rxml";
	}

	@Nonnull
	public String getBuilderExtension()
	{
		return BUILDER_EXTENSION;
	}

	@Override
	@Nullable
	public Image getIcon()
	{
		return RailsIcons.RXTML_ICON;
	}
}

