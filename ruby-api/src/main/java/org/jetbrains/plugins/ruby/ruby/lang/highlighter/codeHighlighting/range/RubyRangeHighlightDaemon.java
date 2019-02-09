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

package org.jetbrains.plugins.ruby.ruby.lang.highlighter.codeHighlighting.range;

import javax.annotation.Nullable;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 26, 2007
 */
public class RubyRangeHighlightDaemon
{
	private static final Key<HighlightInfo[]> RUBY_HIGHLIGHT_INFO = Key.create("RUBY_HIGHLIGHT_INFO");

	@Nullable
	public static HighlightInfo[] getHighlightInfos(final Document document, final Project project)
	{
		ApplicationManager.getApplication().assertIsDispatchThread();
		MarkupModel markup = DocumentMarkupModel.forDocument(document, project, false);
		return markup.getUserData(RUBY_HIGHLIGHT_INFO);
	}

	public static void setHighligthInfos(final Document document, final HighlightInfo[] infos, final Project project)
	{
		ApplicationManager.getApplication().assertIsDispatchThread();
		MarkupModel markup = DocumentMarkupModel.forDocument(document, project, false);
		markup.putUserData(RUBY_HIGHLIGHT_INFO, infos);
	}
}
