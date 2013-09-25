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

package org.jetbrains.plugins.ruby.ruby.lang.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RObjectClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RSingletonMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;

public class RubyStructureViewModel extends TextEditorBasedStructureViewModel {
    private static final Class SUITABLE_CLASSES[] = new Class[]{
                RFile.class, RModule.class,
                RClass.class, RObjectClass.class,
                RMethod.class, RSingletonMethod.class
        };


    private RPsiElement myRoot;

    public RubyStructureViewModel(final RPsiElement root) {
        super(root.getContainingFile());
        myRoot = root;
    }

    @Override
	@NotNull
    public StructureViewTreeElement getRoot() {
        return new RubyStructureViewElement(myRoot);
    }

    @Override
	@NotNull
    public Grouper[] getGroupers() {
        return Grouper.EMPTY_ARRAY;
    }

    @Override
	@NotNull
    public Sorter[] getSorters() {
        return new Sorter[]{Sorter.ALPHA_SORTER};
    }

    @Override
	@NotNull
    public Filter[] getFilters() {
        return Filter.EMPTY_ARRAY;
    }

    @Override
	protected PsiFile getPsiFile() {
        return myRoot.getContainingFile();
    }

    @Override
	@NotNull
    protected Class[] getSuitableClasses() {
        return SUITABLE_CLASSES;
    }
}
