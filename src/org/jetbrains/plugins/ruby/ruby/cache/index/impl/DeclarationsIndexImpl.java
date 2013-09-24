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

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.index.DeclarationsIndex;
import org.jetbrains.plugins.ruby.ruby.cache.index.IndexEntry;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualAlias;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.*;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualConstantHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualFieldHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualGlobalVarHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualConstant;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualFieldAttr;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualGlobalVar;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: Jan 22, 2007
 */
public class DeclarationsIndexImpl implements DeclarationsIndex {
    final private Project myProject;

    final private Map<String, IndexEntry> myIndex = new HashMap<String, IndexEntry>();
    private RubyFilesCache myRubyFilesCache = null;

    public DeclarationsIndexImpl(@NotNull final Project project) {
        myProject = project;
    }

    public void setFileCache(@NotNull final RubyFilesCache rubyFilesCache){
        myRubyFilesCache = rubyFilesCache;
    }

    public void build(final boolean runProcessWithProgressSynchronously) {
        final ProgressManager manager = ProgressManager.getInstance();

        final Runnable buildIndexRunnable = new Runnable() {
            public void run() {
                if (runProcessWithProgressSynchronously) {
                    final ProgressIndicator indicator = manager.getProgressIndicator();
                    if (indicator != null) {
                        indicator.setIndeterminate(true);
                    }
                }
                myIndex.clear();
                buildIndexByCache();
            }
        };
        if (runProcessWithProgressSynchronously){
            manager.runProcessWithProgressSynchronously(buildIndexRunnable, RBundle.message("building.index.message"), false, myProject);
        } else {
            buildIndexRunnable.run();
        }
    }

    private void buildIndexByCache() {
        for (String url : myRubyFilesCache.getAllUrls()) {
            final VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
            final VirtualFile fileByUrl = virtualFileManager.findFileByUrl(url);
            if (fileByUrl!=null){
                final RFileInfo fileInfo = myRubyFilesCache.getUp2DateFileInfo(fileByUrl);
                if (fileInfo!=null){
                    addFileInfoToIndex(fileInfo);
                }
            }
        }
    }

    /**
     * Adds info form rFileInfo to shortNameIndex
     * @param fileInfo RFileInfo to add information from
     */
    public void addFileInfoToIndex(@NotNull final RFileInfo fileInfo) {
        final RVirtualFile root = fileInfo.getRVirtualFile();
        final List<RVirtualStructuralElement> elements = RVirtualUtil.gatherAllStructuralElement(root);

        for (RVirtualStructuralElement element : elements) {
            if (element.getType().isContainer()){
                addVirtualContainerToIndex(((RVirtualContainer) element));
            } else
            if (element.getType() == StructureType.ALIAS){
                addVirtualAliasToIndex((RVirtualAlias) element);
            } else
            if (element.getType() == StructureType.FIELD_ATTR_CALL){
                addVirtualFieldAttrToIndex((RVirtualFieldAttr) element);
            }

            if (element instanceof RVirtualFieldHolder){
                for (RVirtualField field : ((RVirtualFieldHolder) element).getVirtualFields()) {
                    addVirtualFieldToIndex(field);
                }
            }

            if (element instanceof RVirtualConstantHolder){
                for (RVirtualConstant constant : ((RVirtualConstantHolder) element).getVirtualConstants()) {
                    addVirtualConstantToIndex(constant);
                }
            }

            if (element instanceof RVirtualGlobalVarHolder){
                for (RVirtualGlobalVar globalVar : ((RVirtualGlobalVarHolder) element).getVirtualGlobalVars()) {
                    addVirtualGlobalVarToIndex(globalVar);
                }
            }
        }
    }

