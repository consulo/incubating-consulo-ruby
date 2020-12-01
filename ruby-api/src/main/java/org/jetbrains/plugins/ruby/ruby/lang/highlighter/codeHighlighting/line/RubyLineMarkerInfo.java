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

package org.jetbrains.plugins.ruby.ruby.lang.highlighter.codeHighlighting.line;

import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.SeparatorPlacement;
import com.intellij.openapi.editor.markup.TextAttributes;
import consulo.ui.color.ColorValue;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 25, 2007
 */
public class RubyLineMarkerInfo
{
	public final int startOffset;
	public final boolean isSlow;
	public TextAttributes attributes;
	public ColorValue separatorColor;
	public SeparatorPlacement separatorPlacement;
	public RangeHighlighter highlighter;


	public RubyLineMarkerInfo(final int offset, final boolean slow)
	{
		startOffset = offset;
		isSlow = slow;
	}

	public GutterIconRenderer createGutterRenderer()
	{
		return null;
	}

}
