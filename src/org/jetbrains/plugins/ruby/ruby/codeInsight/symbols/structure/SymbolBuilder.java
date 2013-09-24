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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.jruby.codeInsight.resolve.JavaResolveUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.*;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.*;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualConstantHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualFieldHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualGlobalVarHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.*;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.InterpretationMode;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.TypeSet;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RFileUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.FieldType;
import org.jetbrains.plugins.ruby.ruby.roots.RProjectContentRootManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 27, 2007
 */
public class SymbolBuilder {
    private static final Logger LOG = Logger.getInstance(SymbolBuilder.class.getName());

    private InterpretationMode myMode;
    private RVirtualFile myFile;
    private FileSymbol myFileSymbol;

    private enum Context{
        INSTANCE,
        CLASS
    }


    public SymbolBuilder(@NotNull final FileSymbol fileSymbol,
                         @NotNull final RVirtualFile file,
                         final InterpretationMode mode) {
        myFileSymbol = fileSymbol;
        myMode = mode;
        myFile = file;
    }

    public void process() {
        if (myMode == InterpretationMode.EXTERNAL) {
            for (RVirtualRequire require : myFile.getRequires()) {
                for (String name : require.getNames()) {
                    for (String url : RFileUtil.findUrlsForName(myFileSymbol, name, myFile)) {
                        myFileSymbol.process(url, InterpretationMode.FULL, false);
                    }
                }
            }
        } else {
            final Symbol rootSymbol = myFileSymbol.getRootSymbol();
            myFileSymbol.addPrototype(rootSymbol, myFile);
            process(myFile, rootSymbol, Context.INSTANCE);
        }
    }

    private void process(@NotNull final RVirtualElement virtualElement, @NotNull final Symbol symbol, final Context context) {
        if (virtualElement instanceof RVirtualFieldHolder) {
            for (RVirtualField field : ((RVirtualFieldHolder) virtualElement).getVirtualFields()) {
                processField(field, symbol, context);
            }
        }
        if (virtualElement instanceof RVirtualConstantHolder) {
            for (RVirtualConstant constant : ((RVirtualConstantHolder) virtualElement).getVirtualConstants()) {
                processConstant(constant, symbol);
            }
        }
        if (virtualElement instanceof RVirtualGlobalVarHolder) {
            for (RVirtualGlobalVar var : ((RVirtualGlobalVarHolder) virtualElement).getVirtualGlobalVars()) {
                processGlobalVar(var, symbol);
            }
        }

        if (virtualElement instanceof RVirtualContainer) {
            for (RVirtualStructuralElement element : ((RVirtualContainer) virtualElement).getVirtualStructureElements()) {
                final StructureType type = element.getType();
                if (type == StructureType.MODULE) {
                    processModule(((RVirtualModule) element), symbol);
                } else
                if (type == StructureType.CLASS) {
                    processClass(((RVirtualClass) element), symbol);
                } else
                if (type == StructureType.OBJECT_CLASS) {
                    processObjectClass(((RVirtualObjectClass) element), symbol);
                } else
                if (type == StructureType.SINGLETON_METHOD) {
                    processSingletonMethod(((RVirtualSingletonMethod) element), symbol);
                } else
                if (type == StructureType.METHOD) {
                    processMethod(((RVirtualMethod) element), symbol, context);
                } else
                if (type == StructureType.ALIAS) {
                    processAlias((RVirtualAlias) element, symbol);
                } else
// require or load
                if (type == StructureType.CALL_REQUIRE || type == StructureType.CALL_LOAD) {
                    if (myMode != InterpretationMode.IGNORE_EXTERNAL) {
                        assert element instanceof RVirtualRequire;
                        processRequireOrLoad((RVirtualRequire) element, symbol, myMode);
                    }
                } else
// include or extend
                if (type == StructureType.CALL_INCLUDE || type == StructureType.CALL_EXTEND) {
                    assert element instanceof RVirtualInclude;
                    processIncludeOrExtend((RVirtualInclude) element, symbol);
                } else
// fieldAttr
                if (type == StructureType.FIELD_ATTR_CALL) {
                    if (element instanceof RVirtualFieldAttr) {
                        processFieldAttr((RVirtualFieldAttr) element, symbol);
                    } else {
// try to get requirements from call
                        final RVirtualFieldAttr fieldAttr = (RVirtualFieldAttr) ((RCall) element).createVirtualCopy(null, null);
                        processFieldAttr(fieldAttr, symbol);
                    }
                } else
// JRuby specific include java class
                if (type == StructureType.CALL_IMPORT){
                    processImportJavaClass((RVirtualImportJavaClass) element, symbol);
                } else
                if (type == StructureType.CALL_INCLUDE_CLASS){
                    processIncludeJavaClass((RVirtualIncludeJavaClass) element, symbol);
                } else
                if (type == StructureType.CALL_INCLUDE_PACKAGE){
                    processIncludeJavaPackage((RVirtualIncludeJavaPackage) element, symbol);
                } else {
                    LOG.error("Cannot process: " + element);
                }
            }

        }
    }

