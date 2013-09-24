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

package org.jetbrains.plugins.ruby.jruby.codeInsight.types;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.JavaPsiUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.JavaSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data.Children;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.CoreTypes;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 13, 2007
 */
public class JRubyDuckTypeUtil {
    @NotNull
    public static Children getChildrenByJavaClass(@Nullable final FileSymbol fileSymbol,
                                                  @Nullable final PsiClass clazzz,
                                                  @NotNull final Context context){
        final Children children = new Children(null);

        // Here we add JavaProxyMethods to children
        JRubyExtentionsUtil.addJavaProxyMethods(fileSymbol, children, Context.ALL);

        if (clazzz == null || !clazzz.isValid()){
            return children;
        }

        JRubyExtentionsUtil.extendJavaClassWithStubs(fileSymbol, children, clazzz, context);

        // Reverse order to match Ruby style
        final PsiMethod[] methods = clazzz.getAllMethods();
        for (int i = methods.length-1; i>=0; i--) {
            final PsiMethod method = methods[i];
            // We don`t want to show constructors in autocomplete
            if (method.isConstructor()) {
                continue;
            }
            if (JavaPsiUtil.isStaticMethod(method)) {
                if (context != Context.INSTANCE) {
                    addMethod(children, method);
                }
            } else {
                if (context != Context.CLASS) {
                    addMethod(children, method);
                }
            }
        }

        // Reverse order to match Ruby style
        final PsiField[] fields = clazzz.getAllFields();
        for (int i = fields.length-1; i>=0; i--) {
            final PsiField field = fields[i];
            if (JavaPsiUtil.isStaticField(field)) {
                if (context != Context.INSTANCE) {
                    addField(children, field);
                }
            } else {
                if (context != Context.CLASS) {
                    addField(children, field);
                }
            }
        }

        // Add only if we`re in static context
        if (context != Context.INSTANCE){
            // Reverse order to match Ruby style
            final PsiClass[] allInnerClasses = clazzz.getAllInnerClasses();
            for (int i = allInnerClasses.length-1; i >= 0 ; i--) {
                addClass(children, allInnerClasses[i]);
            }
        }
        return children;
    }

    @NotNull
    public static Children getChildrenByJavaPackage(@Nullable final FileSymbol fileSymbol,
                                                    @Nullable final PsiPackage packaggge){
        final Children children = new Children(null);

        // Here we add JavaProxyMethods to children
        JRubyExtentionsUtil.addJavaProxyMethods(fileSymbol, children, Context.ALL);

        if (packaggge == null || !packaggge.isValid()){
            return children;
        }
        for (PsiPackage subPackage : packaggge.getSubPackages()) {
            addPackage(children, subPackage);
        }
        for (PsiClass psiClass : packaggge.getClasses()) {
            addClass(children, psiClass);
        }
        return children;
    }


    @NotNull
    public static Children getChildrenByJavaMethod(@Nullable final FileSymbol fileSymbol, 
                                                   @NotNull final PsiMethod method) {
        return getChildrenByPsiType(fileSymbol, method.getReturnType());
    }

    public static Children getChildrenByJavaField(@Nullable final FileSymbol fileSymbol,
                                                  @NotNull final PsiField psiField) {
        return getChildrenByPsiType(fileSymbol, psiField.getType());
    }

    @NotNull
    public static Children getChildrenByPsiType(@Nullable final FileSymbol fileSymbol,
                                               @Nullable final PsiType type){
        if (type instanceof PsiClassType){
            final PsiClass psiClass = ((PsiClassType) type).resolve();

// check for JRuby type conventions
            if (psiClass!=null && psiClass.isValid()){
                final String rubyCoreType = JRubyTypeConventions.getRubyType(type.getCanonicalText());
                if (rubyCoreType!=null){
                    return getChildrenByRubyCoreType(fileSymbol, rubyCoreType);
                }
            }
            return getChildrenByJavaClass(fileSymbol, psiClass, Context.ALL);
        }
        if (type instanceof PsiArrayType){
            return getChildrenByRubyCoreType(fileSymbol, CoreTypes.Array);
        }
        if (type instanceof PsiPrimitiveType){
            final String name = type.getCanonicalText();
            final String rubyCoreType = JRubyTypeConventions.getRubyType(name);
            return rubyCoreType!=null ? getChildrenByRubyCoreType(fileSymbol, rubyCoreType) : Children.EMPTY_CHILDREN;

        }
        return Children.EMPTY_CHILDREN;
    }

    @NotNull
    public static Children getChildrenByRubyCoreType(@Nullable final FileSymbol fileSymbol,
                                                     @NotNull final String coreTypeName){
        final Symbol array = SymbolUtil.getTopLevelClassByName(fileSymbol, coreTypeName);
        return array != null ? array.getChildren(fileSymbol) : Children.EMPTY_CHILDREN;
    }

    private static void addPackage(final Children children, final PsiPackage subPackage) {
        final String name = subPackage.getName();
        if (name != null) {
            children.addSymbol(new JavaSymbol(subPackage, name, null, Type.JAVA_PACKAGE));
        }
    }

    private static void addClass(final Children children, final PsiClass psiClass) {
        final String name = psiClass.getName();
        if (name != null) {
            children.addSymbol(new JavaSymbol(psiClass, name, null, Type.JAVA_CLASS));
        }
    }

    private static void addField(final Children children, final PsiField field) {
        final String name = field.getName();
        if (name != null) {
            children.addSymbol(new JavaSymbol(field, name, null, Type.JAVA_FIELD));
        }
    }

    private static void addMethod(final Children children, final PsiMethod method) {
        children.addSymbol(new JavaSymbol(method, method.getName(), JRubyNameConventions.getMethodName(method), Type.JAVA_METHOD));
    }

}
