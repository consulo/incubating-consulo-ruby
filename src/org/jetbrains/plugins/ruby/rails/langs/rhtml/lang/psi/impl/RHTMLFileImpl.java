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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileViewProvider;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLFile;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.eRubyElementTypes;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.visitors.RHTMLElementTypeVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualRequire;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualConstant;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualGlobalVar;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.RootScope;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RStructuralElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.Instruction;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.ConstantDefinitions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.FieldDefinition;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.GlobalVarDefinition;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 03.04.2007
 */
public class RHTMLFileImpl extends PsiFileImpl implements RHTMLFile, RFile {

    public RHTMLFileImpl(FileViewProvider viewProvider) {
        super(eRubyElementTypes.RHTML_FILE, eRubyElementTypes.RHTML_FILE, viewProvider);
    }

    public String toString() {
        return "RHTMLFile:" + getName();
    }

    public int getTextLength() {
        //For prevent DirtyScope exception in ExternalToolPassFactory.calculateRangeToProcessForSyntaxPass()
        return getViewProvider().getContents().length();
    }

    @NotNull
    public RHTMLFileViewProvider getViewProvider() {
      return (RHTMLFileViewProvider)super.getViewProvider();
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof RHTMLElementTypeVisitor) {
            ((RHTMLElementTypeVisitor)visitor).visitRHTMLFile(this);
        } else if (visitor instanceof RubyElementVisitor) {
            ((RubyElementVisitor)visitor).visitRFile(this);
        }
    }

    @NotNull
    public FileType getFileType() {
        return RHTMLFileType.RHTML;
    }

///////////////////// RFile methods ///////////////////////////////

    @NotNull
    public RFile getInnerRubyFile() {
        return (RFile)getViewProvider().getPsi(RubyLanguage.RUBY);
    }

    @NotNull
    public RCompoundStatement getCompoundStatement() {
        return getInnerRubyFile().getCompoundStatement();
    }

    @NotNull
    public RVirtualFile createVirtualCopy(@Nullable RVirtualContainer virtualParent, @NotNull RFileInfo fileInfo) {
        return getInnerRubyFile().createVirtualCopy(virtualParent, fileInfo);
    }

    public StructureType getType() {
        return getInnerRubyFile().getType();
    }

    @Nullable
    public RContainer getParentContainer() {
        return getInnerRubyFile().getParentContainer();
    }

    @Nullable
    public RFileInfo getContainingFileInfo() {
        return getInnerRubyFile().getContainingFileInfo();
    }

    @Nullable
    public Module getModule() {
        return getInnerRubyFile().getModule();
    }

    public boolean isJRubyEnabled() {
        return false;
    }

    @Nullable
    public Sdk getSdk() {
        return getInnerRubyFile().getSdk();
    }

    @Nullable
    public String getPresentableLocation() {
        return getInnerRubyFile().getPresentableLocation();
    }

    @NotNull
    public AccessModifier getAccessModifier() {
        return getInnerRubyFile().getAccessModifier();
    }

    @NotNull
    public AccessModifier getDefaultChildAccessModifier() {
        return getInnerRubyFile().getDefaultChildAccessModifier();
    }

    @NotNull
    public String getContainingFileUrl() {
        return getInnerRubyFile().getContainingFileUrl();
    }

    @Nullable
    public RVirtualContainer getVirtualParentContainer() {
        return getInnerRubyFile().getVirtualParentContainer();
    }

    @NotNull
    public List<RVirtualStructuralElement> getVirtualStructureElements() {
        return getInnerRubyFile().getVirtualStructureElements();
    }

    public void accept(@NotNull RubyVirtualElementVisitor visitor) {
        getInnerRubyFile().accept(visitor);
    }

    @NotNull
    public List<RVirtualField> getVirtualFields() {
        return getInnerRubyFile().getVirtualFields();
    }

    @NotNull
    public List<RVirtualConstant> getVirtualConstants() {
        return getInnerRubyFile().getVirtualConstants();
    }

    @NotNull
    public List<RVirtualGlobalVar> getVirtualGlobalVars() {
        return getInnerRubyFile().getVirtualGlobalVars();
    }

    @NotNull
    public List<RStructuralElement> getStructureElements() {
        return getInnerRubyFile().getStructureElements();
    }

    @NotNull
    public List<RVirtualRequire> getRequires() {
        return getInnerRubyFile().getRequires();
    }

    @NotNull
    public List<PsiElement> getChildrenByFilter(IElementType filter) {
        return getInnerRubyFile().getChildrenByFilter(filter);
    }

    @Nullable
    public PsiElement getChildByFilter(TokenSet filter, int number) {
        return getInnerRubyFile().getChildByFilter(filter, number);
    }

    @Nullable
    public PsiElement getChildByFilter(IElementType filter, int number) {
        return getInnerRubyFile().getChildByFilter(filter,  number);
    }

    @NotNull
    public <T extends PsiElement> List<T> getChildrenByType(Class<T> c) {
        return getInnerRubyFile().getChildrenByType(c);
    }

    @Nullable
    public <T extends PsiElement> T getChildByType(Class<T> c, int number) {
        return getInnerRubyFile().getChildByType(c, number);
    }

    @NotNull
    public List<FieldDefinition> getFieldsDefinitions() {
        return getInnerRubyFile().getFieldsDefinitions();
    }

    @Nullable
    public FieldDefinition getDefinition(@NotNull RVirtualField field) {
        return getInnerRubyFile().getDefinition(field);
    }

    @NotNull
    public List<ConstantDefinitions> getConstantDefinitions() {
        return getInnerRubyFile().getConstantDefinitions();
    }

    @Nullable
    public ConstantDefinitions getDefinition(@NotNull RVirtualConstant constant) {
        return getInnerRubyFile().getDefinition(constant);
    }

    public int getIndexOf(@NotNull RVirtualStructuralElement element) {
        return getInnerRubyFile().getIndexOf(element);
    }

    @NotNull
    public List<GlobalVarDefinition> getGlobalVarDefinitions() {
        return getInnerRubyFile().getGlobalVarDefinitions();
    }

    @Nullable
    public GlobalVarDefinition getDefinition(@NotNull RVirtualGlobalVar globalVar) {
        return getInnerRubyFile().getDefinition(globalVar);
    }

    @Nullable
    public FileSymbol getFileSymbol() {
        return getInnerRubyFile().getFileSymbol();
    }

    @NotNull
    public RootScope getScope() {
        return getInnerRubyFile().getScope();
    }

    public Instruction[] getControlFlow() {
        return getInnerRubyFile().getControlFlow();
    }

    @NotNull
    public List<String> getFullPath() {
        return getInnerRubyFile().getFullPath();
    }

    @NotNull
    public String getFullName() {
        return getInnerRubyFile().getFullName();
    }

    public boolean isGlobal() {
        return getInnerRubyFile().isGlobal();
    }

    public boolean equalsToVirtual(@NotNull RVirtualStructuralElement element) {
        return getInnerRubyFile().equalsToVirtual(element);
    }
}
