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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.modules;

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.util.IncorrectOperationException;
import consulo.navigation.ItemPresentation;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RModuleName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.RFieldConstantContainerImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.RubyModuleStub;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import org.jetbrains.plugins.ruby.ruby.presentation.RModulePresentationUtil;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class RModuleImpl extends RFieldConstantContainerImpl<RubyModuleStub> implements RModule
{
	public RModuleImpl(ASTNode astNode)
	{
		super(astNode);
	}

	public RModuleImpl(@Nonnull RubyModuleStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public String getName()
	{
		final RubyModuleStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getName();
		}
		return super.getName();
	}

	@Override
	public void accept(@Nonnull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRModule(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	@Nonnull
	public ItemPresentation getPresentation()
	{
		return RModulePresentationUtil.getPresentation(this);
	}

	@Nullable
	public Image getIcon(final int flags)
	{
		return RModulePresentationUtil.getIcon(this, flags);
	}

	@Override
	@Nullable
	public RModuleName getModuleName()
	{
		return getChildByType(RModuleName.class, 0);
	}

	@Override
	public PsiElement setName(@NonNls @Nonnull String name) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public int getTextOffset()
	{
		final RModuleName moduleName = getModuleName();
		return moduleName != null ? moduleName.getTextOffset() : super.getTextOffset();
	}

	@Override
	public StructureType getType()
	{
		return StructureType.MODULE;
	}

	@Override
	protected RPsiElement getNameElement()
	{
		return getModuleName();
	}
}
