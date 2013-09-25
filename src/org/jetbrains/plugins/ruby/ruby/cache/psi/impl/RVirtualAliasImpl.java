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

package org.jetbrains.plugins.ruby.ruby.cache.psi.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualAlias;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.presentation.RAliasPresentationUtil;

import javax.swing.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 20, 2007
 */
public class RVirtualAliasImpl extends RVirtualStructuralElementBase implements RVirtualAlias, Serializable {
    private String myOldName;
    private String myNewName;

    public RVirtualAliasImpl(final RVirtualContainer container, @NotNull final String oldName, @NotNull final String newName) {
        super(container);
        myOldName = oldName;
        myNewName = newName;
    }

    @Override
	@NotNull
    public String getOldName() {
        return myOldName;
    }

    @Override
	@NotNull
    public String getNewName() {
        return myNewName;
    }

    @Override
	public StructureType getType() {
        return StructureType.ALIAS;
    }

    @Override
	public void accept(@NotNull RubyVirtualElementVisitor visitor) {
        visitor.visitRVirtualAlias(this);
    }

    public String toString() {
        return getPresentableText();
    }

    @Override
	@NotNull
    public String getPresentableText() {
        return RubyTokenTypes.kALIAS.toString() + " '" + getNewName() + "' '"  + getOldName() + "'";
    }

    @Override
	@Nullable
    public Icon getIcon(final int flags) {
        return RAliasPresentationUtil.getIcon();
    }

}
