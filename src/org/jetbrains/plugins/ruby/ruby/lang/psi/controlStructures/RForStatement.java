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

package org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures;

import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.PseudoScopeHolder;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.models.RWrapAndIndentCOMPSTMT;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.models.wrap.RWrapLastChild;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 07.08.2006
 */
public interface RForStatement extends RPsiElement, PseudoScopeHolder, 
        RFormatStructureElement, RWrapAndIndentCOMPSTMT, RWrapLastChild {
    RPsiElement getVariable();
    RPsiElement getExpression();
    RCompoundStatement getBody();
}
