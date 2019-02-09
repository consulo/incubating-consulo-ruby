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

package org.jetbrains.plugins.ruby.support.ui.checkableDir;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.settings.SettingsExternalizer;
import com.intellij.util.xmlb.annotations.Property;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jan 19, 2008
 */

public class CheckableDirectoriesContainer implements Cloneable, Serializable
{
	//    private static final Logger LOG = Logger.getInstance(CheckableDirectoriesContainer.class.getName());

	private LinkedList<CheckableDirectoryItem> myDirectories;
	@NonNls
	@Property
	public static final String NUMBER = "NUMBER";
	@NonNls
	public static final String DIRECTORY = "DIR";
	@NonNls
	public static final String CHECKED = "CHECKED";

	public CheckableDirectoriesContainer()
	{
		myDirectories = new LinkedList<CheckableDirectoryItem>();
	}

	/**
	 * Adds directory for time O(list length)
	 *
	 * @param directory Direcotry
	 * @return false if direcotry is already exists otherwise true
	 */
	public boolean addCheckableDir(@Nonnull final CheckableDirectoryItem directory)
	{
		if(containsDirectoryPath(directory))
		{
			return false;
		}
		myDirectories.add(directory);
		return true;
	}

	public boolean containsDirectoryPath(@Nonnull final CheckableDirectoryItem directory)
	{
		return getDirByPath(directory.getDirectoryPath()) != null;
	}

	@Nonnull
	public List<CheckableDirectoryItem> getCheckableDirectories()
	{
		return myDirectories;
	}

	/**
	 * Clears current directories lists. And loads new from options map.
	 *
	 * @param optionsByName Options map
	 */
	public void loadCheckableDirectores(@Nonnull final Map<String, String> optionsByName)
	{
		removeAll();

		final String sNumber = optionsByName.get(NUMBER);
		if(sNumber == null)
		{
			return;
		}

		final int number = Integer.parseInt(sNumber);
		for(int i = 0; i < number; i++)
		{
			final boolean checked = Boolean.valueOf(optionsByName.get(CHECKED + i));
			final String dir = optionsByName.get(DIRECTORY + i);
			addCheckableDir(new CheckableDirectoryItem(dir, checked));
		}
	}

	public void stateChanged(@Nonnull final String path, final boolean isChecked)
	{
		final List<CheckableDirectoryItem> list = getCheckableDirectories();
		for(CheckableDirectoryItem directoryItem : list)
		{
			if(path.equals(directoryItem.getDirectoryPath()))
			{
				directoryItem.setChecked(isChecked);
				return;
			}
		}
	}

	public void writeCheckableDirectores(@Nonnull final Element elem, @Nonnull final SettingsExternalizer ext)
	{
		final List<CheckableDirectoryItem> dirs = getCheckableDirectories();
		ext.writeOption(NUMBER, Integer.toString(dirs.size()), elem);
		for(int i = 0; i < dirs.size(); i++)
		{
			ext.writeOption(CHECKED + i, Boolean.toString(dirs.get(i).isChecked()), elem);
			ext.writeOption(DIRECTORY + i, dirs.get(i).getDirectoryPath(), elem);
		}
	}

	public void removeAll()
	{
		getCheckableDirectories().clear();
	}

	public void removeDirByPath(@Nonnull final String path)
	{
		final List<CheckableDirectoryItem> list = getCheckableDirectories();

		final CheckableDirectoryItem directoryItem = getDirByPath(path);
		if(directoryItem != null)
		{
			list.remove(directoryItem);
		}
	}

	@Nullable
	public CheckableDirectoryItem getDirByPath(@Nonnull final String path)
	{
		final List<CheckableDirectoryItem> list = getCheckableDirectories();

		for(CheckableDirectoryItem directoryItem : list)
		{
			if(path.equals(directoryItem.getDirectoryPath()))
			{
				return directoryItem;
			}
		}
		return null;
	}

	@Override
	@SuppressWarnings({"CloneDoesntCallSuperClone"})
	@Nonnull
	public CheckableDirectoriesContainer clone() throws CloneNotSupportedException
	{
		final CheckableDirectoriesContainer copy = new CheckableDirectoriesContainer();
		final List<CheckableDirectoryItem> list = getCheckableDirectories();
		for(CheckableDirectoryItem item : list)
		{
			copy.addCheckableDir(new CheckableDirectoryItem(item.getDirectoryPath(), item.isChecked()));
		}

		return copy;
	}
}
