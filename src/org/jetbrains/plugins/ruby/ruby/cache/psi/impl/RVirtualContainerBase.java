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

package org.jetbrains.plugins.ruby.ruby.cache.psi.impl;

import java.io.Serializable;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: Oct 2, 2006
 */

/**
 * RVirtualContainerImpl is an implemetation of RContainer interface
 * To store the information about all the containers in the file.
 */
public abstract class RVirtualContainerBase extends RVirtualStructuralElementBase implements RVirtualContainer, Serializable
{
	private List<RVirtualStructuralElement> myStructureElements;
	private AccessModifier myAccessModifier = AccessModifier.PUBLIC;
	private RFileInfo myContainingFileInfo;
	private RVirtualName myName;


	public RVirtualContainerBase(@Nullable final RVirtualContainer container, @NotNull final RVirtualName name, final AccessModifier accessModifier, @NotNull final RFileInfo containingFileInfo)
	{

		super(container);
		myName = name;
		myAccessModifier = accessModifier;
		myContainingFileInfo = containingFileInfo;
	}


	@NotNull
	public RVirtualName getVirtualName()
	{
		return myName;
	}

	@Override
	@NotNull
	public String getName()
	{
		return myName.getName();
	}

	@Override
	@NotNull
	public List<String> getFullPath()
	{
		return myName.getPath();
	}

	@Override
	@NotNull
	public String getFullName()
	{
		return myName.getFullName();
	}

	@Override
	public boolean isGlobal()
	{
		return myName.isGlobal();
	}

	@Override
	@NotNull
	final public List<RVirtualStructuralElement> getVirtualStructureElements()
	{
		return myStructureElements;
	}

	final public void setStructureElements(List<RVirtualStructuralElement> elements)
	{
		myStructureElements = elements;
	}

	@Override
	@NotNull
	public AccessModifier getDefaultChildAccessModifier()
	{
		return AccessModifier.PUBLIC;
	}

	@Override
	@NotNull
	public AccessModifier getAccessModifier()
	{
		return myAccessModifier;
	}

	@Override
	@NotNull
	public RFileInfo getContainingFileInfo()
	{
		return myContainingFileInfo;
	}

	@Override
	@NotNull
	public String getContainingFileUrl()
	{
		return myContainingFileInfo.getUrl();
	}

	@Override
	@Nullable
	public VirtualFile getVirtualFile()
	{
		return getContainingFileInfo().getVirtualFile();
	}

	@Override
	public void dump(@NotNull StringBuilder buffer, final int indent)
	{
		super.dump(buffer, indent);
		for(RVirtualStructuralElement mySubContainer : myStructureElements)
		{
			buffer.append(NEW_LINE);
			((RVirtualElementBase) mySubContainer).dump(buffer, indent + 1);
		}
	}

	@Override
	public int getIndexOf(@NotNull RVirtualStructuralElement element)
	{
		for(int i = 0; i < myStructureElements.size(); i++)
		{
			if(element == myStructureElements.get(i))
			{
				return i;
			}
		}
		return -1;
	}

	@Override
	public Project getProject()
	{
		return getContainingFileInfo().getProject();
	}
}
