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

package org.jetbrains.jvaymlb.tokens;

import org.jruby.util.ByteList;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 21, 2008
 */
public class FlowScalarToken extends ScalarToken
{
	public FlowScalarToken(final ByteList value, final boolean plain, final int start, final int end)
	{
		super(value, plain, start, end);
	}

	public FlowScalarToken(final ByteList value, final boolean plain, final char style, final int start, final int end)
	{
		super(value, plain, style, start, end);
	}
}
