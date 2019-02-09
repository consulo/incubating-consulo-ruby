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

package org.jetbrains.plugins.ruby.ruby.cache.psi.variables;

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualFieldHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.FieldType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 22, 2006
 */
public interface RVirtualField extends RVirtualElement
{
	@Nonnull
	public String getName();

	@Nonnull
	public RVirtualFieldHolder getHolder();

	/**
	 * @return Field type, FieldType.CLASS_VARIABLE or FieldType.INSTANCE_VARIABLE
	 */
	public FieldType getType();

	@Nonnull
	public String getText();
}
