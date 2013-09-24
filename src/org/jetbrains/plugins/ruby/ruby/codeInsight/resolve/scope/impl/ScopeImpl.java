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

package org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.PseudoScopeHolder;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.Scope;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 4, 2007
 */
public class ScopeImpl implements Scope {
    private HashMap<String, ScopeVariable> myVariables = new HashMap<String, ScopeVariable>();
    private final Object LOCK = new Object();
    private PseudoScopeHolder myHolder;
    private ArrayList<Scope> mySubScopes = new ArrayList<Scope>();

    public ScopeImpl(@NotNull PseudoScopeHolder holder) {
        myHolder = holder;
    }

    @NotNull
    public PseudoScopeHolder getHolder() {
        return myHolder;
    }

    @NotNull
    public Collection<Scope> getSubScopes() {
        return mySubScopes;
    }

    public void addSubScope(@NotNull final Scope scope) {
        mySubScopes.add(scope);
    }

    @NotNull
    public Collection<ScopeVariable> getVariables() {
        synchronized (LOCK) {
            return myVariables.values();
        }
    }

    public void processIdentifier(@NotNull final RIdentifier identifier) {
        final String name = identifier.getText();
        synchronized (LOCK) {
            if (getVariableByName(name) == null){
                myVariables.put(name, new ScopeVariableImpl(name, identifier, identifier.isParameter()));
            }
        }
    }

    @Nullable
    public ScopeVariable getVariableByName(@NotNull final String name) {
        synchronized (LOCK) {
            return myVariables.get(name);
        }
    }

    @NotNull
    public Set<String> getScopeNames() {
        return myVariables.keySet();
    }
}
