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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.util.containers.HashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.codeInsight.resolve.JavaResolveUtil;
import org.jetbrains.plugins.ruby.jruby.codeInsight.types.JRubyDuckTypeUtil;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.*;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data.Children;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.CoreTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Oct 15, 2007
 */
public class SymbolUtil {

    private static final String SYMBOL_DELIMITER = "::";
    /**
     * Returns the path of symbol
     *
     * @param symbol Symbol to get full path for
     * @return List of strings
     */
    @Nullable
    private static List<String> getPath(Symbol symbol) {
        final ArrayList<String> list = new ArrayList<String>();
        while (symbol != null && symbol.getType() != Type.FILE) {
            final String name = symbol.getName();
            if (name == null) {
                return null;
            }
            list.add(0, name);
            symbol = symbol.getParentSymbol();
        }
        return !list.isEmpty() ? list : null;
    }

    /**
     * Returns the path of symbol
     *
     * @param symbol Symbol to get full path for
     * @return List of strings
     */
    @Nullable
    public static String getPresentablePath(@NotNull final Symbol symbol) {
        final List<String> path = getPath(symbol);
        final StringBuilder buffer = new StringBuilder();
        if (path == null) {
            return null;
        }

        for (String s : path) {
            if (buffer.length() > 0) {
                buffer.append(SYMBOL_DELIMITER);
            }
            buffer.append(s);
        }
        return buffer.toString();
    }


    public static Children addAllChildrenWithSuperClassesAndIncludes(@Nullable final FileSymbol fileSymbol,
                                                                     @NotNull final Children children,
                                                                     @NotNull final Context context,
                                                                     @NotNull final Symbol symbol,
                                                                     @Nullable final Symbol before) {
        addAllChildrenWithSuperClassesAndIncludesRec(fileSymbol, symbol, children, before, context, new HashSet<Symbol>());
        return children;
    }

    /**
     * Adds all the children of newChild including superclasses children
     *
     * @param fileSymbol FileSymbol
     * @param symbol     Root newChild
     * @param children   Children to add to
     * @param before     Anchor symbol. We gather info before we meet anchorSymbol
     * @param context   Static or instance context
     * @param set        Set of visited symbols to prevent cycling
     */
    private static void addAllChildrenWithSuperClassesAndIncludesRec(@Nullable final FileSymbol fileSymbol,
                                                                     @NotNull final Symbol symbol,
                                                                     @NotNull final Children children,
                                                                     @Nullable final Symbol before,
                                                                     @NotNull final Context context,
                                                                     @NotNull final Set<Symbol> set) {
// Check to prevent cycling
        if (fileSymbol == null || set.contains(symbol)) {
            return;
        }
        set.add(symbol);

        // JRuby types handling
        if (symbol.getType() == Type.JAVA_CLASS) {
            final PsiElement element = ((JavaSymbol) symbol).getPsiElement();
            assert element instanceof PsiClass;
            children.addChildren(JRubyDuckTypeUtil.getChildrenByJavaClass(fileSymbol, (PsiClass) element, context));
            return;
        }

        if (symbol.getType() == Type.JAVA_PROXY_CLASS) {
            final PsiElement element = ((ProxyJavaSymbol) symbol).getPsiElement();
            if (element instanceof PsiClass) {
                children.addChildren(JRubyDuckTypeUtil.getChildrenByJavaClass(fileSymbol, (PsiClass) element, context));
            }
            return;
        }
        final Children symbolChildren = symbol.getChildren(fileSymbol);

// superclass handling
        for (Symbol child : symbolChildren.getSymbolsOfTypes(Type.SUPERCLASS.asSet()).getAll()) {
            addAllChildrenWithSuperClassesAndIncludesRec(fileSymbol, child.getLinkedSymbol(), children, before, context, set);
        }

// include handling
        for (Symbol child : symbolChildren.getSymbolsOfTypes(Type.INCLUDE.asSet()).getAll()) {
            addAllChildrenWithSuperClassesAndIncludesRec(fileSymbol, child.getLinkedSymbol(), children, before, context, set);
        }

// extend handling
        if (context != Context.INSTANCE) {
            for (Symbol child : symbolChildren.getSymbolsOfTypes(Type.EXTEND.asSet()).getAll()) {
                addAllChildrenWithSuperClassesAndIncludesRec(fileSymbol, child.getLinkedSymbol(), children, before, Context.INSTANCE, set);
            }
        }

        // Here we process this symbol`s own children
        for (Symbol child : symbolChildren.getAll()) {
            // Check for the anchor
            if (child == before) {
                return;
            }
            final Type type = child.getType();
            if (type != Type.SUPERCLASS && type != Type.INCLUDE && type != Type.EXTEND) {
                // Context check!
                if (context == Context.CLASS){
                    if (Types.STATIC_TYPES.contains(type)){
                        children.addSymbol(child);
                    }
                } else
                if (context == Context.INSTANCE){
                    if (Types.INSTANCE_TYPES.contains(type)){
                        children.addSymbol(child);
                    }
                } else {
                    children.addSymbol(child);
                }
            }
        }
    }

