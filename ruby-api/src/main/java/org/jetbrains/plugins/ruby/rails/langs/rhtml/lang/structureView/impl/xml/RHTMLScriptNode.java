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

import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NonNls;
import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rhtmlRoot.RHTMLRubyInjectionTag;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rhtmlRoot.RHTMLRubyInjectionTagImpl;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 21.05.2007
 */
public class RHTMLScriptNode extends PsiTreeElementBase<RHTMLRubyInjectionTag>
{
	public static final String RHTML_PREFIX = "rhtml:";

	public RHTMLScriptNode(final RHTMLRubyInjectionTagImpl element)
	{
		super(element);
	}

	@Override
	@NonNls
	public String getPresentableText()
	{
		return RHTML_PREFIX;
	}

	@Override
	public String getLocationString()
	{
		final RHTMLRubyInjectionTag element = getElement();
		return element != null ? element.getTagText().trim() : TextUtil.EMPTY_STRING;
	}

	@Override
	@Nonnull
	public Collection<StructureViewTreeElement> getChildrenBase()
	{
		return Collections.emptyList();
	}
}
