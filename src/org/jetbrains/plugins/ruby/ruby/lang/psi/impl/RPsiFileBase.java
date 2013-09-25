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

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.JRubyUtil;
import org.jetbrains.plugins.ruby.ruby.RubyPsiManager;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.*;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualContainerBase;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualFileImpl;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualConstant;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualGlobalVar;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.RootScope;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeBuilder;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.FileSymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.TypeInferenceContext;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RStructuralElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.Instruction;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.impl.RControlFlowBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.ConstantDefinitions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.FieldDefinition;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.GlobalVarDefinition;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.*;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import org.jetbrains.plugins.ruby.ruby.presentation.RFilePresentationUtil;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, oleg
 * @date: 02.04.2007
 */

public abstract class RPsiFileBase extends PsiFileImpl implements RFile {

    private RFileInfo myContainingFileInfo;
    protected List<RStructuralElement> myStructureElements;
    private Instruction[] myControlFlow;

    protected RPsiFileBase(IElementType elementType, IElementType contentElementType, FileViewProvider provider) {
        super(elementType, contentElementType, provider);
    }

    public RPsiFileBase(final FileViewProvider viewProvider) {
        super(viewProvider);
    }

    @Override
	@NotNull
    public String getContainingFileUrl() {
        final VirtualFile file = getVirtualFile();
        assert file != null;
        return file.getUrl();
    }

    @Override
	@NotNull
    public AccessModifier getAccessModifier() {
        return AccessModifier.PUBLIC;
    }

    @Override
	@Nullable
    public RFileInfo getContainingFileInfo() {
        return myContainingFileInfo;
    }

    public void setContainingFileInfo(RFileInfo containingFileInfo) {
        myContainingFileInfo = containingFileInfo;
    }

    @Override
	@Nullable
    public RContainer getParentContainer() {
        return null;
    }

    @Override
	@Nullable
    public RVirtualContainer getVirtualParentContainer() {
        return getParentContainer();
    }

    @Override
	@NotNull
    public AccessModifier getDefaultChildAccessModifier() {
        return AccessModifier.PUBLIC;
    }



    @Override
	public synchronized void subtreeChanged() {
        clearMyCaches();
        super.subtreeChanged();
    }

    @Override
	@NotNull
    public FileType getFileType() {
        return getViewProvider().getVirtualFile().getFileType();
    }

    private void clearMyCaches() {
        myStructureElements = null;
        myConstantDefinitions = null;
        myGlobalVarDefinitions = null;
        myFieldDefinitions = null;
        myScope = null;

        // Clear control flow and inference info
        final TypeInferenceContext context = RubyPsiManager.getInstance(getProject()).getTypeInferenceHelper().getContext();
        if (context!=null){
            context.localVariablesTypesCache.remove(this);
        }
        myControlFlow = null;
    }

