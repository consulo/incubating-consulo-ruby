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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.classes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualObjectClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualNameImpl;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualObjectClassImpl;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RObjectClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RClassObject;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.RFieldConstantContainerImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import org.jetbrains.plugins.ruby.ruby.presentation.RObjectClassPresentationUtil;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class RObjectClassImpl extends RFieldConstantContainerImpl implements RObjectClass
{
	public RObjectClassImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	@Nonnull
	public ItemPresentation getPresentation()
	{
		return RObjectClassPresentationUtil.getPresentation(this);
	}

	public Image getIcon(int flags)
	{
		return RObjectClassPresentationUtil.getIcon();
	}

	@Override
	@Nonnull
	public String getPresentableName()
	{
		return "<<" + getFullName();
	}

	@Override
	@Nullable
	public RClassObject getObject()
	{
		PsiElement result = RubyPsiUtil.getChildByFilter(this, RubyElementTypes.CLASS_OBJECT, 0);
		return result != null ? (RClassObjectImpl) result : null;
	}

	@Override
	public void accept(@Nonnull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRObjectClass(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	@Nonnull
	public RVirtualObjectClass createVirtualCopy(@Nullable final RVirtualContainer virtualParent, @Nonnull RFileInfo info)
	{
		assert virtualParent != null;
		final RVirtualName name = new RVirtualNameImpl(getFullPath(), isGlobal());
		final RVirtualObjectClassImpl vObjectClass = new RVirtualObjectClassImpl(virtualParent, name, getAccessModifier(), info);
		addVirtualData(vObjectClass, info);
		return vObjectClass;
	}

	@Override
	public StructureType getType()
	{
		return StructureType.OBJECT_CLASS;
	}


	@Override
	protected RPsiElement getNameElement()
	{
		return getObject();
	}
}
