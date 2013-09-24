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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualSingletonMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVMethodName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualSingletonMethodImpl;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RClassObject;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RSingletonMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class RSingletonMethodImpl extends RMethodImpl implements RSingletonMethod {
    public RSingletonMethodImpl(ASTNode astNode) {
        super(astNode);
    }

    @Nullable
    public RClassObject getClassObject(){
        PsiElement result =  getChildByFilter(RubyElementTypes.CLASS_OBJECT,0);
        return result instanceof RClassObject ?  (RClassObject) result : null;
    }

    public void accept(@NotNull PsiElementVisitor visitor){
        if (visitor instanceof RubyElementVisitor){
            ((RubyElementVisitor) visitor).visitRSingletonMethod(this);
            return;
        }
        super.accept(visitor);
    }

    @NotNull
    public RVirtualSingletonMethod createVirtualCopy(@Nullable final RVirtualContainer virtualParent,
                                               @NotNull RFileInfo info) {
        final RVirtualName virtualMethodName = new RVMethodName(getFullPath(), isGlobal());
        assert virtualParent != null;
        
        final RVirtualSingletonMethodImpl singletonMethod =
                new RVirtualSingletonMethodImpl(virtualParent, virtualMethodName,
                                                getArgumentInfos(),
                                                getAccessModifier(),
                                                info);
        addVirtualData(singletonMethod, info);
        return singletonMethod;
    }

    public boolean isConstructor() {
        return false;
    }

    public StructureType getType() {
        return StructureType.SINGLETON_METHOD;
    }
}
