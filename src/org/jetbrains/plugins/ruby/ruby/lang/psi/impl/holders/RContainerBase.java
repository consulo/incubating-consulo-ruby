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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.RubyPsiManager;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualContainerBase;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.RootScope;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeBuilder;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.TypeInferenceContext;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RStructuralElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.Instruction;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.impl.RControlFlowBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RBodyStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RConstantHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RFieldHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.RNameUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 21.07.2006
 */
public abstract class RContainerBase extends RPsiElementBase implements RContainer {
    private AccessModifier myAccessModifier = AccessModifier.PUBLIC;
    protected List<RStructuralElement> myStructureElements;
    private Instruction[] myControlFlow;

    public RContainerBase(final ASTNode astNode) {
        super(astNode);
    }


    @NotNull
    public AccessModifier getDefaultChildAccessModifier() {
        return AccessModifier.PUBLIC;
    }

    @NotNull
    public AccessModifier getAccessModifier() {
// getSubContainers of parentContainer will set correct access modifiers to all of it`s children
        final RContainer parentContainer = getParentContainer();
        if (parentContainer!=null){
            parentContainer.getStructureElements();
        }
        return myAccessModifier;
    }

    public void setAccessModifier(final AccessModifier modifier) {
        myAccessModifier = modifier;
    }

    @NotNull
    public final List<RStructuralElement> getStructureElements() {
        if (myStructureElements == null) {
            myStructureElements = RContainerUtil.getStructureElements(this);
        }
        return myStructureElements;
    }

    public synchronized void subtreeChanged() {
        clearMyCaches();
        super.subtreeChanged();
    }

    private void clearMyCaches(){
        myStructureElements = null;
        myScope = null;

        // Clear control flow and inference info
        final TypeInferenceContext context = RubyPsiManager.getInstance(getProject()).getTypeInferenceHelper().getContext();
        if (context!=null){
            context.localVariablesTypesCache.remove(this);
        }
        myControlFlow = null;
    }

    @Nullable
    public RFileInfo getContainingFileInfo() {
        return null;
    }

    @NotNull
    public String getContainingFileUrl() {
        //noinspection ConstantConditions
        return getContainingFile().getVirtualFile().getUrl();
    }

    @Nullable
    public VirtualFile getVirtualFile() {
        return getContainingFile().getVirtualFile();
    }

    protected void addVirtualData(@NotNull final RVirtualContainer virtualCopy,
                                  @NotNull final RFileInfo fileInfo) {
        if (this instanceof RFieldHolder){
            RVirtualUtil.addVirtualFields(virtualCopy, (RFieldHolder)this);
        }
        if (this instanceof RConstantHolder){
            RVirtualUtil.addVirtualConstants(virtualCopy, (RConstantHolder)this);
        }

        final List<RVirtualStructuralElement> elements = new ArrayList<RVirtualStructuralElement>();

// recursive generate all the info about all the children
        for (RStructuralElement structuralElement : getStructureElements()){
            elements.add(structuralElement.createVirtualCopy(virtualCopy, fileInfo));
        }
        ((RVirtualContainerBase)virtualCopy).setStructureElements(elements);
    }


    @Nullable
    public RVirtualContainer getVirtualParentContainer() {
        return getParentContainer();
    }

    @NotNull
    public List<RVirtualStructuralElement> getVirtualStructureElements() {
        final ArrayList<RVirtualStructuralElement> elements = new ArrayList<RVirtualStructuralElement>();
        for (RStructuralElement element : getStructureElements()) {
            elements.add(element);
        }
        return elements;
    }

    public int getIndexOf(@NotNull RVirtualStructuralElement element) {
        final List<RStructuralElement> structuralElements = getStructureElements();
        for (int i=0; i < structuralElements.size();i++){
            if (element == structuralElements.get(i)){
                return i;
            }
        }
        return -1;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// ScopeHolder
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // scope for scopeHolder
    private RootScope myScope;

    @NotNull
    public synchronized RootScope getScope() {
        if (myScope == null){
            myScope = ScopeBuilder.buildScope(this);
        }
        return myScope;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// Name related methods
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @NotNull
    public String getName() {
        return RNameUtil.getName(getFullPath());
    }

    @NotNull
    public List<String> getFullPath() {
        return RNameUtil.getPath(getNameElement());
    }

    @NotNull
    public String getFullName() {
        return RNameUtil.getPresentableName(getNameElement().getText());
    }

    public boolean isGlobal() {
        return RNameUtil.isGlobal(getNameElement());
    }

    protected abstract RPsiElement getNameElement();

    public boolean equalsToVirtual(@NotNull final RVirtualStructuralElement element) {
        if (!(element instanceof RVirtualContainer)){
            return false;
        }
        final RVirtualContainer container = (RVirtualContainer) element;
        // Container changes
        if (getType()!=container.getType()){
            return false;
        }
        if (getAccessModifier()!=container.getAccessModifier()){
            return false;
        }
        if (!getFullName().equals(container.getFullName())){
            return false;
        }
        //noinspection SimplifiableIfStatement
        if (isGlobal()!=container.isGlobal()){
            return false;
        }

        return RVirtualPsiUtil.areSubStructureEqual(this, container);
    }

    @NotNull
    public RBodyStatement getBody() {
        final List<RBodyStatement> list = getChildrenByType(RBodyStatement.class);
        assert list.size() == 1;
        return list.get(0);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// ControlFlowOwner
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Instruction[] getControlFlow() {
        if (myControlFlow == null){
            myControlFlow = new RControlFlowBuilder().buildControlFlow(null, this, null, null);
        }
        return myControlFlow;
    }
}
