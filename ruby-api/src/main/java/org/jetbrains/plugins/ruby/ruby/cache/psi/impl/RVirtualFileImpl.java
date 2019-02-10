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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualLoad;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualRequire;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualGlobalVar;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.presentation.RFilePresentationUtil;
import com.intellij.navigation.ItemPresentation;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: Oct 2, 2006
 */
public class RVirtualFileImpl extends RVirtualFieldContantContainerImpl implements RVirtualFile
{
	private String myLocation;
	private List<RVirtualRequire> myRequires;
	private List<RVirtualGlobalVar> myGlobalVars;

	public RVirtualFileImpl(final String name, final String location, final RVirtualContainer parentContainer, final AccessModifier defaultChildAccessModifier, @Nonnull final RFileInfo containingFileInfo)
	{
		super(parentContainer, new RVirtualNameImpl(Arrays.asList(name), false), defaultChildAccessModifier, containingFileInfo);
		myLocation = location;
	}

	@Override
	@Nonnull
	public synchronized List<RVirtualRequire> getRequires()
	{
		if(myRequires == null)
		{
			myRequires = new ArrayList<RVirtualRequire>();
			final RubyVirtualElementVisitor visitor = new RubyVirtualElementVisitor()
			{
				@Override
				public void visitElement(RVirtualElement element)
				{
					if(element instanceof RVirtualContainer)
					{
						for(RVirtualStructuralElement child : ((RVirtualContainer) element).getVirtualStructureElements())
						{
							child.accept(this);
						}
					}
				}

				@Override
				public void visitRVirtualRequire(@Nonnull final RVirtualRequire virtualRequire)
				{
					myRequires.add(virtualRequire);
				}

				@Override
				public void visitRVirtualLoad(RVirtualLoad rVirtualLoad)
				{
					myRequires.add(rVirtualLoad);
				}
			};
			accept(visitor);
		}
		return myRequires;
	}


	@Override
	@Nonnull
	public ItemPresentation getPresentation()
	{
		return RFilePresentationUtil.getPresentation(this);
	}

	public Image getIcon(final int flags)
	{
		return RFilePresentationUtil.getIcon();
	}


	@Override
	public void accept(@Nonnull RubyVirtualElementVisitor visitor)
	{
		visitor.visitRVirtualFile(this);
	}

	@Override
	@Nullable
	public String getPresentableLocation()
	{
		return myLocation;
	}

	public String toString()
	{
		return "file [" + ((RVirtualElementBase) getVirtualName()).getId() + "] " + getFullName();
	}

	@Override
	public void dump(@Nonnull StringBuilder buffer, int indent)
	{
		super.dump(buffer, indent);
		for(RVirtualGlobalVar var : myGlobalVars)
		{
			buffer.append(NEW_LINE);
			((RVirtualElementBase) var).dump(buffer, indent + 1);
		}
	}

	@Override
	public StructureType getType()
	{
		return StructureType.FILE;
	}

	// RVirtualGlobalVarsHolder methods
	public void setVirtualGlobalVars(List<RVirtualGlobalVar> vars)
	{
		myGlobalVars = vars;
	}

	@Override
	@Nonnull
	public List<RVirtualGlobalVar> getVirtualGlobalVars()
	{
		return myGlobalVars;
	}

}
