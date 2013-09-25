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

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualGlobalVar;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.GlobalVarDefinition;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RGlobalVarHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.GlobalVarDefinitionImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.global.RGlobalVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Aug 22, 2007
 */
public class RGlobalVarHolderUtil {

    /**
     * Adds new globalVar to List of RConstants
     * @param list List of GlobalVars, already found
     * @param globalVar constant expression to add
     */
    private static void addDefinition(@NotNull final List<GlobalVarDefinition> list,
                                      @NotNull final RGlobalVariable globalVar){
        for (GlobalVarDefinition f: list){
            if (f.isFor(globalVar)){
                return;
            }
        }

        list.add(new GlobalVarDefinitionImpl(globalVar));
    }

    @NotNull
    public static List<GlobalVarDefinition> gatherGlobalVarDefinitions(final RGlobalVarHolder holder) {
        final ArrayList<GlobalVarDefinition> list = new ArrayList<GlobalVarDefinition>();
        RubyElementVisitor myVisitor = new RubyElementVisitor() {

            @Override
			public void visitRGlobalVariable(RGlobalVariable rGlobalVariable) {
                if (rGlobalVariable.isInDefinition()){
                    addDefinition(list, rGlobalVariable);
                }
            }

            @Override
			public void visitElement(PsiElement element) {
                if (element instanceof RGlobalVarHolder){
                    return;
                }
                element.acceptChildren(this);
            }

        };

        holder.acceptChildren(myVisitor);
        return list;
    }

    private static GlobalVarDefinition getGlobalVarDefinition(final List<GlobalVarDefinition> definitions,
                                                             final RVirtualGlobalVar globalVariable) {
        for (GlobalVarDefinition def : definitions) {
            if (def.isFor(globalVariable)) {
                return def;
            }
        }
        return null;
    }

    public static GlobalVarDefinition getDefinition(RGlobalVarHolder holder, RVirtualGlobalVar globalVar) {
        return getGlobalVarDefinition(holder.getGlobalVarDefinitions(), globalVar);
    }
}
