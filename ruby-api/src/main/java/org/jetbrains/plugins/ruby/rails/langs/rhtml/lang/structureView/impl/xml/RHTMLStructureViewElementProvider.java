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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.structureView.impl.xml;

import consulo.annotation.component.ExtensionImpl;
import consulo.fileEditor.structureView.StructureViewTreeElement;
import consulo.xml.ide.structureView.xml.XmlStructureViewElementProvider;
import consulo.xml.language.psi.XmlTag;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rhtmlRoot.RHTMLRubyInjectionTag;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rhtmlRoot.RHTMLRubyInjectionTagImpl;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 21.05.2007
 */
@ExtensionImpl
public class RHTMLStructureViewElementProvider implements XmlStructureViewElementProvider
{
	@Override
	@Nullable
	public StructureViewTreeElement createCustomXmlTagTreeElement(@Nonnull final XmlTag tag)
	{
		if(tag instanceof RHTMLRubyInjectionTag)
		{
			return new RHTMLScriptNode((RHTMLRubyInjectionTagImpl) tag);
		}
		return null;
	}
}
