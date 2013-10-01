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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree;

import org.jetbrains.plugins.ruby.ruby.lang.psi.PresentableElementType;
import com.intellij.psi.tree.xml.IXmlElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 18, 2007
 */
public class IXMLRHTMLElementType extends IXmlElementType implements PresentableElementType, IRHTMLElement
{
	private String myName;

	public IXMLRHTMLElementType(String debugName)
	{
		this(debugName, debugName);
	}

	public IXMLRHTMLElementType(final String debugName, final String presentableName)
	{
		super(debugName);
		myName = presentableName;
	}


	@Override
	public String getPresentableName()
	{
		return myName;
	}
}
