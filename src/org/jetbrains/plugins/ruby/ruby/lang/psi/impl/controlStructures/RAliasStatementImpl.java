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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualAlias;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualAliasImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RAliasStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import org.jetbrains.plugins.ruby.ruby.presentation.RAliasPresentationUtil;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.06.2006
 */
public class RAliasStatementImpl extends RPsiElementBase implements RAliasStatement {
    public RAliasStatementImpl(ASTNode astNode) {
        super(astNode);
    }

    @Override
	public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof RubyElementVisitor) {
            ((RubyElementVisitor)visitor).visitRAliasStatement(this);
            return;
        }
        super.accept(visitor);
    }

    @Override
	@Nullable
    public RPsiElement getPsiOldName() {
        return getChildByType(RPsiElement.class, 1);
    }

    @Override
	@Nullable
    public RPsiElement getPsiNewName() {
        return getChildByType(RPsiElement.class, 0);
    }

    @Override
	@NotNull
    public String getOldName() {
        return getName(getPsiOldName());
    }

    @Override
	@NotNull
    public String getNewName() {
        return getName(getPsiNewName());
    }

    @Override
	@NotNull
    public RVirtualStructuralElement createVirtualCopy(@Nullable final RVirtualContainer container,
                                                       @NotNull final RFileInfo info) {
        return new RVirtualAliasImpl(container, getOldName(), getNewName());
    }

    @Override
	public StructureType getType() {
        return StructureType.ALIAS;
    }

    @Override
	@NotNull
    public String getPresentableText() {
        return getText();
    }

    @NotNull
    private static String getName(RPsiElement element) {
        if (element!=null){
            if (element instanceof RSymbol){
                return ((RSymbol) element).getObject().getText();
            }
            return element.getText();
        }
        return "";
    }

    @Override
	public boolean equalsToVirtual(@NotNull final RVirtualStructuralElement element) {
        if (!(element instanceof RVirtualAlias)){
            return false;
        }
        final RVirtualAlias alias = (RVirtualAlias) element;
        return getNewName().equals(alias.getNewName()) && getOldName().equals(alias.getOldName());
    }

    @Override
	@Nullable
    public Icon getIcon(final int flags) {
        return RAliasPresentationUtil.getIcon();
    }

    @Override
	public ItemPresentation getPresentation() {
        return RAliasPresentationUtil.getPresentation(this);
    }
}
