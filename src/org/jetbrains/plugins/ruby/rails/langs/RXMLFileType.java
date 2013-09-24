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
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 17.10.2006
 */
public class RXMLFileType  extends LanguageFileType {
    public static final String VALID_EXTENTIONS = "rxml;builder;";

    public static final RXMLFileType RXML = new RXMLFileType();
    @NonNls private static final String NAME = "RXML";
    @NonNls private static final String DESCRIPTION = RBundle.message("filetype.description.rxml");
    @NonNls private static final String DEFAULT_EXTENSION = "rxml";
    @NonNls private static final String BUILDER_EXTENSION = "builder";
    private static final Icon ICON = RailsIcons.RXTML_ICON;

    private RXMLFileType() {
        super(RubyFileType.RUBY.getLanguage());
    }

    @NotNull
    public String getName() {
        return NAME;
    }

    @NotNull
    public String getDescription() {
        return DESCRIPTION;
    }

    @NotNull
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @NotNull
    public String getBuilderExtension() {
        return BUILDER_EXTENSION; 
    }

    @Nullable
    public Icon getIcon() {
        return ICON;
    }
}

