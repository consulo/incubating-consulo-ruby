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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.RubyModuleCachesManager;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RStructuralElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RAliasStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.AccessModifiersUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.blocks.RCompoundStatementNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.RContainerBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyStructureVisitor;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubySystemCallVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 21.07.2006
 */
public abstract class RContainerUtil {

    @NotNull
    public static List<RStructuralElement> selectElementsByType(@NotNull List<RStructuralElement> fullList,
                                                                @NotNull final StructureType type) {
        ArrayList<RStructuralElement> list = new ArrayList<RStructuralElement>();
        for (RStructuralElement element : fullList) {
            if (element.getType() == type) {
                list.add(element);
            }
        }
        return list;
    }

    @NotNull
    public static List<RVirtualStructuralElement> selectVirtualElementsByType(@NotNull List<RVirtualStructuralElement> fullList,
                                                                       @NotNull final StructureType type) {
        ArrayList<RVirtualStructuralElement> list = new ArrayList<RVirtualStructuralElement>();
        for (RVirtualStructuralElement element : fullList) {
            if (element.getType() == type) {
                list.add(element);
            }
        }
        return list;
    }


    /**
     * Searches first class among container children with name
     * <code>className</code> (search algorithm isn't recursive).
     * @param container container, i.e ruby class, module of file
     * @param className not qualified class name
     * @return Class element or null.
     */
    @Nullable
    public static RClass getClassByName(@NotNull final RContainer container, @Nullable final String className) {
        for (RStructuralElement element :
                selectElementsByType(container.getStructureElements(), StructureType.CLASS)) {
            assert element instanceof RClass;
            final RClass rClass = (RClass) element;
            if (rClass.getName().equals(className)){
                return rClass;
            }
        }
        return null;
    }

    /**
     * Searches first class among cached container children with name
     * <code>className</code> (search algorithm isn't recursive).
     * @param container container, i.e cached ruby class, module of file
     * @param className not qualified class name
     * @return Class element or null.
     */
    @Nullable
    public static RVirtualClass getVClassByName(@NotNull final RVirtualContainer container, 
                                               @Nullable final String className) {
        for (RVirtualStructuralElement element :
                selectVirtualElementsByType(container.getVirtualStructureElements(), StructureType.CLASS)) {
            final RVirtualClass rClass = (RVirtualClass) element;
            if (rClass.getName().equals(className)){
                return rClass;
            }
        }
        return null;
    }

    /**
     * Searches first method(not static then static) among container children with name
     * <code>methodName</code> (search algorithm isn't recursive).
     * @param container container, i.e ruby class, module of file
     * @param methodName method name
     * @return Method element or null.
     */
    @Nullable
    public static RMethod getMethodByName(@NotNull final RContainer container, @Nullable final String methodName) {
        for (RStructuralElement element : selectElementsByType(container.getStructureElements(), StructureType.METHOD)) {
            assert element instanceof RMethod;
            final RMethod rMethod = (RMethod) element;
            if (rMethod.getName().equals(methodName)){
                return rMethod;
            }
        }
        for (RStructuralElement element : selectElementsByType(container.getStructureElements(), StructureType.SINGLETON_METHOD)) {
            assert element instanceof RMethod;
            final RMethod rMethod = (RMethod) element;
            if (rMethod.getName().equals(methodName)){
                return rMethod;
            }
        }
        return null;
    }

    /**
     * Searches first method among container children with name
     * <code>methodName</code> (search algorithm isn't recursive).
     * @param container container, i.e ruby class, module of file
     * @param moduleName not qualified module name
     * @return Method element or null.
     */
    @Nullable
    public static RModule getModuleByName(@NotNull final RContainer container, @Nullable final String moduleName) {
        for (RStructuralElement element : selectElementsByType(container.getStructureElements(), StructureType.MODULE)) {
            assert element instanceof RModule;
            final RModule rModule = (RModule) element;
            if (rModule.getName().equals(moduleName)){
                return rModule;
            }
        }
        return null;
    }

    /**
     * Checks if element has parentContainer of type containerType
     * @param containerType type of RContainer
     * @return true, if belongs
     * @param element RPsiElement to check
     */
    public static boolean belongsToRContainer(@NotNull final RPsiElement element,
                                              @NotNull final StructureType containerType) {
        final RContainer container = PsiTreeUtil.getParentOfType(element, RContainer.class);
        //noinspection SimplifiableIfStatement
        if (container == null){
            return false;
        }
        return container.getType() == containerType;
    }

