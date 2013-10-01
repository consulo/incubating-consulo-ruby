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

package org.jetbrains.plugins.ruby.ruby.inspections.ducktype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.impl.MessageImpl;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.Access;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.CallAccess;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.ConstantAccess;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.FieldWriteAccess;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.Instruction;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.RControlFlowUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.ReadWriteInstruction;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RArgument;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RArgumentList;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.methodCall.RCallNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;

/**
 * @author oleg
 * @date Jul 1, 2008
 */
public class RubyDuckTypeUtil
{
	public static List<ExpectedMessages> getMethodRequiredTypes(@NotNull final RMethod method)
	{
		// TODO[oleg]: add stubs method arguments notations
		final Map<String, ExpectedMessages> map = getTypeOfParameters(method);

		final ArrayList<ExpectedMessages> list = new ArrayList<ExpectedMessages>();
		final RArgumentList argumentList = method.getArgumentList();
		if(argumentList != null)
		{
			for(RArgument argument : argumentList.getArguments())
			{
				final RIdentifier id = argument.getIdentifier();
				final ExpectedMessages type = map.get(id.getName());
				list.add(type != null ? type : new ExpectedMessages());
			}
		}
		return list;
	}

	/*
	 * creates required types for method
	 */
	private static Map<String, ExpectedMessages> getTypeOfParameters(@NotNull final RMethod method)
	{
		final HashMap<String, ExpectedMessages> map = new HashMap<String, ExpectedMessages>();
		// Fill map with argsInfo
		for(ArgumentInfo info : method.getArgumentInfos())
		{
			if(info.getType() == ArgumentInfo.Type.SIMPLE || info.getType() == ArgumentInfo.Type.PREDEFINED)
			{
				map.put(info.getName(), null);
			}
		}
		final Instruction[] flow = method.getControlFlow();
		final int[] nums = RControlFlowUtil.accessesBeforeWrites(flow);

		for(int num : nums)
		{
			final Access access = ((ReadWriteInstruction) flow[num]).getAccess();
			final String name = ((RIdentifier) access.getElement()).getName();

			//we dont want handle unneccesary usages
			if(!map.containsKey(name))
			{
				continue;
			}
			ExpectedMessages messages = map.get(name);
			if(messages == null)
			{
				messages = new ExpectedMessages();
				map.put(name, messages);
			}

			if(access instanceof CallAccess)
			{
				final String callName = ((CallAccess) access).getCall().getText();
				final RPsiElement ref = ((CallAccess) access).getFullReference();
				final RCall rCall = RCallNavigator.getByCommand(ref);
				if(!messages.containsName(callName))
				{
					if(rCall != null)
					{
						messages.addMessage(new MessageImpl(callName, rCall.getArguments().size(), false, null));
					}
					else
					{
						messages.addMessage(new MessageImpl(callName, 0, false, null));
					}
				}
			}

			if(access instanceof ConstantAccess)
			{
				final String constantName = ((ConstantAccess) access).getConstant().getText();
				if(!messages.containsName(constantName))
				{
					messages.addMessage(new MessageImpl(constantName, 0, false, null));
				}
			}

			if(access instanceof FieldWriteAccess)
			{
				final String fieldName = ((FieldWriteAccess) access).getField().getText() + '=';
				if(!messages.containsName(fieldName))
				{
					messages.addMessage(new MessageImpl(fieldName, 0, false, null));
				}
			}
			map.put(name, messages);
		}
		return map;
	}
}
