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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree;

import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.util.CharTable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 11.05.2007
 */

//TODO Refactor with jsp TreePatcher, when it will be available in core API
public interface TreePatcher
{
	/**
	 * Inserts toInsert into destinationTree according to parser rules.
	 *
	 * @param parent       Parent element
	 * @param anchorBefore If not null element will be inserted before it, otherwise as last child in parent
	 * @param toInsert     Element to insert
	 */
	void insert(CompositeElement parent, TreeElement anchorBefore, OuterLanguageElement toInsert);

	/**
	 * If leaf need to be split to insert OuterLanguageElement this function is called
	 *
	 * @param leaf   Leaf to split
	 * @param offset Offset to split leaf into parts
	 * @param table  CharTable
	 * @return first part of the split
	 */
	LeafElement split(LeafElement leaf, int offset, final CharTable table);
}