    @NotNull
    public static List<RStructuralElement> getStructureElements(@NotNull final RContainer container) {
        final Ref<AccessModifier> scopeAccessModifier = new Ref<AccessModifier>(container.getDefaultChildAccessModifier());
        final List<RStructuralElement> elements = new ArrayList<RStructuralElement>();

// Adding all the subcontainers with default scope access modifiers
        RubyStructureVisitor myVisitor = new RubyStructureVisitor() {

            @Override
			public void visitRCall(RCall rCall) {
                if (rCall.getType().isStructureCall()){
                    elements.add(rCall);
                }
            }

            @Override
			public void visitRAliasStatement(RAliasStatement rAliasStatement) {
                elements.add(rAliasStatement);
            }

            @Override
			public void visitContainer(RContainer rContainer) {
                ((RContainerBase) rContainer).setAccessModifier(scopeAccessModifier.get());
                elements.add(rContainer);
            }

            @Override
			public void visitRIdentifier(RIdentifier rIdentifier){
// Processing single command statements like private, public, protected, that changes the default container access_attributes
                if (RCompoundStatementNavigator.getByPsiElement(rIdentifier) != null) {
                    AccessModifier mod = AccessModifiersUtil.getModifierByName(rIdentifier.getText());
                    if (mod != AccessModifier.UNKNOWN) {
                        scopeAccessModifier.set(mod);
                    }
                }
            }

        };
        container.acceptChildren(myVisitor);

        RubySystemCallVisitor callVisitor = new RubySystemCallVisitor(){
            @Override
			public void visitPublicCall(@NotNull RCall rCall) {
                setAccessModifiers(elements, rCall.getArguments(), AccessModifier.PUBLIC);
            }

            @Override
			public void visitProtectedCall(@NotNull RCall rCall) {
                setAccessModifiers(elements, rCall.getArguments(), AccessModifier.PROTECTED);
            }

            @Override
			public void visitPrivateCall(@NotNull RCall rCall) {
                setAccessModifiers(elements, rCall.getArguments(), AccessModifier.PRIVATE);
            }

        };
        container.acceptChildren(callVisitor);

        return elements;
    }


    @Nullable
    private static RContainerBase getContainerByName(final List<RStructuralElement> list, @NotNull final String name) {
        for (RStructuralElement element : list) {
            if (element.getType().isContainer()) {
                final RContainerBase container = (RContainerBase) element;
                if (name.equals(container.getName())) {
                    return container;
                }
            }
        }
        return null;
    }

    private static void setAccessModifiers(@NotNull final List<RStructuralElement> list,
                                           @NotNull final List<RPsiElement> args,
                                           final AccessModifier modifier) {
        for (RPsiElement arg : args) {
// Symbol processing
            if (arg instanceof RSymbol) {
                final PsiElement symbolObject = ((RSymbol) arg).getObject();
                RContainer container = getContainerByName(list, symbolObject.getText());
                if (container != null) {
                    ((RContainerBase) container).setAccessModifier(modifier);
                }
            }
// String like processing
            if (arg instanceof RStringLiteral && !((RStringLiteral)arg).hasExpressionSubstitutions()) {
                RContainer container = getContainerByName(list, ((RStringLiteral)arg).getContent());
                if (container != null) {
                    ((RContainerBase) container).setAccessModifier(modifier);
                }
            }
        }
    }

    /**
     * Returns classes of container and of all it modules and submodules.
     * @param container Container for classes.
     * @return list of RVirtualClasses
     */
    @NotNull
    public static List<RVirtualClass> getTopLevelClasses(@NotNull final RVirtualContainer container) {
        List<RVirtualClass> allClasses = new ArrayList<RVirtualClass>();
        gatherClasses(container, allClasses);
        return allClasses;
    }

    private static void gatherClasses(@NotNull final RVirtualContainer container,
                                      @NotNull final List<RVirtualClass> allClasses) {
        for (RVirtualStructuralElement element : selectVirtualElementsByType(container.getVirtualStructureElements(), StructureType.CLASS)) {
            assert element instanceof RVirtualClass;
            allClasses.add((RVirtualClass) element);
        }
        for (RVirtualStructuralElement element : selectVirtualElementsByType(container.getVirtualStructureElements(), StructureType.MODULE)) {
            assert element instanceof RVirtualModule;
            gatherClasses((RVirtualModule) element, allClasses);
        }
    }

    public static List<RVirtualModule> getTopLevelModules(@NotNull final RVirtualContainer container) {
        final ArrayList<RVirtualModule> modules = new ArrayList<RVirtualModule>();
        for (RVirtualStructuralElement element : selectVirtualElementsByType(container.getVirtualStructureElements(), StructureType.MODULE)) {
            assert element instanceof RVirtualModule;
            modules.add((RVirtualModule) element);
        }
        return modules;
    }


    public static RVirtualClass getFirstClassInFile(@Nullable final VirtualFile file,
                                                     @NotNull final Module module) {
        if (file != null) {
            final RubyModuleCachesManager manager = RubyModuleCachesManager.getInstance(module);
            final RubyFilesCache cache = manager.getFilesCache();
            final RFileInfo info = cache.getUp2DateFileInfo(file);
            if (info != null) {
                final List<RVirtualClass> allClasses =
                        getTopLevelClasses(info.getRVirtualFile());
                if (!allClasses.isEmpty()) {
                    return allClasses.get(0);
                }
            }
        }
        return null;
    }

    public static RVirtualModule getFirstModuleInFile(@Nullable final VirtualFile file,
                                                       @NotNull final Module module) {
        if (file != null) {
            final RubyModuleCachesManager manager = RubyModuleCachesManager.getInstance(module);
            final RubyFilesCache cache = manager.getFilesCache();
            final RFileInfo info = cache.getUp2DateFileInfo(file);
            if (info != null) {
                final List<RVirtualModule> allModules =
                        getTopLevelModules(info.getRVirtualFile());
                if (!allModules.isEmpty()) {
                    return allModules.get(0);
                }
            }
        }
        return null;
    }
}
