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

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualClassImpl;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualNameImpl;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RClassName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RSuperClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.RNameUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.RFieldConstantContainerImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import org.jetbrains.plugins.ruby.ruby.presentation.RClassPresentationUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class RClassImpl extends RFieldConstantContainerImpl implements RClass {
    public RClassImpl(ASTNode astNode) {
        super(astNode);
    }

    @Override
	public void accept(@NotNull PsiElementVisitor visitor){
        if (visitor instanceof RubyElementVisitor){
            ((RubyElementVisitor) visitor).visitRClass(this);
            return;
        }
        super.accept(visitor);
    }

    @Override
	@NotNull
    public ItemPresentation getPresentation() {
        return RClassPresentationUtil.getPresentation(this);
    }

    @Override
	@Nullable
    public RClassName getClassName() {
        return RubyPsiUtil.getChildByType(this, RClassName.class, 0);
    }

    @Override
	@Nullable
    public RSuperClass getPsiSuperClass() {
        PsiElement superClass = getChildByFilter(RubyElementTypes.SUPER_CLASS, 0);
        return superClass != null ? (RSuperClass) superClass : null;
    }

    @Override
	@NotNull
    public RVirtualClass createVirtualCopy(@Nullable final RVirtualContainer virtualParent,
                                           @NotNull final RFileInfo info) {
        final RVirtualName virtualClassName = new RVirtualNameImpl(getFullPath(), isGlobal());
        final RSuperClass superClass = getPsiSuperClass();
        RVirtualName virtualSuperClass = null;
        if (superClass!=null){
            virtualSuperClass = new RVirtualNameImpl(RNameUtil.getPath(superClass), RNameUtil.isGlobal(superClass));
        }

        assert virtualParent != null;
        final RVirtualClassImpl rVirtualClass =
                new RVirtualClassImpl(virtualParent, virtualClassName, virtualSuperClass,
                                      getAccessModifier(), info);
        addVirtualData(rVirtualClass, info);
        return rVirtualClass;
    }

    @Override
	public int getTextOffset() {
        final RClassName className = getClassName();
        return className!=null ? className.getTextOffset() : super.getTextOffset();
    }

    @Override
	public PsiElement setName(@NonNls @NotNull final String name) throws IncorrectOperationException {
        return null;
    }

    @Override
	public StructureType getType() {
        return StructureType.CLASS;
    }

    @Override
	protected RPsiElement getNameElement() {
        return getClassName();
    }

    @Override
	@Nullable
    public RVirtualName getVirtualSuperClass() {
        final RSuperClass superClass = getPsiSuperClass();
        return superClass!=null ? new RVirtualNameImpl(RNameUtil.getPath(superClass), RNameUtil.isGlobal(superClass)) : null;
    }

    @Override
	public boolean equalsToVirtual(@NotNull final RVirtualStructuralElement element) {
        if (!super.equalsToVirtual(element)) {
            return false;
        }
        if (!(element instanceof RVirtualClass)){
            return false;
        }
        final RVirtualClass rvClass = (RVirtualClass) element;

// superclass checking
        final RSuperClass rSuperClass = getPsiSuperClass();
        final RVirtualName rvSuperClass = rvClass.getVirtualSuperClass();
        if (rSuperClass!=null){
            if (rvSuperClass==null){
                return false;
            }
            if (!RNameUtil.getPath(rSuperClass).equals(rvSuperClass.getPath())){
                return false;
            }
            if (RNameUtil.isGlobal(rSuperClass) != rvSuperClass.isGlobal()){
                return false;
            }
        } else {
            if (rvSuperClass!=null){
                return false;
            }
        }
        return true;
    }

    @Override
	@NotNull
    public List<RClass> getSuperClass(@NotNull final FileSymbol fileSymbol) {
        final Symbol symbol = SymbolUtil.getSymbolByContainer(fileSymbol, this);
        if (symbol == null){
            return Collections.emptyList();
        }
        final ArrayList<RClass> superClasses = new ArrayList<RClass>();
        for (Symbol superClass : symbol.getChildren(fileSymbol).getSymbolsOfTypes(Type.SUPERCLASS.asSet()).getAll()) {
            for (RVirtualElement element : superClass.getLinkedSymbol().getVirtualPrototypes(fileSymbol).getAll()) {
                if (element instanceof RClass){
                    superClasses.add((RClass) element);
                }
            }
        }
        return superClasses;
    }
}
