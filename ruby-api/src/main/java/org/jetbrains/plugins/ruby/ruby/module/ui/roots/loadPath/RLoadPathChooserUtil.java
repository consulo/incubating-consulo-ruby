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

package org.jetbrains.plugins.ruby.ruby.module.ui.roots.loadPath;

import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.JComponent;

import org.jetbrains.plugins.ruby.support.ui.checkableDir.CheckableDirectoriesContainer;
import org.jetbrains.plugins.ruby.support.ui.checkableDir.CheckableDirectoryItem;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jan 20, 2008
 */
public class RLoadPathChooserUtil
{
	private static final Logger LOG = Logger.getInstance(RLoadPathChooserUtil.class.getName());

	public static JComponent createLoadPathPanel(@Nonnull final Module module, @Nonnull final Ref<CheckableDirectoriesContainer> loadPathDirsCopyRef, @Nonnull final CheckableDirectoriesContainer loadPathDirs)
	{
		try
		{
			loadPathDirsCopyRef.set(loadPathDirs.clone());
			return new RLoadPathCooserPanel(module, loadPathDirsCopyRef.get()).getContentPane();
		}
		catch(CloneNotSupportedException e)
		{
			LOG.error(e);
		}
		return null;
	}

	public static boolean loadPathDirsAreModified(@Nonnull final CheckableDirectoriesContainer newLoadPathDirs, @Nonnull final CheckableDirectoriesContainer origLoadPathDirs)
	{
		final List<CheckableDirectoryItem> newItemList = newLoadPathDirs.getCheckableDirectories();
		final List<CheckableDirectoryItem> origItemList = origLoadPathDirs.getCheckableDirectories();
		if(newItemList.size() != origItemList.size())
		{
			return true;
		}
		for(CheckableDirectoryItem item : newItemList)
		{
			final CheckableDirectoryItem origItem = origLoadPathDirs.getDirByPath(item.getDirectoryPath());
			if(origItem == null || item.isChecked() != origItem.isChecked())
			{
				return true;
			}
		}
		return false;
	}
}
