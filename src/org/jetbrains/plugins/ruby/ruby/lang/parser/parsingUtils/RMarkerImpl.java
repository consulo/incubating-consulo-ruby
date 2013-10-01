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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 10.08.2006
 */
public class RMarkerImpl implements RMarker
{
	private PsiBuilder.Marker myOriginalMarker;
	private RBuilder myBuilder;

	public RMarkerImpl(final RBuilder builder, final PsiBuilder.Marker marker)
	{
		myBuilder = builder;
		myOriginalMarker = marker;
	}

	@Override
	@NotNull
	public RMarker precede()
	{
		return new RMarkerImpl(myBuilder, myOriginalMarker.precede());
	}

	@Override
	public void drop()
	{
		myOriginalMarker.drop();
	}

	@Override
	public void rollbackTo()
	{
		myOriginalMarker.rollbackTo();
		myBuilder.initNextTokens();
	}

	@Override
	public void done(final IElementType type)
	{
		myOriginalMarker.done(type);
	}

	@Override
	public void error(final String message)
	{
		myOriginalMarker.error(message);
	}
}
