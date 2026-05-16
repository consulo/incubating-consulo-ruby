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

package org.jetbrains.plugins.ruby.ruby.pom.impl;

import java.util.Collections;

import consulo.annotation.component.ExtensionImpl;
import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;

import consulo.application.progress.ProgressManager;
import consulo.language.pom.PomModel;
import consulo.language.pom.PomModelAspect;
import consulo.language.pom.PomModelAspectRegistrator;
import consulo.language.pom.TreeAspect;
import consulo.language.pom.event.PomModelEvent;
import consulo.language.pom.event.TreeChangeEvent;
import consulo.language.psi.PsiFile;
import consulo.project.Project;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.pom.RubyPomAspect;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 03.10.2006
 */
@ExtensionImpl
public class RubyPomAspectImpl implements RubyPomAspect
{
	private final PomModel myModel;
	private final TreeAspect myTreeAspect;


	@Inject
	public RubyPomAspectImpl(final PomModel model, final TreeAspect treeAspect, final Project project)
	{
		myModel = model;
		myTreeAspect = treeAspect;
	}

	@Override
	public void register(PomModelAspectRegistrator registrator)
	{
		registrator.register(RubyPomAspect.class, this, Collections.singleton((PomModelAspect) myTreeAspect));
	}

	@NonNls
	@Nonnull
	public String getComponentName()
	{
		return RComponents.RUBY_ASPECT;
	}

	public void initComponent()
	{
	}

	public void projectOpened()
	{
	}


	public void projectClosed()
	{
	}


	@Override
	public void update(PomModelEvent event)
	{
		// we hope it`s enough often operation
		ProgressManager.getInstance().checkCanceled();

		if(!event.getChangedAspects().contains(myTreeAspect))
		{
			return;
		}

		final TreeChangeEvent changeSet = (TreeChangeEvent) event.getChangeSet(myTreeAspect);
		if(changeSet == null)
		{
			return;
		}

		final ASTNode rootElement = changeSet.getRootElement();
		final PsiFile file = (PsiFile) rootElement.getPsi();
		if(!(file instanceof RFile))
		{
			return;
		}
		final RubyChangeSetImpl rubyChangeSet = new RubyChangeSetImpl(myModel, (RFile) file);
		final RubyAspectVisitor visitor = new RubyAspectVisitor(rubyChangeSet);
		final ASTNode[] changedElements = changeSet.getChangedElements();
		for(ASTNode changedASTNode : changedElements)
		{
			final PsiElement psiElement = changedASTNode.getPsi();
			assert psiElement != null;
			// We use RubyAspectVisitor only for Ruby changes
			if(psiElement.getLanguage() == RubyLanguage.INSTANCE)
			{
				if(!visitor.isChangeFound())
				{
					psiElement.accept(visitor);
				}
			}
		}
		if(rubyChangeSet.getChanges().size() > 0)
		{
			event.registerChangeSet(this, rubyChangeSet);
		}
	}

	public void disposeComponent()
	{
	}

}