    @Override
	public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof RubyElementVisitor) {
            ((RubyElementVisitor) visitor).visitRFile(this);
            return;
        }
        visitor.visitFile(this);
    }


    @Override
	@NotNull
    public RCompoundStatement getCompoundStatement() {
        //noinspection ConstantConditions
        return getChildByType(RCompoundStatement.class, 0);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// RPsiElement methods implementation
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
	public void accept(@NotNull RubyVirtualElementVisitor visitor) {
        visitor.visitRVirtualFile(this);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// RContainer methods implementation
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
	@NotNull
    public final List<RStructuralElement> getStructureElements() {
        if (myStructureElements == null) {
            myStructureElements = RContainerUtil.getStructureElements(this);
        }
        return myStructureElements;
    }

    @Override
	@NotNull
    public List<RVirtualConstant> getVirtualConstants() {
        return RVirtualUtil.getVirtualConstants(this, this);
    }

    @Override
	@NotNull
    public List<RVirtualField> getVirtualFields() {
        return RVirtualUtil.getVirtualFields(this, this);
    }

    @Override
	@NotNull
    public List<RVirtualGlobalVar> getVirtualGlobalVars() {
        return RVirtualUtil.getVirtualGlobalVars(this, this);
    }

    @Override
	@NotNull
    public RVirtualFile createVirtualCopy(@Nullable final RVirtualContainer virtualParent,
                                          @NotNull final RFileInfo fileInfo) {
        final VirtualFile file = getVirtualFile();
        assert file != null;
        final RVirtualFile virtualCopy =
                new RVirtualFileImpl(getName(), getPresentableLocation(), null, getDefaultChildAccessModifier(), fileInfo);
// setting virtual constants
        RVirtualUtil.addVirtualConstants(virtualCopy, this);

// setting virtual fields
        RVirtualUtil.addVirtualFields(virtualCopy, this);

// setting virtual fields
        RVirtualUtil.addVirtualGlobalVars(virtualCopy, this);

        final List<RVirtualStructuralElement> elements = new ArrayList<RVirtualStructuralElement>();

// recursive generate all the info about all the children
        for (RStructuralElement structuralElement : getStructureElements()){
            elements.add(structuralElement.createVirtualCopy(virtualCopy, fileInfo));
        }
        ((RVirtualContainerBase)virtualCopy).setStructureElements(elements);

        return virtualCopy;
    }

    @Override
	@NotNull
    public List<RVirtualRequire> getRequires() {
        throw new UnsupportedOperationException("getRequires is not implemented in org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiFileBase");
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// Fields, constantHolder, globalVarHolder
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Cached information about constant usages
    private List<ConstantDefinitions> myConstantDefinitions;
    // Cached information about globalVariables
    private List<GlobalVarDefinition> myGlobalVarDefinitions;
    // Cached information for FieldUsages
    private List<FieldDefinition> myFieldDefinitions;

    @Override
	@NotNull
    public List<FieldDefinition> getFieldsDefinitions() {
        if (myFieldDefinitions == null) {
            myFieldDefinitions = RFieldHolderUtil.gatherFieldDescriptions(this);
        }
        return myFieldDefinitions;
    }

    @Override
	@Nullable
    public FieldDefinition getDefinition(@NotNull final RVirtualField field) {
        return RFieldHolderUtil.getDefinition(this, field);
    }

    @Override
	@NotNull
    public List<ConstantDefinitions> getConstantDefinitions() {
        if (myConstantDefinitions == null) {
            myConstantDefinitions = RConstantHolderUtil.gatherConstantDefinitions(this);
        }
        return myConstantDefinitions;
    }

    @Override
	@Nullable
    public ConstantDefinitions getDefinition(@NotNull final RVirtualConstant constant) {
       return RConstantHolderUtil.getDefinition(this, constant);
    }

    @Override
	@NotNull
    public List<GlobalVarDefinition> getGlobalVarDefinitions() {
        if (myGlobalVarDefinitions == null) {
            myGlobalVarDefinitions = RGlobalVarHolderUtil.gatherGlobalVarDefinitions(this);
        }
        return myGlobalVarDefinitions;
    }

    @Override
	@Nullable
    public GlobalVarDefinition getDefinition(@NotNull RVirtualGlobalVar globalVar) {
        return RGlobalVarHolderUtil.getDefinition(this, globalVar);
    }

    @Override
	public VirtualFile getVirtualFile() {
        VirtualFile file = super.getVirtualFile();
        if (file == null) {
            final PsiFile originalFile = getOriginalFile();
            if (originalFile != null) {
                file = originalFile.getVirtualFile();
            }
        }
        return file;
    }

    @Override
	@Nullable
    public String getPresentableLocation() {
        final VirtualFile file = getVirtualFile();
        assert file != null;
        return RubySdkUtil.getPresentableLocation(getSdk(), file.getUrl());
    }



    @Override
	public StructureType getType() {
        return StructureType.FILE;
    }

    @Override
	@NotNull
    public List<RVirtualStructuralElement> getVirtualStructureElements() {
        // I do really hate JAVA Type system with covariant typing!!!
        final ArrayList<RVirtualStructuralElement> elements = new ArrayList<RVirtualStructuralElement>();
        for (RStructuralElement element : getStructureElements()) {
            elements.add(element);
        }
        return elements;
    }

    @Override
	@NotNull
    public List<String> getFullPath() {
        return Collections.emptyList();
    }

    @Override
	@NotNull
    public String getFullName() {
        return "foo";
    }

    @Override
	public boolean isGlobal() {
        return false;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// RPsiFile methods implementation
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
	@NotNull
    public ItemPresentation getPresentation() {
        return RFilePresentationUtil.getPresentation(this);
    }

    @Override
	@Nullable
    public Module getModule() {
        final VirtualFile file = getVirtualFile();
        return file!=null ? RFileUtil.getModule(getProject(), file) : null;
    }

    @Override
	public boolean isJRubyEnabled() {
        final Module module = getModule();
        return module!=null && JRubyUtil.hasJRubySupport(module);
    }

    @Override
	@Nullable
    public Sdk getSdk() {
        final VirtualFile file = getVirtualFile();
        return file!=null ? RFileUtil.getSdk(getProject(), file) : null;
    }


    @Override
	public int getIndexOf(@NotNull RVirtualStructuralElement element) {
        final List<RStructuralElement> structuralElements = getStructureElements();
        for (int i=0; i < structuralElements.size();i++){
            if (element == structuralElements.get(i)){
                return i;
            }
        }
        return -1;
    }

    @Override
	@Nullable
    public FileSymbol getFileSymbol() {
        return FileSymbolUtil.getFileSymbol(this);
    }

    @Override
	public boolean equalsToVirtual(@NotNull final RVirtualStructuralElement element) {
        // TODO: to be honest, we must add another 2 check!
        // RVPsiUtuils.areConstantHoldersEqual and RVPsiUtuils.areFieldHoldersEqual
        return element instanceof RVirtualFile && RVirtualPsiUtil.areSubStructureEqual(this, (RVirtualFile) element);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// ScopeHolder
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // scope for scopeHolder
    private RootScope myScope;

    @Override
	@NotNull
    public synchronized RootScope getScope() {
        if (myScope == null){
            myScope = ScopeBuilder.buildScope(this);
        }
        return myScope;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// ControlFlowOwner
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
	public Instruction[] getControlFlow() {
        if (myControlFlow == null){
            myControlFlow = new RControlFlowBuilder().buildControlFlow(null, this, null, null);
        }
        return myControlFlow;
    }
}