    public void registerContainerSymbol(@NotNull final RVirtualContainer container, @NotNull final Symbol symbol){
        myFileSymbol.registerContainerSymbol(container, symbol);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// Processing virtual containers
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void processModule(@NotNull final RVirtualModule rVirtualModule,
                               @NotNull final Symbol symbol) {
        final List<String> path = rVirtualModule.getFullPath();
        final Symbol moduleSymbol = SymbolCoreUtil.create(myFileSymbol, symbol, path, rVirtualModule.isGlobal(), Type.MODULE, rVirtualModule);
        registerContainerSymbol(rVirtualModule, moduleSymbol);
        process(rVirtualModule, moduleSymbol, Context.INSTANCE);
    }

    private void processClass(@NotNull final RVirtualClass rVirtualClass,
                              @NotNull final Symbol symbol) {
// creating superclass
        Symbol superClassSymbol = null;
        final RVirtualName superClass = rVirtualClass.getVirtualSuperClass();
        if (superClass != null) {
            superClassSymbol = findJRubyOrRuby(symbol, superClass, superClass, new TypeSet(Type.CLASS, Type.JAVA_PROXY_CLASS));
        }

// creating class
        final List<String> path = rVirtualClass.getFullPath();
        final Symbol classSymbol = SymbolCoreUtil.create(myFileSymbol, symbol, path, rVirtualClass.isGlobal(), Type.CLASS, rVirtualClass);
        if (superClassSymbol != null) {
            myFileSymbol.addChild(classSymbol, new SpecialSymbol(myFileSymbol, classSymbol, superClassSymbol, Type.SUPERCLASS));
        }

        registerContainerSymbol(rVirtualClass, classSymbol);
        process(rVirtualClass, classSymbol, Context.INSTANCE);
    }

    private void processObjectClass(@NotNull final RVirtualObjectClass rVirtualObjectClass,
                                    @NotNull final Symbol symbol) {
        final String name = rVirtualObjectClass.getName();
        final Symbol objectToAdd = SymbolCoreUtil.find(myFileSymbol, symbol, Arrays.asList(name), false, true, Types.MODULE_OR_CLASS);
        addPrototypeIfNeeded(objectToAdd, rVirtualObjectClass);

// In this case we should link object class manually
        Symbol symbol2Link = objectToAdd;
        while(symbol2Link!=null && !new TypeSet(Type.FILE, Type.CLASS, Type.MODULE).contains(symbol2Link.getType())){
            symbol2Link = symbol2Link.getParentSymbol();
        }
        assert symbol2Link!=null;
        registerContainerSymbol(rVirtualObjectClass, objectToAdd);
        process(rVirtualObjectClass, objectToAdd, Context.CLASS);
    }

    private void processSingletonMethod(@NotNull final RVirtualSingletonMethod rVirtualSingletonMethod,
                                        @NotNull final Symbol symbol) {
        final List<String> path = rVirtualSingletonMethod.getFullPath();
        final Symbol singletonMethodSymbol;
        final String anObject = path.get(0);
// self and class name processing
        if (path.size() == 2 && anObject.equals(RubyTokenTypes.kSELF.toString())) {
            final List<String> name = new ArrayList<String>(1);
            name.add(rVirtualSingletonMethod.getName());
            singletonMethodSymbol = SymbolCoreUtil.create(myFileSymbol, symbol, name, false, Type.CLASS_METHOD, rVirtualSingletonMethod);
        } else {
            singletonMethodSymbol = SymbolCoreUtil.create(myFileSymbol, symbol, path, false, Type.CLASS_METHOD, rVirtualSingletonMethod);
        }
        myFileSymbol.setEmptyChildren(singletonMethodSymbol);
        addParameters(rVirtualSingletonMethod, singletonMethodSymbol);
        registerContainerSymbol(rVirtualSingletonMethod, singletonMethodSymbol);
        process(rVirtualSingletonMethod, singletonMethodSymbol, Context.INSTANCE);
    }

    private void processMethod(@NotNull final RVirtualMethod rVirtualMethod,
                               @NotNull final Symbol symbol,
                               final Context context) {
        final String name = rVirtualMethod.getName();
        final Type type = context == Context.INSTANCE && !RMethod.INITIALIZE.equals(name) ? Type.INSTANCE_METHOD : Type.CLASS_METHOD;
        final Symbol methodSymbol = SymbolCoreUtil.create(myFileSymbol, symbol, Arrays.asList(name), false, type, rVirtualMethod);
        myFileSymbol.setEmptyChildren(methodSymbol);
        addParameters(rVirtualMethod, methodSymbol);
        registerContainerSymbol(rVirtualMethod, methodSymbol);
        process(rVirtualMethod, methodSymbol, Context.INSTANCE);
    }

    private void addParameters(@NotNull final RVirtualMethod method, @NotNull final Symbol symbol) {
        for (ArgumentInfo arg : method.getArgumentInfos()) {
            final Type type = getArgumentType(arg.getType());
            myFileSymbol.addChild(symbol, new Symbol(myFileSymbol, arg.getName(), type, symbol, null));
        }
    }

    private void processField(@NotNull final RVirtualField field,
                              @NotNull final Symbol symbol,
                              final Context context) {
        if (field.getType() == FieldType.CLASS_VARIABLE && context==Context.CLASS) {
            return;
        }

        Type type;
        if (field.getType() == FieldType.CLASS_VARIABLE) {
            type = Type.CLASS_FIELD;
        } else if (field.getType() == FieldType.INSTANCE_VARIABLE && context==Context.CLASS) {
            type = Type.CLASS_INSTANCE_FIELD;
        } else {
            type = Type.INSTANCE_FIELD;
        }
        myFileSymbol.addChild(symbol, new Symbol(myFileSymbol, field.getName(), type, symbol, field));
    }

    private void processGlobalVar(@NotNull final RVirtualGlobalVar var,
                                  @NotNull final Symbol symbol) {
        myFileSymbol.addChild(symbol, new Symbol(myFileSymbol, var.getText(), Type.GLOBAL_VARIABLE, symbol, var));
    }

    private void processConstant(@NotNull final RVirtualConstant constant,
                                 @NotNull final Symbol symbol) {
        myFileSymbol.addChild(symbol, new Symbol(myFileSymbol, constant.getName(), Type.CONSTANT, symbol, constant));
    }

    private void processIncludeOrExtend(@NotNull final RVirtualInclude include,
                                        @NotNull final Symbol symbol) {
        final Type type = include.getType() == StructureType.CALL_INCLUDE ? Type.INCLUDE : Type.EXTEND;
        for (RVirtualName path : include.getNames()) {
            final Symbol includeSymbol = findJRubyOrRuby(symbol, include, path, new TypeSet(Type.MODULE, Type.JAVA_PROXY_CLASS));
            // adding to symbol
            if (includeSymbol != null) {
                myFileSymbol.addChild(symbol, new SpecialSymbol(myFileSymbol, symbol, includeSymbol, type));
            }
        }
    }

    private void processFieldAttr(@NotNull final RVirtualFieldAttr fieldAttr,
                                  @NotNull final Symbol symbol) {
        final FieldAttrType type = fieldAttr.getFieldAttrType();
        for (String name : fieldAttr.getNames()) {
            final Symbol field = SymbolCoreUtil.find(myFileSymbol, symbol, Arrays.asList(name), false, true, Types.FIELDS);
            final RVirtualElement prototype = field.getLastVirtualPrototype(myFileSymbol);
            if (type == FieldAttrType.ATTR_READER || type == FieldAttrType.ATTR_ACCESSOR){
                final SpecialSymbol reader = new SpecialSymbol(myFileSymbol, name, symbol, field, Type.FIELD_READER);
                if (prototype!=null){
                    myFileSymbol.addPrototype(reader, prototype);
                }
                myFileSymbol.addPrototype(reader, fieldAttr);
                myFileSymbol.addChild(symbol, reader);
            }
            if (type == FieldAttrType.ATTR_WRITER || type == FieldAttrType.ATTR_ACCESSOR){
                final SpecialSymbol writer = new SpecialSymbol(myFileSymbol, name + "=", symbol, field, Type.FIELD_WRITER);
                if (prototype!=null){
                    myFileSymbol.addPrototype(writer, prototype);
                }
                myFileSymbol.addPrototype(writer, fieldAttr);
                myFileSymbol.addChild(symbol, writer);
            }
            if (type == FieldAttrType.ATTR_INTERNAL){
                final SpecialSymbol internal = new SpecialSymbol(myFileSymbol, name, symbol, field, Type.ATTR_INTERNAL);
                if (prototype!=null){
                    myFileSymbol.addPrototype(internal, prototype);
                }
                myFileSymbol.addPrototype(internal, fieldAttr);
                myFileSymbol.addChild(symbol, internal);
            }
            if (type == FieldAttrType.CATTR_ACCESSOR){
                final SpecialSymbol caccessor = new SpecialSymbol(myFileSymbol, name, symbol, field, Type.CATTR_ACCESSOR);
                if (prototype!=null){
                    myFileSymbol.addPrototype(caccessor, prototype);
                }
                myFileSymbol.addPrototype(caccessor, fieldAttr);
                myFileSymbol.addChild(symbol, caccessor);
            }
        }
    }

    private void processAlias(@NotNull final RVirtualAlias alias,
                              @NotNull final Symbol symbol) {
// Searching in parent!
        final Symbol original = SymbolCoreUtil.find(myFileSymbol, symbol, Arrays.asList(alias.getOldName()), false, true, Types.ALIAS_OBJECTS);
        addPrototypeIfNeeded(original, alias);
        final Symbol parent = original.getParentSymbol();
        if (parent!=null){
            final SpecialSymbol aliasSymbol = new SpecialSymbol(myFileSymbol, alias.getNewName(), parent, original, Type.ALIAS);
            final RVirtualElement prototype = original.getLastVirtualPrototype(myFileSymbol);
            if (prototype!=null){
                myFileSymbol.addPrototype(aliasSymbol, prototype);
            }
            myFileSymbol.addPrototype(aliasSymbol, alias);
            myFileSymbol.addChild(parent, aliasSymbol);
        }
    }

    private void processRequireOrLoad(@NotNull final RVirtualRequire require,
                                      @NotNull final Symbol symbol,
                                      @NotNull final InterpretationMode mode) {
        final RProjectContentRootManager manager =
                RProjectContentRootManager.getInstance(symbol.getProject());

        for (String name : require.getNames()) {
            for (String url : RFileUtil.findUrlsForName(myFileSymbol, name, myFile)) {
                if (mode == InterpretationMode.ONLY_TESTS_EXTERNAL) {
                    if (manager.isUnderTestUnitRoot(url)) {
                        myFileSymbol.process(url, mode, false);
                    }
                } else {
                    myFileSymbol.process(url, InterpretationMode.FULL, false);
                }
            }
        }
    }

    private void processImportJavaClass(@NotNull final RVirtualImportJavaClass importJavaClass,
                                        @NotNull final Symbol symbol) {
        final Symbol context = SymbolUtil.getClassModuleFileSymbol(symbol);
        LOG.assertTrue(context!=null, "Context cannot be null");
        for (RVirtualName path : importJavaClass.getNames()) {
            final PsiElement clazzz = JavaResolveUtil.getPackageOrClass(symbol.getProject(), path.getPath());
            if (clazzz instanceof PsiClass){
                myFileSymbol.addChild(context, new ProxyJavaSymbol(myFileSymbol, path.getName(), clazzz, context, importJavaClass));
            }
        }
    }

    private void processIncludeJavaClass(@NotNull final RVirtualIncludeJavaClass includeJavaClass,
                                         @NotNull final Symbol symbol) {
        final Symbol context = SymbolUtil.getClassModuleFileSymbol(symbol);
        LOG.assertTrue(context!=null, "Context cannot be null");
        final String fullName = includeJavaClass.getQualifiedName();
        final PsiElement clazzz = fullName!=null ? JavaResolveUtil.getPackageOrClass(symbol.getProject(), fullName) : null;
        if (clazzz instanceof PsiClass){
            myFileSymbol.addChild(context, new ProxyJavaSymbol(myFileSymbol, ((PsiClass) clazzz).getName(), clazzz, context, includeJavaClass));
        }
    }

    private void processIncludeJavaPackage(@NotNull final RVirtualIncludeJavaPackage includeJavaPackage,
                                           @NotNull final Symbol symbol) {
        final Symbol context = SymbolUtil.getClassModuleFileSymbol(symbol);
        LOG.assertTrue(context!=null, "Context cannot be null");
        final String fullName = includeJavaPackage.getQualifiedName();
        final PsiElement psiPackage = fullName!=null ? JavaResolveUtil.getPackageOrClass(symbol.getProject(), fullName) : null;
        if (psiPackage instanceof PsiPackage){
            for (PsiClass clazzz : ((PsiPackage) psiPackage).getClasses()) {
                myFileSymbol.addChild(context, new ProxyJavaSymbol(myFileSymbol, clazzz.getName(), clazzz, context, includeJavaPackage));
            }
        }
    }

    private Symbol findJRubyOrRuby(@NotNull final Symbol symbol,
                                   @NotNull final RVirtualElement prototype,
                                   @NotNull final RVirtualName path,
                                   final TypeSet typeSet){
        final List<String> fullPath = path.getPath();
        // Try to find JRuby
        if (myFileSymbol.isJRubyEnabled() && !path.isGlobal() && TextUtil.isIdentifier(fullPath.get(0))){
            return SymbolUtil.findJRubySymbol(symbol.getProject(), fullPath);
        }
        // Searching in parent!
        final Symbol includeSymbol = SymbolCoreUtil.find(myFileSymbol, symbol, fullPath, path.isGlobal(), true, typeSet);
        addPrototypeIfNeeded(includeSymbol, prototype);
        return includeSymbol;
    }

    private void addPrototypeIfNeeded(@NotNull final Symbol symbol, @NotNull final RVirtualElement element) {
        if (symbol.getType() == Type.NOT_DEFINED){
            myFileSymbol.addPrototype(symbol, element);
        }
    }

    private Type getArgumentType(final ArgumentInfo.Type type) {
        if (type == ArgumentInfo.Type.SIMPLE) {
            return Type.ARG_SIMPLE;
        }
        if (type == ArgumentInfo.Type.PREDEFINED) {
            return Type.ARG_PREDEFINED;
        }
        if (type == ArgumentInfo.Type.ARRAY) {
            return Type.ARG_ARRAY;
        }
        if (type == ArgumentInfo.Type.BLOCK) {
            return Type.ARG_BLOCK;
        }
        throw new IllegalArgumentException("Wrong parameter type");
    }

}
