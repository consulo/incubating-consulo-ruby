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

package org.jetbrains.plugins.ruby.ruby.cache.listeners;

import java.util.List;

import org.jetbrains.plugins.ruby.ruby.pom.RubyChange;
import org.jetbrains.plugins.ruby.ruby.pom.RubyChangeSet;
import org.jetbrains.plugins.ruby.ruby.pom.RubyPomAspect;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.PomModel;
import com.intellij.pom.PomModelAspect;
import com.intellij.pom.event.PomChangeSet;
import com.intellij.pom.event.PomModelEvent;
import com.intellij.pom.event.PomModelListener;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 2, 2006
 */
public abstract class RubyPomModelListener implements PomModelListener
{
	private Module myModule;
	private PomModel myPomModel;
	private ProjectFileIndex myFileIndex;

	public RubyPomModelListener(final Module module, final PomModel pomModel)
	{
		myModule = module;
		myPomModel = pomModel;
		myFileIndex = ProjectRootManager.getInstance(myModule.getProject()).getFileIndex();
	}

	@Override
	public boolean isAspectChangeInteresting(PomModelAspect aspect)
	{
		return aspect instanceof RubyPomAspect;
	}

	@Override
	public void modelChanged(final PomModelEvent event)
	{
		final PomChangeSet changeSet = event.getChangeSet(myPomModel.getModelAspect(RubyPomAspect.class));
		if(changeSet != null)
		{
			final List<RubyChange> list = ((RubyChangeSet) changeSet).getChanges();
			if(list.isEmpty())
			{
				return;
			}
			final VirtualFile vFile = ((RubyChangeSet) changeSet).getChangedFile().getVirtualFile();

			if(vFile == null)
			{
				return;
			}

			if(myFileIndex.getModuleForFile(vFile) == myModule)
			{
				processEvent(list, vFile);
			}
		}
	}

	@SuppressWarnings({"UnusedParameters"})
	protected abstract void processEvent(final List<RubyChange> list, final VirtualFile vFile);
}