    public void removeFileInfoFromIndex(@Nullable final RFileInfo fileInfo) {
        if (fileInfo==null){
            return;
        }

        final RVirtualFile root = fileInfo.getRVirtualFile();
        final List<RVirtualStructuralElement> elements = RVirtualUtil.gatherAllStructuralElement(root);

        for (RVirtualStructuralElement element : elements) {
            if (element.getType().isContainer()){
                removeVirtualContainerFromIndex(((RVirtualContainer) element));
            } else
            if (element.getType() == StructureType.ALIAS){
                removeVirtualAliasFromIndex((RVirtualAlias) element);
            } else
            if (element.getType() == StructureType.FIELD_ATTR_CALL){
                removeVirtualFieldAttrFromIndex((RVirtualFieldAttr) element);
            }

            if (element instanceof RVirtualFieldHolder){
                for (RVirtualField field : ((RVirtualFieldHolder) element).getVirtualFields()) {
                    removeVirtualFieldFromIndex(field);
                }
            }

            if (element instanceof RVirtualConstantHolder){
                for (RVirtualConstant constant : ((RVirtualConstantHolder) element).getVirtualConstants()) {
                    removeVirtualConstantFromIndex(constant);
                }
            }

            if (element instanceof RVirtualGlobalVarHolder){
                for (RVirtualGlobalVar globalVar : ((RVirtualGlobalVarHolder) element).getVirtualGlobalVars()) {
                    removeVirtualGlobalVarFromIndex(globalVar);
                }
            }
        }
    }

    /**
     * Adds the given container to index. Creates records for given name, if not exists
     * @param container Container to add to Index
     */
    private void addVirtualContainerToIndex(@NotNull final RVirtualContainer container) {
        final String name = container.getName();
        IndexEntry entry = myIndex.get(name);
        if (entry == null) {
            entry = new IndexEntryImpl();
            myIndex.put(name, entry);
        }
        ((IndexEntryImpl) entry).addContainer(container);
    }

    private void addVirtualConstantToIndex(@NotNull final RVirtualConstant constant) {
        final String name = constant.getName();
        IndexEntry entry = myIndex.get(name);
        if (entry == null) {
            entry = new IndexEntryImpl();
            myIndex.put(name, entry);
        }
        ((IndexEntryImpl) entry).addConstant(constant);
    }

    private void addVirtualGlobalVarToIndex(@NotNull final RVirtualGlobalVar globalVar) {
        final String name = globalVar.getText();
        IndexEntry entry = myIndex.get(name);
        if (entry == null) {
            entry = new IndexEntryImpl();
            myIndex.put(name, entry);
        }
        ((IndexEntryImpl) entry).addGlobalVar(globalVar);
    }

    private void addVirtualAliasToIndex(@NotNull final RVirtualAlias rVirtualAlias) {
        final String name = rVirtualAlias.getNewName();
        IndexEntry entry = myIndex.get(name);
        if (entry == null) {
            entry = new IndexEntryImpl();
            myIndex.put(name, entry);
        }
        ((IndexEntryImpl) entry).addAlias(rVirtualAlias);
    }

    private void addVirtualFieldAttrToIndex(@NotNull final RVirtualFieldAttr rVirtualFieldAttr) {
        for (String name : rVirtualFieldAttr.getNames()) {
            IndexEntry entry = myIndex.get(name);
            if (entry == null) {
                entry = new IndexEntryImpl();
                myIndex.put(name, entry);
            }
            ((IndexEntryImpl) entry).addFieldAttr(rVirtualFieldAttr);
        }
    }

    private void addVirtualFieldToIndex(@NotNull final RVirtualField field) {
        final String name = field.getName();
        IndexEntry entry = myIndex.get(name);
        if (entry == null) {
            entry = new IndexEntryImpl();
            myIndex.put(name, entry);
        }
        ((IndexEntryImpl) entry).addField(field);
    }


    /**
     * Removes given container from index. Deletes records by name if needed
     * @param container Container to remove from Index
     */
    private void removeVirtualContainerFromIndex(@NotNull final RVirtualContainer container) {
        final String name = container.getName();
        final IndexEntry entry = myIndex.get(name);
        if (entry==null){
            return;
        }
        ((IndexEntryImpl) entry).removeContainer(container);
        if (entry.isEmpty()){
            myIndex.remove(name);
        }
    }

    private void removeVirtualConstantFromIndex(@NotNull final RVirtualConstant constant) {
        final String name = constant.getName();
        final IndexEntry entry = myIndex.get(name);
        if (entry==null){
            return;
        }
        ((IndexEntryImpl) entry).removeConstant(constant);
        if (entry.isEmpty()){
            myIndex.remove(name);
        }
    }

    private void removeVirtualGlobalVarFromIndex(@NotNull final RVirtualGlobalVar globalVar) {
        final String name = globalVar.getText();
        final IndexEntry entry = myIndex.get(name);
        if (entry==null){
            return;
        }
        ((IndexEntryImpl) entry).removeGlobalVar(globalVar);
        if (entry.isEmpty()){
            myIndex.remove(name);
        }
    }

