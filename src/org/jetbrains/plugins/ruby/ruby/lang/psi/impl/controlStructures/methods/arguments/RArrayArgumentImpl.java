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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.arguments;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RArrayArgument;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 9, 2006
 */
public class RArrayArgumentImpl extends RPsiElementBase implements RArrayArgument {
    public RArrayArgumentImpl(ASTNode astNode) {
        super(astNode);
    }

    @Override
	@NotNull
    public String getName(){
        final RIdentifier identifier = getIdentifier();
        //noinspection ConstantConditions
        return identifier!=null ? identifier.getName() : "";
    }

    @Override
	public RIdentifier getIdentifier(){
        return RubyPsiUtil.getChildByType(this, RIdentifier.class, 0);
    }

    @Override
	public ArgumentInfo.Type getType() {
        return ArgumentInfo.Type.ARRAY;
    }

}
