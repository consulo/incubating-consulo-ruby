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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl;

import gnu.trove.THashSet;

import java.util.Arrays;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementTypeEx;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rhtmlRoot.RHTMLCommentImpl;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rhtmlRoot.RHTMLRubyInjectionTagImpl;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rhtmlRoot.RHTMLXMLFileElement;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rhtmlRoot.RHTMLXmlDocument;
import com.intellij.lang.ASTCompositeFactory;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 14.04.2007
 */
public class RHTMLElementFactory implements ASTCompositeFactory
{
    private final Set<IElementType> ourTypes = new THashSet<IElementType>(Arrays.asList(
            RHTMLElementTypeEx.RHTML_FILE,
            RHTMLElementTypeEx.HTML_TEMPLATE_IN_RHTML_ROOT,

            RHTMLElementType.RHTML_DOCUMENT,

            RHTMLElementType.RHTML_XML_TAG,
            RHTMLElementType.RHTML_COMMENT_ELEMENT //RHTML Comment in RHTML PsiRoot
    ));


	@NotNull
	@Override
	public CompositeElement createComposite(IElementType type)
	{
		if (type == RHTMLElementTypeEx.RHTML_FILE) {
			return new RHTMLXMLFileElement(type);
		}
		if (type == RHTMLElementTypeEx.HTML_TEMPLATE_IN_RHTML_ROOT) {
			return new RHTMLXMLFileElement(type);
		}
		if (type == RHTMLElementType.RHTML_COMMENT_ELEMENT) {
			return new RHTMLCommentImpl();
		}

		if (type == RHTMLElementType.RHTML_XML_TAG) {
			return new RHTMLRubyInjectionTagImpl();
		}

		return new RHTMLXmlDocument();
	}

	@Override
	public boolean apply(@Nullable IElementType type)
	{
		return ourTypes.contains(type);
	}
}