    @Nullable
    public static Symbol getTopLevelClassByName(@Nullable final FileSymbol fileSymbol, @NotNull final String s) {
        if (fileSymbol == null) {
            return null;
        }
        return fileSymbol.getChildren(fileSymbol.getRootSymbol()).getSymbolByNameAndTypes(s, Type.CLASS.asSet());
    }

    @Nullable
    public static Symbol getTopLevelModuleByName(@Nullable final FileSymbol fileSymbol, @NotNull final String s) {
        if (fileSymbol == null) {
            return null;
        }
        return fileSymbol.getChildren(fileSymbol.getRootSymbol()).getSymbolByNameAndTypes(s, Type.MODULE.asSet());
    }

    @NotNull
    public static ArrayList<Symbol> filterMethods(@NotNull final List<Symbol> symbols, final TypeSet typeSet) {
        final ArrayList<Symbol> methods = new ArrayList<Symbol>();
        for (Symbol method : symbols) {
            if (typeSet.contains(method.getType())) {
                methods.add(method);
            }
        }
        return methods;
    }

    @NotNull
    public static Children getAllChildrenWithSuperClassesAndIncludes(@Nullable FileSymbol fileSymbol,
                                                                     @NotNull final Context context,
                                                                     @NotNull final Symbol symbol,
                                                                     @Nullable final Symbol before) {
        final Children children = new Children(null);

        // add base Object or Class or Module symbols
        final Type type = symbol.getType();
        includeTopLevelClassSymbol(fileSymbol, children, context, CoreTypes.Object);

        if (type == Type.CLASS) {
            includeTopLevelClassSymbol(fileSymbol, children, context, CoreTypes.Class);
        } else
        if (type == Type.MODULE) {
            includeTopLevelClassSymbol(fileSymbol, children, context, CoreTypes.Module);
        }
        // add it`s own symbols
        addAllChildrenWithSuperClassesAndIncludes(fileSymbol, children, context, symbol, before);

        return children;
    }

    /*
     * Tryes to find top level class with given name and includes it`s content to children
     * if symbols was found
     */
    public static void includeTopLevelClassSymbol(@Nullable final FileSymbol fileSymbol,
                                                  @NotNull final Children children,
                                                  @NotNull final Context context,
                                                  @NotNull final String name) {
        if (fileSymbol == null) {
            return;
        }
        final Symbol symbolToInclude = SymbolUtil.getTopLevelClassByName(fileSymbol, name);
        if (symbolToInclude != null) {
            SymbolUtil.addAllChildrenWithSuperClassesAndIncludes(fileSymbol, children, context, symbolToInclude, null);
        }
    }

    /*
     * Tryes to find top level module with given name and includes it`s content to children
     * if symbols was found
     */
    public static void includeTopLevelModuleSymbol(@Nullable final FileSymbol fileSymbol,
                                                   @NotNull final Children children,
                                                   @NotNull final Context context,
                                                   @NotNull final String name) {
        if (fileSymbol == null) {
            return;
        }
        final Symbol symbolToInclude = SymbolUtil.getTopLevelModuleByName(fileSymbol, name);
        if (symbolToInclude != null) {
            SymbolUtil.addAllChildrenWithSuperClassesAndIncludes(fileSymbol, children, context, symbolToInclude, null);
        }
    }

