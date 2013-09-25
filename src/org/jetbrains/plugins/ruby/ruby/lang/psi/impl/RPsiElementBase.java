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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RSuperClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.names.RNameNavigator;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 07.05.2005
 */
public class RPsiElementBase extends ASTWrapperPsiElement implements RPsiElement {
    public RPsiElementBase(@NotNull final ASTNode astNode) {
        super(astNode);
    }

    @Override
	@NotNull
    public Language getLanguage() {
        return RubyFileType.RUBY.getLanguage();
    }

    @Override
	@NotNull
    public String getName(){
        return getText();
    }

    public final String toString(){
        return getNode().getElementType().toString();
    }

    @Override
	public PsiElement replace(@NotNull PsiElement element){
        RubyPsiUtil.replaceInParent(this, element);
        return element;
    }

    @Override
	@NotNull
    public List<PsiElement> getChildrenByFilter(IElementType filter) {
        return RubyPsiUtil.getChildrenByFilter(this, filter);
    }

    @Override
	@Nullable
    public PsiElement getChildByFilter(TokenSet filter, int number) {
        return RubyPsiUtil.getChildByFilter(this, filter, number);
    }

    @Override
	@Nullable
    public PsiElement getChildByFilter(IElementType filter, int number) {
        return RubyPsiUtil.getChildByFilter(this, filter, number);
    }


    @Override
	@NotNull
    public <T extends PsiElement> List<T> getChildrenByType(Class<T> c) {
        return RubyPsiUtil.getChildrenByType(this, c);
    }

    @Override
	@Nullable
    public <T extends PsiElement> T getChildByType(Class<T> c, int number) {
        return RubyPsiUtil.getChildByType(this, c, number);
    }

    @Override
	@Nullable
    public RContainer getParentContainer() {
        return PsiTreeUtil.getParentOfType(this, RContainer.class);
    }

    @Nullable
    public RVirtualContainer getVirtualParentContainer() {
        return getParentContainer();
    }

    @Override
	public void accept(@NotNull RubyVirtualElementVisitor visitor) {
        visitor.visitElement(this);
    }

    public boolean isClassOrModuleName() {
        final RName rName = RNameNavigator.getRName(this);
        return rName != null && !(rName instanceof RSuperClass);
    }

    @Nullable
    public FileSymbol forceFileSymbolUpdate(){
        final RFile file = RubyPsiUtil.getRFile(this);
        assert file!=null;
        return file.getFileSymbol();
    }
}
