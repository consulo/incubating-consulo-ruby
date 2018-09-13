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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 23, 2006
 */
public class AccessModifiersUtil
{
	/**
	 * Tries to interpretate a <code>text</code> as <code>ACCESS_MODIFIER</code>
	 *
	 * @param text text
	 * @return Appropriate <code>ACCESS_MODIFIER</code> or <code>ACCESS_MODIFIER.UNKNOWN</code>
	 */
	@NotNull
	public static AccessModifier getModifierByName(@NotNull final String text)
	{
		if(text.equals(RCall.PUBLIC_COMMAND))
		{
			return AccessModifier.PUBLIC;
		}
		if(text.equals(RCall.PRIVATE_COMMAND))
		{
			return AccessModifier.PRIVATE;
		}
		if(text.equals(RCall.PROTECTED_COMMAND))
		{
			return AccessModifier.PROTECTED;
		}
		return AccessModifier.UNKNOWN;
	}

	/**
	 * Checks if <code>ACCESS_MODIFIER</code> exists with similar name.
	 * (Uses in RubyAspectImpl)
	 * <p>For example:<br>
	 * p -> true (public / protected)<br>
	 * pro -> true (protected)<br>
	 * pu -> true (public)<br>
	 * pa -> false<br>
	 * private -> true (private)<br>
	 * private_anytext -> true (private)<br>
	 *
	 * @param str text
	 * @return true if str is similar to some valid modifier
	 */
	public static boolean existModifierByString(final String str)
	{
		return !TextUtil.isEmpty(str) && (RCall.PUBLIC_COMMAND.startsWith(str) || str.startsWith(RCall.PUBLIC_COMMAND) || RCall.PRIVATE_COMMAND.startsWith(str) || str.startsWith(RCall.PRIVATE_COMMAND) || RCall.PROTECTED_COMMAND.startsWith(str) || str.startsWith(RCall.PROTECTED_COMMAND));
	}
}
