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

package org.jetbrains.plugins.ruby.ruby.cache.psi.impl;

import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.*;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualGlobalVar;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.presentation.RFilePresentationUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: Oct 2, 2006
 */
public class RVirtualFileImpl extends RVirtualFieldContantContainerImpl implements RVirtualFile {
    private String myLocation;
    private List<RVirtualRequire> myRequires;
    private List<RVirtualGlobalVar> myGlobalVars;

    public RVirtualFileImpl(final String name, final String location,
                            final RVirtualContainer parentContainer,
                            final AccessModifier defaultChildAccessModifier,
                            @NotNull final RFileInfo containingFileInfo) {
        super(parentContainer, new RVirtualNameImpl(Arrays.asList(name), false), defaultChildAccessModifier, containingFileInfo);
        myLocation = location;
    }

    @NotNull
    public synchronized List<RVirtualRequire> getRequires() {
        if (myRequires == null){
            myRequires = new ArrayList<RVirtualRequire>();
            final RubyVirtualElementVisitor visitor = new RubyVirtualElementVisitor(){
                public void visitElement(RVirtualElement element) {
                    if (element instanceof RVirtualContainer){
                        for (RVirtualStructuralElement child : ((RVirtualContainer) element).getVirtualStructureElements()) {
                            child.accept(this);
                        }
                    }
                }

                public void visitRVirtualRequire(@NotNull final RVirtualRequire virtualRequire) {
                    myRequires.add(virtualRequire);
                }

                public void visitRVirtualLoad(RVirtualLoad rVirtualLoad) {
                    myRequires.add(rVirtualLoad);
                }
            };
            accept(visitor);
        }
        return myRequires;
    }


    @NotNull
    public ItemPresentation getPresentation() {
        return RFilePresentationUtil.getPresentation(this);
    }

    public Icon getIcon(final int flags) {
        return RFilePresentationUtil.getIcon();
    }


    public void accept(@NotNull RubyVirtualElementVisitor visitor) {
        visitor.visitRVirtualFile(this);
    }

    @Nullable
    public String getPresentableLocation() {
        return myLocation;
    }

    public String toString() {
        return "file [" + ((RVirtualElementBase) getVirtualName()).getId() + "] " + getFullName();
    }

    public void dump(@NotNull StringBuilder buffer, int indent) {
        super.dump(buffer, indent);
        for (RVirtualGlobalVar var : myGlobalVars) {
            buffer.append(NEW_LINE);
            ((RVirtualElementBase) var).dump(buffer, indent+1);
        }
    }

    public StructureType getType() {
        return StructureType.FILE;
    }

    // RVirtualGlobalVarsHolder methods
    public void setVirtualGlobalVars(List<RVirtualGlobalVar> vars) {
        myGlobalVars = vars;
    }

    @NotNull
    public List<RVirtualGlobalVar> getVirtualGlobalVars() {
        return myGlobalVars;
    }

}
