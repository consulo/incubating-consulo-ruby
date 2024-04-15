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

package org.jetbrains.plugins.ruby.ruby.actions.intention;

import consulo.codeEditor.Editor;
import consulo.language.editor.intention.IntentionAction;
import consulo.language.psi.PsiFile;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * This is hack for classLoader problem. See RUBY-1143 for more details
 *
 * @author: oleg
 * @date: Nov 7, 2007
 */
public class RubyIntentionActionClassLoaderHack implements IntentionAction
{
	private final IntentionAction myOriginalIntentionAction;

	public RubyIntentionActionClassLoaderHack(@Nonnull final IntentionAction originalIntentionAction)
	{
		myOriginalIntentionAction = originalIntentionAction;
	}

	@Override
	@Nonnull
	public String getText()
	{
		return myOriginalIntentionAction.getText();
	}

	@Override
	public boolean isAvailable(@Nonnull Project project, Editor editor, PsiFile file)
	{
		return RubyIntentionUtil.isAvailable(editor, file) && myOriginalIntentionAction.isAvailable(project, editor, file);
	}

	@Override
	public void invoke(@Nonnull Project project, Editor editor, PsiFile file) throws IncorrectOperationException
	{
		myOriginalIntentionAction.invoke(project, editor, file);
	}

	@Override
	public boolean startInWriteAction()
	{
		return myOriginalIntentionAction.startInWriteAction();
	}
}
