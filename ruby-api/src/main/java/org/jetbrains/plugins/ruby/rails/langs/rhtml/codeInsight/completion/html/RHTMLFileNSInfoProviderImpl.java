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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.codeInsight.completion.html;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileType;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlFileNSInfoProvider;
import com.intellij.xml.util.XmlUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 15.02.2007
 */

/**
 * This class adds html tags auto completion for *.rhtml files.
 */
@SuppressWarnings({"ComponentRegistrationProblems"})
public class RHTMLFileNSInfoProviderImpl implements XmlFileNSInfoProvider
{
	private static String[][] NAMESPACES = new String[][]{
			new String[]{
					TextUtil.EMPTY_STRING,
					XmlUtil.XHTML_URI
			}
	};

	@Override
	@Nullable
	public String[][] getDefaultNamespaces(final @Nonnull XmlFile file)
	{
		if(RHTMLFileType.INSTANCE.equals(file.getFileType()))
		{
			return NAMESPACES;
		}
		return null;
	}

	@Override
	public boolean overrideNamespaceFromDocType(@Nonnull XmlFile xmlFile)
	{
		return true;
	}
}