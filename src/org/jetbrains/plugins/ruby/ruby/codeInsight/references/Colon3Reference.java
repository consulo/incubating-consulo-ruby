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

package org.jetbrains.plugins.ruby.ruby.codeInsight.references;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubyLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.*;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RSuperClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.classes.RSuperClassNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;
import org.jetbrains.plugins.ruby.ruby.presentation.SymbolPresentationUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Aug 15, 2007
 */
public class Colon3Reference extends RQualifiedReference implements RPsiPolyvariantReference {

    @NotNull
    public List<Symbol> multiResolveToSymbols(@Nullable final FileSymbol fileSymbol) {
        if (fileSymbol == null){
            return Collections.emptyList();
        }
        final Symbol symbol = SymbolUtil.findSymbol(fileSymbol, fileSymbol.getRootSymbol(), myName, true, Types.MODULE_OR_CLASS_OR_CONSTANT);
        if (symbol != null) {
            return Arrays.asList(symbol);
        }
        return Collections.emptyList();
    }

    @NotNull
    @Override
    protected List<Symbol> multiResolveToSymbols(@Nullable final FileSymbol fileSymbol, @NotNull final RType refObjectType) {
        return multiResolveToSymbols(fileSymbol);
    }

    public Object[] getVariants() {
        final FileSymbol fileSymbol = ((RPsiElementBase) myWholeReference).forceFileSymbolUpdate();
        if (fileSymbol == null){
            return EMPTY_ARRAY;
        }

        myWholeReference.putCopyableUserData(REFERENCE_BEING_COMPLETED, Boolean.TRUE);
        try{
            // RUBY-1363. Completion after "class Name <" should show only class names
            final RSuperClass superClass = RSuperClassNavigator.getByPsiElement(myWholeReference);
            final SymbolFilter filter = superClass!=null ?
                    SymbolFilterFactory.CLASSES_ONLY_FILTER :
                    SymbolFilterFactory.EMPTY_FILTER;

            final List<RubyLookupItem> variants = new ArrayList<RubyLookupItem>();
            for (Symbol symbol : fileSymbol.getRootSymbol().getChildren(fileSymbol).getSymbolsOfTypes(Types.MODULE_OR_CLASS_OR_CONSTANT).
                    getChildrenByFilter(filter).getAll()) {
                final String name = symbol.getName();
                if (name != null) {
                    variants.add(SymbolPresentationUtil.createRubyLookupItem(symbol, name, false, false));
                }
            }

            return variants.toArray(new Object[variants.size()]);
        } finally {
            myWholeReference.putCopyableUserData(REFERENCE_BEING_COMPLETED, null);
        }
    }

    public Colon3Reference(@NotNull Project project,
                           @NotNull RPsiElement wholeReference,
                           @NotNull PsiElement refValue) {
        super(project, wholeReference, null, refValue, RReference.Type.COLON_REF, refValue.getText());
    }
}
