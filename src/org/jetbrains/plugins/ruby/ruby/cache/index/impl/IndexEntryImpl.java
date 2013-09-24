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

package org.jetbrains.plugins.ruby.ruby.cache.index.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.index.IndexEntry;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualAlias;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualConstant;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualFieldAttr;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualGlobalVar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 13, 2007
 */
public class IndexEntryImpl implements IndexEntry {
    private List<RVirtualClass> myClasses = new ArrayList<RVirtualClass>();
    private List<RVirtualModule> myModules = new ArrayList<RVirtualModule>();
    private List<RVirtualMethod> myMethods = new ArrayList<RVirtualMethod>();
    private List<RVirtualField> myFields = new ArrayList<RVirtualField>();
    private List<RVirtualConstant> myConstants = new ArrayList<RVirtualConstant>();
    private List<RVirtualGlobalVar> myGlobalVars = new ArrayList<RVirtualGlobalVar>();
    private List<RVirtualAlias> myAliases = new ArrayList<RVirtualAlias>();
    private List<RVirtualFieldAttr> myFieldAttrs = new ArrayList<RVirtualFieldAttr>();

    @NotNull
    public List<RVirtualClass> getClasses() {
        return myClasses;
    }

    @NotNull
    public List<RVirtualModule> getModules() {
        return myModules;
    }

    @NotNull
    public List<RVirtualMethod> getMethods() {
        return myMethods;
    }

    @NotNull
    public List<RVirtualField> getFields() {
        return myFields;
    }

    @NotNull
    public List<RVirtualConstant> getConstants() {
        return myConstants;
    }

    @NotNull
    public List<RVirtualGlobalVar> getGlobalVars() {
        return myGlobalVars;
    }

    @NotNull
    public List<RVirtualAlias> getAliases() {
        return myAliases;
    }

    @NotNull
    public List<RVirtualFieldAttr> getFieldAttrs() {
        return myFieldAttrs;
    }

    public boolean isEmpty() {
        return myClasses.isEmpty() &&
                myModules.isEmpty() &&
                myMethods.isEmpty() &&
                myConstants.isEmpty() &&
                myGlobalVars.isEmpty() &&
                myFields.isEmpty() &&
                myAliases.isEmpty() &&
                myFieldAttrs.isEmpty();
    }


    public void addContainer(@NotNull final RVirtualContainer container) {
        final StructureType type = container.getType();
        if (type.isMethod()) {
            addMethod((RVirtualMethod) container);
            return;
        }
        if (type == StructureType.CLASS) {
            addClass((RVirtualClass) container);
            return;
        }
        if (type == StructureType.MODULE) {
            addModule((RVirtualModule) container);
        }
    }

    private void addClass(@NotNull final RVirtualClass vClass) {
        myClasses.add(vClass);
    }

    private void addModule(@NotNull final RVirtualModule vModule) {
        myModules.add(vModule);
    }

    private void addMethod(@NotNull final RVirtualMethod vMethod) {
        myMethods.add(vMethod);
    }

    public void addConstant(@NotNull final RVirtualConstant constant) {
        myConstants.add(constant);
    }

    public void addGlobalVar(@NotNull final RVirtualGlobalVar globalVar) {
        myGlobalVars.add(globalVar);
    }

    public void addAlias(@NotNull final RVirtualAlias rVirtualAlias) {
        myAliases.add(rVirtualAlias);
    }

    public void addFieldAttr(@NotNull final RVirtualFieldAttr rVirtualFieldAttr) {
        myFieldAttrs.add(rVirtualFieldAttr);
    }

    public void addField(@NotNull final RVirtualField field) {
        myFields.add(field);
    }


    public void removeContainer(@NotNull final RVirtualContainer container) {
        final StructureType type = container.getType();
        if (type.isMethod()) {
            removeMethod((RVirtualMethod) container);
            return;
        }
        if (type == StructureType.CLASS) {
            removeClass((RVirtualClass) container);
            return;
        }
        if (type == StructureType.MODULE) {
            removeModule((RVirtualModule) container);
        }
    }

    private void removeModule(@NotNull final RVirtualModule rVirtualModule) {
        myModules.remove(rVirtualModule);
    }

    private void removeClass(@NotNull final RVirtualClass rVirtualClass) {
        myClasses.remove(rVirtualClass);
    }

    private void removeMethod(@NotNull final RVirtualMethod rVirtualMethod) {
        myMethods.remove(rVirtualMethod);
    }

    public void removeField(@NotNull final RVirtualField field) {
        myFields.remove(field);
    }

    public void removeConstant(@NotNull final RVirtualConstant constant) {
        myConstants.remove(constant);
    }

    public void removeGlobalVar(@NotNull final RVirtualGlobalVar globalVar) {
        myGlobalVars.remove(globalVar);
    }

    public void removeAlias(@NotNull final RVirtualAlias rVirtualAlias) {
        myAliases.remove(rVirtualAlias);
    }

    public void removeFieldAttr(@NotNull final RVirtualFieldAttr rVirtualFieldAttr) {
        myFieldAttrs.remove(rVirtualFieldAttr);
    }

}
