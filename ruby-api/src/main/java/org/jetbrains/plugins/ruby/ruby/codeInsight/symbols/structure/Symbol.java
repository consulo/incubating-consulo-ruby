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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualElementBase;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data.Children;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data.Prototypes;
import org.jetbrains.plugins.ruby.ruby.lang.documentation.MarkupUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import com.intellij.openapi.project.Project;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Oct 15, 2007
 */
public class Symbol
{
	private static long currentID = 0;
	private long myId = 0;

	protected final String myName;
	protected Type myType;

	private Symbol myParentSymbol;
	private Project myProject;
	private Symbol myRootSymbol;


	public Symbol(@Nonnull final Project project, @Nullable final String name, @Nonnull final Type type, @Nullable final Symbol parent, @Nullable final RVirtualElement prototype)
	{
		this(project, null, name, type, parent, prototype);
	}

	public Symbol(@Nonnull final FileSymbol fileSymbol, @Nullable final String name, @Nonnull final Type type, @Nullable final Symbol parent, @Nullable final RVirtualElement prototype)
	{
		this(fileSymbol.getProject(), fileSymbol, name, type, parent, prototype);
	}

	private Symbol(@Nonnull final Project project, @Nullable final FileSymbol fileSymbol, @Nullable final String name, @Nonnull final Type type, @Nullable final Symbol parent, @Nullable final RVirtualElement prototype)
	{
		myId = currentID++;
		myProject = project;
		myName = name;
		myType = type;
		myParentSymbol = parent;
		if(fileSymbol != null)
		{
			myRootSymbol = fileSymbol.getRootSymbol();
			if(prototype != null)
			{
				fileSymbol.addPrototype(this, prototype);
			}
		}
	}

	@Nullable
	public String getName()
	{
		return myName;
	}

	@Nonnull
	public Type getType()
	{
		return myType;
	}

	public void setType(@Nonnull final Type type)
	{
		myType = type;
	}

	public static void resetIdCounter()
	{
		currentID = 0;
	}

	public long getId()
	{
		return myId;
	}

	@Nonnull
	public Symbol getLinkedSymbol()
	{
		return this;
	}


	@Nonnull
	public Project getProject()
	{
		return myProject;
	}

	@Nullable
	public Symbol getParentSymbol()
	{
		return myParentSymbol;
	}

	@Nullable
	public Symbol getRootSymbol()
	{
		return myRootSymbol;
	}

	public void setRootSymbol(@Nonnull final Symbol rootSymbol)
	{
		assert myType == Type.FILE;
		myRootSymbol = rootSymbol;
	}


	@Nullable
	public RVirtualElement getLastVirtualPrototype(@Nullable final FileSymbol fileSymbol)
	{
		return fileSymbol != null ? fileSymbol.getLastVirualPrototype(this) : null;
	}

	@Nonnull
	public Prototypes getVirtualPrototypes(@Nullable final FileSymbol fileSymbol)
	{
		return fileSymbol != null ? fileSymbol.getVirtualPrototypes(this) : Prototypes.EMPTY_PROTOTYPES;
	}

	@Nonnull
	public Children getChildren(@Nullable final FileSymbol fileSymbol)
	{
		return fileSymbol != null ? fileSymbol.getChildren(this) : Children.EMPTY_CHILDREN;
	}

	public String toString(@Nonnull final FileSymbol fileSymbol, final boolean useHtml)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("[").append(myId).append("] ");
		builder.append(myType);
		if(myName != null)
		{
			builder.append(" ");
			if(useHtml)
			{
				MarkupUtil.appendBold(builder, myName);
			}
			else
			{
				builder.append(myName);
			}
		}
		final Prototypes prototypes = fileSymbol.getVirtualPrototypes(this);
		if(myType != Type.FILE && prototypes.hasElements())
		{
			final List<RVirtualElement> prototypesList = prototypes.getAll();
			builder.append(" prototypes: ");

			if(useHtml)
			{
				builder.append(prototypesList.size());
			}
			else
			{
				for(RVirtualElement element : prototypesList)
				{
					if(element instanceof RVirtualElementBase)
					{
						builder.append(" ").append(((RVirtualElementBase) element).getId());
					}
					else if(element instanceof RPsiElement)
					{
						builder.append(" ").append(RubyPsiUtil.getPresentableName((RPsiElement) element));
					}
					else
					{
						assert false : "wrong prototype" + element;
					}
				}
			}
		}
		return builder.toString();
	}

	public String toString()
	{
		return myType + " : " + myName;
	}

}
