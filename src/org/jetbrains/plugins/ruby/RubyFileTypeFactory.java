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

package org.jetbrains.plugins.ruby;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.langs.RJSFileType;
import org.jetbrains.plugins.ruby.rails.langs.RXMLFileType;
import org.jetbrains.plugins.ruby.rails.langs.YAMLFileType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileType;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 4, 2008
 */
public class RubyFileTypeFactory extends FileTypeFactory {
	@Override
	public void createFileTypes(@NotNull FileTypeConsumer consumer)
	{
		// Loading Ruby file type
		consumer.consume(RubyFileType.RUBY, RubyFileType.RUBY_EXTENTIONS);
		// Loading rails related types
		consumer.consume(YAMLFileType.YML, YAMLFileType.YML.getDefaultExtension());
		consumer.consume(RJSFileType.RJS, RJSFileType.RJS.getDefaultExtension());
		consumer.consume(RHTMLFileType.RHTML, RHTMLFileType.VALID_EXTENTIONS);
		consumer.consume(RXMLFileType.RXML, RXMLFileType.VALID_EXTENTIONS);
	}
}