    /**
     * It is macros for getSymbolByContainer(container, isLightRubyTestMode)
     * where isLightRubyTestMode is false.
     *
     * @param fileSymbol FileSymbol
     * @param container Psi or Cached Container class
     * @return Symbol or null
     */
    @Nullable
    public static Symbol getSymbolByContainer(@Nullable FileSymbol fileSymbol,
                                              @NotNull final RVirtualContainer container) {
        if (fileSymbol == null) {
            return null;
        }

        final RVirtualContainer key;
        if (container instanceof RContainer) {
            key = RVirtualPsiUtil.findVirtualContainer((RContainer) container);
        } else {
            key = container;
        }

        if (container instanceof RVirtualFile) {
            return fileSymbol.getRootSymbol();
        }
        return fileSymbol.getSymbolForContainer(key);
    }

    /**
     * @param container         Psi or Virtual container
     * @param fileSymbolWrapper if null nothing will happen. If wrapper contains
     *                          not null value, this value will be used for evaluating container symbol, otherwise method
     *                          will store evaluated ruby mode symbol.
     * @return pair with Symbol and not null FileSymbol or null
     */
    @Nullable
    public static Pair<Symbol, FileSymbol> getSymbolByContainerRubyTestMode(@NotNull final RVirtualContainer container,
                                                                            @Nullable final Ref<FileSymbol> fileSymbolWrapper) {
        final RVirtualFile file;
        final RVirtualContainer key;

        //key and file must be pure virtual elements
        if (container instanceof RContainer) {
            key = RVirtualPsiUtil.findVirtualContainer((RContainer) container);
            final RFile rFile = RubyPsiUtil.getRFile((RContainer) container);
            assert rFile != null; //can't be null here
            file = (RVirtualFile) RVirtualPsiUtil.findVirtualContainer(rFile);
        } else {
            // virtual element
            final RFileInfo info = container.getContainingFileInfo();
            assert info != null; // not null for virtual elements
            file = info.getRVirtualFile();
            key = container;
        }


        if (file == null) {
            return null;
        }

        FileSymbol fileSymbol = (fileSymbolWrapper != null)
                ? fileSymbolWrapper.get()
                : null;

        if (fileSymbol == null) {
            fileSymbol = FileSymbolUtil.getFileSymbol(file, true);
        }

        if (fileSymbol == null) {
            return null;
        }

        if (fileSymbolWrapper != null) {
            fileSymbolWrapper.set(fileSymbol);
        }

        final Symbol symbol;
        if (key instanceof RVirtualFile) {
            symbol = fileSymbol.getRootSymbol();
        } else {
            symbol = fileSymbol.getSymbolForContainer(key);
        }
        return new Pair<Symbol, FileSymbol>(symbol, fileSymbol);
    }


