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

package org.jetbrains.plugins.ruby.rails.langs.rhtml;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.eRubyLanguage;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.eRubyElementTypes;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rubyRoot.RHTMLRubyFileImpl;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import lombok.val;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 02.04.2007
 */
public class RHTMLFileViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider implements TemplateLanguageFileViewProvider
{
	private Set<Language> myViews = null;

	public RHTMLFileViewProvider(final PsiManager manager, final VirtualFile virtualFile, final boolean physical)
	{
		super(manager, virtualFile, physical);
	}

	@Override
	@NotNull
	public Language getBaseLanguage()
	{
		return eRubyLanguage.INSTANCE;
	}

	@Override
	@NotNull
	public Language getTemplateDataLanguage()
	{
		return HTMLLanguage.INSTANCE;
	}

	@NotNull
	@Override
	public Set<Language> getLanguages()
	{
		if(myViews != null)
		{
			return myViews;
		}
		Set<Language> views = new HashSet<Language>(4);
		views.add(eRubyLanguage.INSTANCE);
		views.add(RubyLanguage.INSTANCE);
		views.add(HTMLLanguage.INSTANCE);

		return myViews = views;
	}

	@Override
	protected MultiplePsiFilesPerDocumentFileViewProvider cloneInner(final VirtualFile copy)
	{
		return new RHTMLFileViewProvider(getManager(), copy, false);
	}

	@Override
	protected PsiFile createFile(final Language lang)
	{
		if(lang == RubyLanguage.INSTANCE)
		{
			val ruby = new RHTMLRubyFileImpl(this);
			ruby.setOriginalFile(getPsi(eRubyLanguage.INSTANCE));
			return ruby;
		}
		else if(lang == HTMLLanguage.INSTANCE)
		{
			ParserDefinition parserDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(HTMLLanguage.INSTANCE);

			PsiFileImpl file = (PsiFileImpl) parserDefinition.createFile(this);
			file.setContentElementType(eRubyElementTypes.TEMPLATE_DATA);
			return file;
		}
		else if(lang == eRubyLanguage.INSTANCE)
		{
			val def = LanguageParserDefinitions.INSTANCE.forLanguage(lang);

			return def.createFile(this);
		}
		return null;
	}
}
