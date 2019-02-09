/*
 * Copyright 2000-2007 JetBrains s.r.o.
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

package org.jetbrains.plugins.ruby.ruby.codeInsight.types;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 14, 2008
 */
public class RMethodTypeUtil
{

	public static int getMinNumberOfArguments(@Nonnull final RMethod method)
	{
		int result = 0;
		for(ArgumentInfo info : method.getArgumentInfos())
		{
			if(info.getType() == ArgumentInfo.Type.SIMPLE)
			{
				result++;
			}
			else
			{
				break;
			}
		}
		return result;
	}

	public static int getMaxNumberOfArguments(@Nonnull final RMethod method)
	{
		int result = 0;
		for(ArgumentInfo info : method.getArgumentInfos())
		{
			final ArgumentInfo.Type type = info.getType();
			if(type == ArgumentInfo.Type.SIMPLE || type == ArgumentInfo.Type.PREDEFINED)
			{
				result++;
			}
			else if(type == ArgumentInfo.Type.ARRAY)
			{
				result = -1;
				break;
			}
		}
		return result;
	}

	/**
	 * @param fileSymbol FileSymbol
	 * @param method     Symbol of method @return returns minimum number of arguments for method
	 * @return number
	 */
	public static int getMinNumberOfArguments(@Nullable final FileSymbol fileSymbol, @Nonnull final Symbol method)
	{
		return method.getChildren(fileSymbol).getSymbolsOfTypes(Type.ARG_SIMPLE.asSet()).getAll().size();
	}

	/**
	 * @param fileSymbol FileSymbol
	 * @param method     Symbol of method @return returns maximum number of arguments for method, -1 if infinite
	 * @return number
	 */
	public static int getMaxNumberOfArguments(@Nullable final FileSymbol fileSymbol, @Nonnull final Symbol method)
	{
		int result = 0;
		for(Symbol child : method.getChildren(fileSymbol).getSymbolsOfTypes(Types.ARGS).getAll())
		{
			final Type type = child.getType();
			if(type == Type.ARG_SIMPLE || type == Type.ARG_PREDEFINED)
			{
				result++;
			}
			if(type == Type.ARG_ARRAY)
			{
				return -1;
			}
		}
		return result;
	}
}
