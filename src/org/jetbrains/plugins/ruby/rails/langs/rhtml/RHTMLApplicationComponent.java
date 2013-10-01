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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.codeInsight.completion.RHTMLCompletionData;
import com.intellij.codeInsight.completion.CompletionUtil;
import com.intellij.codeInsight.editorActions.HtmlQuoteHandler;
import com.intellij.codeInsight.editorActions.TypedHandler;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.impl.FileTypeManagerImpl;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 14.04.2007
 */
public class RHTMLApplicationComponent implements ApplicationComponent
{

	@SuppressWarnings({
			"UnusedDeclaration",
			"UnusedParameters"
	})
	//fileTypeManager: to ensure that file types are initialized
	public RHTMLApplicationComponent(final FileTypeManagerImpl fileTypeManager)
	{
		loadRHTML();
	}

	public static void loadRHTML()
	{
		//Element Factory for RHTML lang elements
		// Factory.addElementFactory(new RHTMLElementFactory());

		//Brace Matcher
		//BraceMatchingUtil.registerBraceMatcher(RHTMLFileType.RHTML, new RHTMLBraceMather());

		//Quote Handler
		TypedHandler.registerQuoteHandler(RHTMLFileType.RHTML, new HtmlQuoteHandler());

		//TODO Metadata Bindings

		registerCompletionData();
	}

	/**
	 * Enables autocompletion for HTML and Ruby in RHTML files.
	 */
	private static void registerCompletionData()
	{
		CompletionUtil.registerCompletionData(RHTMLFileType.RHTML, new RHTMLCompletionData());
	}

	@Override
	@NonNls
	@NotNull
	public String getComponentName()
	{
		return getClass().getName();
	}

	@Override
	public void initComponent()
	{
		//Do nothing
	}

	@Override
	public void disposeComponent()
	{
		//Do nothing
	}
}