    private void removeVirtualAliasFromIndex(@NotNull final RVirtualAlias rVirtualAlias) {
        final String name = rVirtualAlias.getNewName();
        final IndexEntry entry = myIndex.get(name);
        if (entry==null){
            return;
        }
        ((IndexEntryImpl) entry).removeAlias(rVirtualAlias);
        if (entry.isEmpty()){
            myIndex.remove(name);
        }
    }

    private void removeVirtualFieldAttrFromIndex(@NotNull final RVirtualFieldAttr rVirtualFieldAttr) {
        for (String name : rVirtualFieldAttr.getNames()) {
            final IndexEntry entry = myIndex.get(name);
            if (entry==null){
                return;
            }
            ((IndexEntryImpl) entry).removeFieldAttr(rVirtualFieldAttr);
            if (entry.isEmpty()){
                myIndex.remove(name);
            }
        }
    }

    private void removeVirtualFieldFromIndex(@NotNull final RVirtualField field) {
        final String name = field.getName();
        final IndexEntry entry = myIndex.get(name);
        if (entry==null){
            return;
        }
        ((IndexEntryImpl) entry).removeField(field);
        if (entry.isEmpty()){
            myIndex.remove(name);
        }
    }


    @NotNull
    public List<RVirtualClass> getClassesByName(@NotNull final String name) {
        final IndexEntry entry = myIndex.get(name);
        if (entry != null) {
            return entry.getClasses();
        }
        return Collections.emptyList();
    }

    @NotNull
    public List<RVirtualModule> getModulesByName(@NotNull final String name) {
        final IndexEntry entry = myIndex.get(name);
        if (entry != null) {
            return entry.getModules();
        }
        return Collections.emptyList();
    }

    @NotNull
    public List<RVirtualMethod> getMethodsByName(@NotNull final String name) {
        final IndexEntry entry = myIndex.get(name);
        if (entry != null) {
            return entry.getMethods();
        }
        return Collections.emptyList();
    }

    @NotNull
    public List<RVirtualField> getFieldsByName(@NotNull final String name) {
        final IndexEntry entry = myIndex.get(name);
        if (entry != null) {
            return entry.getFields();
        }
        return Collections.emptyList();
    }

    @NotNull
    public List<RVirtualConstant> getConstantsByName(@NotNull final String name) {
        final IndexEntry entry = myIndex.get(name);
        if (entry != null) {
            return entry.getConstants();
        }
        return Collections.emptyList();
    }

    @NotNull
    public List<RVirtualGlobalVar> getGlobalVarsByName(@NotNull String name) {
        final IndexEntry entry = myIndex.get(name);
        if (entry != null) {
            return entry.getGlobalVars();
        }
        return Collections.emptyList();
    }

    @NotNull
    public List<RVirtualAlias> getAliasesByName(@NotNull String name) {
        final IndexEntry entry = myIndex.get(name);
        if (entry != null) {
            return entry.getAliases();
        }
        return Collections.emptyList();
    }

    @NotNull
    public List<RVirtualFieldAttr> getFieldAttrsByName(@NotNull String name) {
        final IndexEntry entry = myIndex.get(name);
        if (entry != null) {
            return entry.getFieldAttrs();
        }
        return Collections.emptyList();
    }

    @NotNull
    public Collection<String> getAllClassesNames() {
        final List<String> names = new ArrayList<String>();
        for (String name: getAllNames()) {
            final IndexEntry entry = myIndex.get(name);
            if (entry !=null && !entry.getClasses().isEmpty()){
                names.add(name);
            }
        }
        return names;
    }

    @NotNull
    public Collection<String> getAllMethodsNames() {
        final List<String> names = new ArrayList<String>();
        for (String name: getAllNames()) {
            final IndexEntry entry = myIndex.get(name);
            if (entry !=null && !entry.getMethods().isEmpty()){
                names.add(name);
            }
        }
        return names;
    }

    @NotNull
    public Collection<String> getAllModulesNames() {
        final List<String> names = new ArrayList<String>();
        for (String name: getAllNames()) {
            final IndexEntry entry = myIndex.get(name);
            if (entry !=null && !entry.getModules().isEmpty()){
                names.add(name);
            }
        }
        return names;
    }

    @NotNull
    public Collection<String> getAllFieldsNames() {
        final List<String> names = new ArrayList<String>();
        for (String name: getAllNames()) {
            final IndexEntry entry = myIndex.get(name);
            if (entry !=null && !entry.getFields().isEmpty()){
                names.add(name);
            }
        }
        return names;
    }

