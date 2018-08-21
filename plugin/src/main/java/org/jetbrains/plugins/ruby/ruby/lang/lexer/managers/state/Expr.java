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

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Jul 24, 2007
 */
public enum Expr
{
	// Initial state, ignore newlines
	BEG,
	// State after Binary operation in END or ARG state
	MID,
	// State after identifier, fid, or come command. Shows that we`re expecting arguments of call or block
	ARG,
	// State after expression end.
	END,

	// When in method definition, rigth after method_name, used to process braces in method arguments
	CMD_ARG,

	// Right after kDO_COND or {
	CMD_BRACE,

	// After Dot or Colon, ignore newlines
	DOT_OR_COLON,

	// After class resword, ingore heredocs
	CLASS,

	// When in alias, in undef, in definition or after Symbeg
	FNAME
}
