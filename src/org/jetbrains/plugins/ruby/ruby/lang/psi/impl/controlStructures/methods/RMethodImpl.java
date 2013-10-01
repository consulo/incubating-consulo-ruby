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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods;

import java.util.Collections;
import java.util.List;

import javax.swing.Icon;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVMethodName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualMethodImpl;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RBodyStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RArgumentList;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RMethodName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.RContainerBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import org.jetbrains.plugins.ruby.ruby.presentation.RMethodPresentationUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.RPresentationConstants;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class RMethodImpl extends RContainerBase implements RMethod
{
	private static final TokenSet TS_ARGUMENT_LISTS = TokenSet.create(RubyElementTypes.FUNCTION_ARGUMENT_LIST, RubyElementTypes.COMMAND_ARGUMENT_LIST);
	private boolean isClassConstructor;

	public RMethodImpl(ASTNode astNode)
	{
		super(astNode);
		updateIfIsConstructor();
	}

	private void updateIfIsConstructor()
	{
		if(INITIALIZE.equals(getName()))
		{
			if(isClassConstructor)
			{
				return;
			}
			if(isClassConstructor = RContainerUtil.belongsToRContainer(this, StructureType.CLASS))
			{
				super.setAccessModifier(AccessModifier.PRIVATE);
			}
		}
		else
		{
			isClassConstructor = false;
		}
	}

	@Override
	@NotNull
	public AccessModifier getAccessModifier()
	{
		updateIfIsConstructor();
		if(isClassConstructor)
		{
			return AccessModifier.PRIVATE;
		}
		return super.getAccessModifier();
	}


	@Override
	public void setAccessModifier(final AccessModifier modifier)
	{
		updateIfIsConstructor();
		if(isClassConstructor)
		{
			return;
		}
		super.setAccessModifier(modifier);
	}

	@Override
	@Nullable
	public RArgumentList getArgumentList()
	{
		PsiElement argList = getChildByFilter(TS_ARGUMENT_LISTS, 0);
		return argList != null ? (RArgumentList) argList : null;
	}

	@Override
	public void accept(@NotNull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRMethod(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	@NotNull
	public ItemPresentation getPresentation()
	{
		return RMethodPresentationUtil.getPresentation(this);
	}

	@Nullable
	public Icon getIcon(final int flags)
	{
		return RMethodPresentationUtil.getIcon(this, flags);
	}

	@Override
	@Nullable
	public RMethodName getMethodName()
	{
		return getChildByType(RMethodName.class, 0);
	}

	@Override
	@NotNull
	public RVirtualMethod createVirtualCopy(@Nullable final RVirtualContainer virtualParent, @NotNull final RFileInfo info)
	{
		final RVirtualName virtualMethodName = new RVMethodName(getFullPath(), isGlobal());
		final RVirtualMethodImpl vMethod = new RVirtualMethodImpl(virtualParent, virtualMethodName, getArgumentInfos(), getAccessModifier(), info);
		addVirtualData(vMethod, info);
		return vMethod;
	}

	@Override
	@NotNull
	public List<ArgumentInfo> getArgumentInfos()
	{
		final RArgumentList argumentList = getArgumentList();
		if(argumentList == null)
		{
			return Collections.emptyList();
		}
		return argumentList.getArgumentInfos();
	}

	@Override
	public boolean isConstructor()
	{
		updateIfIsConstructor();
		return isClassConstructor;
	}

	@Override
	public boolean equalsToVirtual(@NotNull RVirtualStructuralElement element)
	{
		return element instanceof RVirtualMethod &&
				getArgumentInfos().equals(((RVirtualMethod) element).getArgumentInfos()) &&
				super.equalsToVirtual(element);
	}

	/**
	 * Method assumes that both method have equal parent container
	 *
	 * @param otherMethod other method (virtual or psi)
	 * @return true if methods equals.
	 */
	@Override
	public boolean equalsToMethod(@NotNull final RVirtualMethod otherMethod)
	{
		return RVirtualPsiUtil.areMethodsEqual(this, otherMethod);
	}

	@Override
	@NotNull
	public String getPresentableName()
	{
		final int options = RPresentationConstants.SHOW_NAME | RPresentationConstants.SHOW_PARAMETERS;
		return RMethodPresentationUtil.formatName(this, options);
	}

	@Override
	@NotNull
	public String getPresentableName(final boolean includeDefaultArgs)
	{
		int options = RPresentationConstants.SHOW_NAME | RPresentationConstants.SHOW_PARAMETERS;
		if(includeDefaultArgs)
		{
			options |= RPresentationConstants.SHOW_INITIALIZER;
		}
		return RMethodPresentationUtil.formatName(this, options);
	}

	@Override
	public int getTextOffset()
	{
		final RMethodName methodName = getMethodName();
		return methodName != null ? methodName.getTextOffset() : super.getTextOffset();
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public StructureType getType()
	{
		return StructureType.METHOD;
	}

	@Override
	protected RPsiElement getNameElement()
	{
		return getMethodName();
	}

	@Override
	@NotNull
	public RCompoundStatement getCompoundStatement()
	{
		final RBodyStatement body = RubyPsiUtil.getChildByType(this, RBodyStatement.class, 0);
		assert body != null;
		//noinspection ConstantConditions
		return RubyPsiUtil.getChildByType(body, RCompoundStatement.class, 0);
	}
}