    @NotNull
    public Collection<String> getAllConstantsNames() {
        final List<String> names = new ArrayList<String>();
        for (String name: getAllNames()) {
            final IndexEntry entry = myIndex.get(name);
            if (entry !=null && !entry.getConstants().isEmpty()){
                names.add(name);
            }
        }
        return names;
    }

    @NotNull
    public Collection<String> getAllGlobalVarsNames() {
        final List<String> names = new ArrayList<String>();
        for (String name: getAllNames()) {
            final IndexEntry entry = myIndex.get(name);
            if (entry !=null && !entry.getGlobalVars().isEmpty()){
                names.add(name);
            }
        }
        return names;
    }

    @NotNull
    public Collection<String> getAllAliasesNames() {
        final List<String> names = new ArrayList<String>();
        for (String name: getAllNames()) {
            final IndexEntry entry = myIndex.get(name);
            if (entry !=null && !entry.getAliases().isEmpty()){
                names.add(name);
            }
        }
        return names;
    }

    @NotNull
    public Collection<String> getAllFieldAttrsNames() {
        final List<String> names = new ArrayList<String>();
        for (String name: getAllNames()) {
            final IndexEntry entry = myIndex.get(name);
            if (entry !=null && !entry.getFieldAttrs().isEmpty()){
                names.add(name);
            }
        }
        return names;
    }

    private HashSet<String> getAllNames() {
        return new HashSet<String>(myIndex.keySet());
    }

    /**
     * Used in debug purposes
     */
    public void printIndex(){
        final Collection<String> classNames = getAllClassesNames();
        System.out.println("\n\nClass names: "+classNames.size());
        for (String s : classNames) {
            final List<RVirtualClass> classes = getClassesByName(s);
            System.out.println("  "+s+": " + classes.size());
            for (RVirtualContainer container : classes) {
                System.out.println("    "+container.getContainingFileUrl());
            }
        }

        final Collection<String> moduleNames = getAllModulesNames();
        System.out.println("\n\nModule names: "+moduleNames.size());
        for (String s : moduleNames) {
            final List<RVirtualModule> modules = getModulesByName(s);
            System.out.println("  "+s + ": " + modules.size());
            for (RVirtualContainer container : modules) {
                System.out.println("    "+container.getContainingFileUrl());
            }
        }

        final Collection<String> methodNames = getAllMethodsNames();
        System.out.println("\n\nMethod names: "+methodNames.size());
        for (String s : methodNames) {
            final List<RVirtualMethod> methods = getMethodsByName(s);
            System.out.println("  "+s + ": " + methods.size());
            for (RVirtualContainer container : methods) {
                System.out.println("    "+container.getContainingFileUrl());
            }
        }

        final Collection<String> fieldNames = getAllFieldsNames();
        System.out.println("\n\nFields names: "+fieldNames.size());
        for (String s : fieldNames) {
            final List<RVirtualField> fields = getFieldsByName(s);
            System.out.println("  "+s+": " + fields.size());
        }

        final Collection<String> constantNames = getAllConstantsNames();
        System.out.println("\n\nConstant names: "+constantNames.size());
        for (String s : constantNames) {
            final List<RVirtualConstant> constants = getConstantsByName(s);
            System.out.println("  "+s+": " + constants.size());
        }

        final Collection<String> globalVarNames = getAllGlobalVarsNames();
        System.out.println("\n\nGlobal variable names: "+globalVarNames.size());
        for (String s : globalVarNames) {
            final List<RVirtualGlobalVar> globalVars = getGlobalVarsByName(s);
            System.out.println("  "+s+": " + globalVars.size());
        }

        final Collection<String> aliasNames = getAllAliasesNames();
        System.out.println("\n\nAlias names: "+aliasNames.size());
        for (String s : aliasNames) {
            final List<RVirtualAlias> aliases = getAliasesByName(s);
            System.out.println("  "+s+": " + aliases.size());
        }

        final Collection<String> fieldAttrNames = getAllFieldAttrsNames();
        System.out.println("\n\nField attrs names: "+fieldAttrNames.size());
        for (String s : fieldAttrNames) {
            final List<RVirtualFieldAttr> fieldAttrs = getFieldAttrsByName(s);
            System.out.println("  "+s+": " + fieldAttrs.size());
        }
    }

}