    @Nullable
    public static Symbol getClassModuleFileSymbol(@Nullable Symbol symbol) {
        while (symbol != null && !new TypeSet(Type.FILE, Type.MODULE, Type.CLASS).contains(symbol.getType())) {
            symbol = symbol.getParentSymbol();
        }
        return symbol;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Gather all symbols and overriden symbols
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gathers all the symbols of root symbol
     *
     * @param fileSymbol FileSymbol
     * @return Collection of symbols
     */
    public static Children gatherAllSymbols(@Nullable final FileSymbol fileSymbol) {
        // It`s called often enough
        ProgressManager.getInstance().checkCanceled();
        
        if (fileSymbol != null) {
            Children allSymbols = fileSymbol.getAllSymbols();
            if (allSymbols == null) {
                allSymbols = gatherAllSymbols(fileSymbol, fileSymbol.getRootSymbol());
                fileSymbol.setAllSymbols(allSymbols);
            }
            return allSymbols;
        }
        return Children.EMPTY_CHILDREN;
    }

    private static Children gatherAllSymbols(@NotNull final FileSymbol fileSymbol,
                                             @NotNull final Symbol symbol) {
        fileSymbol.getAllSymbols();
        final HashSet<Symbol> visitedSet = new HashSet<Symbol>();
        final Children children = new Children(null);
        gatherAllSymbolsRec(fileSymbol, symbol, children, visitedSet);
        return children;
    }

    private static void gatherAllSymbolsRec(@NotNull final FileSymbol fileSymbol,
                                            @NotNull final Symbol symbol,
                                            @NotNull final Children children,
                                            @NotNull final Set<Symbol> symbols) {
        if (symbols.contains(symbol)) {
            return;
        }
        symbols.add(symbol);
        children.addSymbol(symbol);
        for (Symbol childSymbol : symbol.getChildren(fileSymbol).getAll()) {
            gatherAllSymbolsRec(fileSymbol, childSymbol, children, symbols);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Find and Symbols gathering. The scheme is the same with Symbol.findRailsSpecificSymbol
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gathers all the visible symbols within anchorSymbol
     *
     * @param fileSymbol FileSymbol
     * @param symbol    AnchorSymbol
     * @return Children object
     */
    @NotNull
    public static Children gatherOuterSymbols(@NotNull final FileSymbol fileSymbol,
                                              @NotNull final Symbol symbol) {

// we adding children with all superclasses etc
        Children children = new Children(null);

// lifting to the level of module/class/file
        Symbol currentLevelSymbol = symbol.getParentSymbol();
        while (currentLevelSymbol != null) {
            final Children ch = currentLevelSymbol.getChildren(fileSymbol);
            TypeSet typeSet = currentLevelSymbol.getType() != Type.FILE ? Types.MODULE_OR_CLASS_OR_CONSTANT : Types.TOP_LEVEL_AUTOCOMPLETE_TYPES;
            children.addChildren(ch.getSymbolsOfTypes(typeSet));
            currentLevelSymbol = currentLevelSymbol.getParentSymbol();
        }
        return children;
    }

    @Nullable
    public static Symbol findSymbol(@Nullable final FileSymbol fileSymbol,
                                    @NotNull final Symbol anchor,
                                    @NotNull final String name,
                                    final boolean global,
                                    final TypeSet typeSet) {
        return findSymbol(fileSymbol, anchor, Arrays.asList(name), global, typeSet);
    }

    /**
     * Tries to find symbol in current namespace or in global namespace.
     *
     * @param path           Symbol path
     * @param typeSet          List of acceptable types
     * @param global         if true, we search in global namespace
     * @param fileSymbol     FileSymbol context
     * @param anchor         Context symbol
     * @return Symbol
     */
    @Nullable
    public static Symbol findSymbol(@Nullable final FileSymbol fileSymbol,
                                    @NotNull final Symbol anchor,
                                    @NotNull final List<String> path,
                                    final boolean global,
                                    final TypeSet typeSet) {

        if (fileSymbol == null) {
            return null;
        }

        Symbol context = SymbolUtil.getClassModuleFileSymbol(anchor);
        if (context == null) {
            return null;
        }

        // try to find ruby symbol
        final Symbol symbol = SymbolCoreUtil.find(fileSymbol, context, path, global, false, typeSet);
        if (symbol!=null && symbol.getType()!=Type.NOT_DEFINED) {
            return symbol;
        }

        // try to find java class/package if needed for example "java.lang.String"
        if (fileSymbol.isJRubyEnabled()) {
            final JavaSymbol jRubySymbol = findJRubySymbol(anchor.getProject(), path);
            if (jRubySymbol!=null && jRubySymbol.getType()!=Type.NOT_DEFINED){
                return jRubySymbol;
            }
        }

        // try to find rails specific symbol
        return path.size() == 1 ? RailsSymbolUtil.findRailsSpecificSymbol(fileSymbol, context, path.get(0), Context.ALL, typeSet) : null;
    }

    @Nullable
    public static JavaSymbol findJRubySymbol(@NotNull final Project project,
                                             @NotNull final List<String> path) {
        final PsiElement element = JavaResolveUtil.getPackageOrClass(project, path);
        if (element instanceof PsiPackage) {
            final String name = ((PsiPackage) element).getName();
            if (name != null) {
                return new JavaSymbol(element, name, null, Type.JAVA_PACKAGE);
            }
        }
        if (element instanceof PsiClass) {
            final String name = ((PsiClass) element).getName();
            if (name != null) {
                return new JavaSymbol(element, name, null, Type.JAVA_CLASS);
            }
        }
        return null;
    }

    public static boolean equalSymbols(Symbol s1, Symbol s2){
        if (s1==null){
            return s2 == null;
        }
        return s1.equals(s2);
    }
}
