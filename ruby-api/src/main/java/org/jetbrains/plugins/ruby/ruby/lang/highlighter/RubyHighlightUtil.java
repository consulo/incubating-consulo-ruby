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

package org.jetbrains.plugins.ruby.ruby.lang.highlighter;

import java.awt.Point;
import java.awt.Rectangle;

import javax.annotation.Nonnull;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, oleg
 * @date: 02.02.2007
 */
public class RubyHighlightUtil
{
	/**
	 * @param element PsiElement to get start offset
	 * @return Start Offset of given element
	 */
	public static int getStartOffset(@Nonnull final PsiElement element)
	{
		return element.getTextRange().getStartOffset();
	}

	/**
	 * Returns visible Range of editor
	 *
	 * @param editor Editor to get visible area
	 * @return TextRange - the visible area
	 */
	public static TextRange getVisibleRange(@Nonnull final Editor editor)
	{
		final Rectangle rect = editor.getScrollingModel().getVisibleArea();
		final LogicalPosition startPosition = editor.xyToLogicalPosition(new Point(rect.x, rect.y));
		final LogicalPosition endPosition = editor.xyToLogicalPosition(new Point(rect.x + rect.width, rect.y + rect.height));

		final int visibleStart = editor.logicalPositionToOffset(startPosition);
		final int visibleEnd = editor.logicalPositionToOffset(new LogicalPosition(endPosition.line + 1, 0));
		return new TextRange(visibleStart, visibleEnd);
	}
}
