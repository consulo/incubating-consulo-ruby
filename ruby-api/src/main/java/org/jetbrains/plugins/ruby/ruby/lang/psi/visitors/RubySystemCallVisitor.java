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

package org.jetbrains.plugins.ruby.ruby.lang.psi.visitors;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RubyCallType;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 14.08.2006
 */

/**
 * Ruby system commands visitor
 */
public abstract class RubySystemCallVisitor extends RubyElementVisitor
{
	@Override
	public void visitRCall(@Nonnull final RCall rCall)
	{
		// require command
		if(rCall.getCallType() == RubyCallType.RAISE_CALL)
		{
			visitRaiseCall(rCall);
			return;
		}
		// require command
		if(rCall.getCallType() == RubyCallType.REQUIRE_CALL)
		{
			visitRequireCall(rCall);
			return;
		}
		// load command
		if(rCall.getCallType() == RubyCallType.LOAD_CALL)
		{
			visitLoadCall(rCall);
			return;
		}

		// include
		if(rCall.getCallType() == RubyCallType.INCLUDE_CALL)
		{
			visitIncludeCall(rCall);
			return;
		}
		// extend
		if(rCall.getCallType() == RubyCallType.EXTEND_CALL)
		{
			visitExtendCall(rCall);
			return;
		}

		// attr_reader command
		if(rCall.getCallType() == RubyCallType.ATTR_READER_CALL)
		{
			visitAttrReaderCall(rCall);
			return;
		}
		// attr_writer command
		if(rCall.getCallType() == RubyCallType.ATTR_WRITER_CALL)
		{
			visitAttrWriterCall(rCall);
			return;
		}

		// attr_accessor command
		if(rCall.getCallType() == RubyCallType.ATTR_ACCESSOR_CALL)
		{
			visitAttrAccessorCall(rCall);
			return;
		}

		// attr_internal command
		if(rCall.getCallType() == RubyCallType.ATTR_INTERNAL_CALL)
		{
			visitAttrInternalCall(rCall);
			return;
		}

		// cattr_accessor command
		if(rCall.getCallType() == RubyCallType.CATTR_ACCESSOR_CALL)
		{
			visitCAttrAccessorCall(rCall);
			return;
		}

		// private command
		if(rCall.getCallType() == RubyCallType.PRIVATE_CALL)
		{
			visitPrivateCall(rCall);
			return;
		}
		// protected command
		if(rCall.getCallType() == RubyCallType.PROTECTED_CALL)
		{
			visitProtectedCall(rCall);
			return;
		}

		// public command
		if(rCall.getCallType() == RubyCallType.PUBLIC_CALL)
		{
			visitPublicCall(rCall);
			return;
		}

		// JRuby commands
		// import command
		if(rCall.getCallType() == RubyCallType.IMPORT_CALL)
		{
			visitImportClassCall(rCall);
			return;
		}
		// include_class command
		if(rCall.getCallType() == RubyCallType.INCLUDE_CLASS_CALL)
		{
			visitIncludeClassCall(rCall);
			return;
		}
		// include_package command
		if(rCall.getCallType() == RubyCallType.INCLUDE_PACKAGE_CALL)
		{
			visitIncludePackageCall(rCall);
			return;
		}
		visitElement(rCall);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////// Methods to ovveride //////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void visitRaiseCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitRequireCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitLoadCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitIncludeCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitExtendCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitAttrAccessorCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitAttrInternalCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitCAttrAccessorCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitAttrWriterCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitAttrReaderCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitPublicCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitProtectedCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitPrivateCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitImportClassCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitIncludeClassCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitIncludePackageCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	public void visitGemCall(@Nonnull final RCall rCall)
	{
		visitElement(rCall);
	}

	@Override
	public void visitElement(@Nonnull final PsiElement element)
	{
		element.acceptChildren(this);
	}
}
