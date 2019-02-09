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

package org.jetbrains.plugins.ruby.ruby.lang.lexer.managers.state;

import java.util.HashMap;

import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 23, 2006
 */
public class State
{
	// state by yyState()
	private int myYYState;

	private HashMap<Class, StateComponent> myComponentsMap;

	public State(final int state)
	{
		myYYState = state;
		myComponentsMap = new HashMap<Class, StateComponent>();
	}

	public int getYYState()
	{
		return myYYState;
	}

	public void setYYState(final int state)
	{
		myYYState = state;
	}

	public void addComponent(final StateComponent stateComponent)
	{
		myComponentsMap.put(stateComponent.getClass(), stateComponent);
	}

	@SuppressWarnings({"unchecked"})
	@Nullable
	/**
	 * @return component by class
	 */
	public <T extends StateComponent> T getComponent(final Class<T> componentClass)
	{
		return (T) myComponentsMap.get(componentClass);
	}
}

