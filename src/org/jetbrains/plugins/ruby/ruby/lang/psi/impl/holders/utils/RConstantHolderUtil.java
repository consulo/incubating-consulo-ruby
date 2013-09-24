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
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualConstant;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.ConstantDefinitions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RConstantHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.ConstantDefinitionsImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 08.08.2006
 */
public class RConstantHolderUtil {

    /**
     * Adds new rConstant to List of RConstants
     * @param list List of RConstants, already found
     * @param rConstant constant expression to add
     */
    private static void addDefinition(@NotNull final List<ConstantDefinitions> list,
                                      @NotNull final RConstant rConstant){
        for (ConstantDefinitions f: list){
            if (f.isFor(rConstant)){
                return;
            }
        }

        list.add(new ConstantDefinitionsImpl(rConstant));
    }

    @NotNull
    public static List<ConstantDefinitions> gatherConstantDefinitions(final RConstantHolder holder) {
        final ArrayList<ConstantDefinitions> list = new ArrayList<ConstantDefinitions>();
        RubyElementVisitor myVisitor = new RubyElementVisitor() {

            public void visitRConstant(RConstant rConstant){
                if (rConstant.isInDefinition()){
                    addDefinition(list, rConstant);
                }
            }

            public void visitElement(PsiElement element) {
                if (element instanceof RConstantHolder){
                    return;
                }
                element.acceptChildren(this);
            }

        };

        holder.acceptChildren(myVisitor);
        return list;
    }

    private static ConstantDefinitions getConstantDefinition(final List<ConstantDefinitions> definitions,
                                                            final RVirtualConstant constant) {
        for (ConstantDefinitions def : definitions) {
            if (def.isFor(constant)) {
                return def;
            }
        }
        return null;
    }

    public static ConstantDefinitions getDefinition(RConstantHolder holder, RVirtualConstant constant) {
        return getConstantDefinition(holder.getConstantDefinitions(), constant);
    }
}
