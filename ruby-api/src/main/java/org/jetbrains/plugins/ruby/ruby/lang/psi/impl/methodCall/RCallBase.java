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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.methodCall;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import consulo.awt.TargetAWT;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.codeInsight.resolve.JavaReferencesBuilder;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.*;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.*;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.variables.RVirtualFieldAttrImpl;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.FieldAttrType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualFieldAttr;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.GemReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RFieldAttrReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RFileReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.TypeInferenceHelper;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RBaseString;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RBinaryExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RFieldHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RFileUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RubyCallType;
import org.jetbrains.plugins.ruby.ruby.presentation.JavaClassPackagePresentationUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.RFieldAttrPresentationUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 14.08.2006
 */
public abstract class RCallBase extends RPsiElementBase implements RPsiElement, RCall
{
	public RCallBase(ASTNode astNode)
	{
		super(astNode);
	}


	@Override
	@NotNull
	public RubyCallType getCallType()
	{
		return RCallBaseUtil.getCallType(this);
	}

	@Override
	@NotNull
	public PsiElement getPsiCommand()
	{
		final PsiElement command = getFirstChild();
		assert command != null : "Cannot find command";
		return command;
	}

	@Override
	@NotNull
	public PsiReference[] getReferences()
	{
		final RubyCallType callType = getCallType();

		// Adding required or loaded elements references
		final List<RPsiElement> arguments = getCallArguments().getElements();
		if(callType.isFileRef())
		{
			List<PsiReference> refs = new ArrayList<PsiReference>();
			for(RPsiElement argument : arguments)
			{
				// add variants for File.dirname(__FILE__) + 'some string here'
				if(argument instanceof RBinaryExpression)
				{
					final RBinaryExpression expr = (RBinaryExpression) argument;
					final RPsiElement right = expr.getRightOperand();
					if(expr.getOperationType() == RubyTokenTypes.tPLUS &&
							RFileUtil.FILE_DIRNAME.equals(expr.getLeftOperand().getText()) &&
							right instanceof RBaseString)
					{
						refs.add(new RFileReference(this, argument, right, true));
					}
				}
				else
				{
					refs.add(new RFileReference(this, argument, argument, false));
				}
			}
			return refs.toArray(new PsiReference[refs.size()]);
		}

		// Adding references from attributes to fields
		if(callType.isAttributeCall())
		{
			final RFieldHolder holder = PsiTreeUtil.getParentOfType(this, RFieldHolder.class);
			assert holder != null;
			List<PsiReference> refs = new ArrayList<PsiReference>();
			for(RPsiElement argument : arguments)
			{
				refs.add(new RFieldAttrReference(this, argument, holder));
			}
			return refs.toArray(new PsiReference[refs.size()]);
		}

		// Include_class and include_package references
		if(callType == RubyCallType.INCLUDE_CLASS_CALL || callType == RubyCallType.INCLUDE_PACKAGE_CALL)
		{
			final List<RPsiElement> args = getArguments();
			if(args.size() == 1)
			{
				final RPsiElement arg = args.get(0);
				if(arg instanceof RBaseString)
				{
					final List<PsiReference> refs = JavaReferencesBuilder.createReferences(this, (RBaseString) arg);
					return refs.toArray(new PsiReference[refs.size()]);
				}
			}
		}

		// Gems references
		if(callType.isGemCall())
		{
			if(arguments.size() >= 1)
			{
				final RPsiElement gemName = arguments.get(0);
				if(gemName instanceof RBaseString)
				{
					return new PsiReference[]{new GemReference(this, gemName)};
				}
			}
		}
		return PsiReference.EMPTY_ARRAY;
	}

	@Override
	@NotNull
	public RListOfExpressions getCallArguments()
	{
		//noinspection ConstantConditions
		return getChildByType(RListOfExpressions.class, 0);
	}

	@Override
	@NotNull
	public String getCommand()
	{
		return getPsiCommand().getText();
	}

	@Override
	@NotNull
	public List<RPsiElement> getArguments()
	{
		return getCallArguments().getElements();
	}


	@Override
	@NotNull
	public RVirtualStructuralElement createVirtualCopy(@Nullable RVirtualContainer container, @Nullable RFileInfo info)
	{
		final StructureType type = getType();
		// require
		if(type == StructureType.CALL_REQUIRE)
		{
			final VirtualFile file = info != null ? info.getVirtualFile() : null;
			return new RVirtualRequireImpl(container, RCallBaseUtil.gatherUrls(this, file));
		}
		// load
		if(type == StructureType.CALL_LOAD)
		{
			final VirtualFile file = info != null ? info.getVirtualFile() : null;
			return new RVirtualLoadImpl(container, RCallBaseUtil.gatherUrls(this, file));
		}

		// include
		if(type == StructureType.CALL_INCLUDE)
		{
			return new RVirtualIncludeImpl(container, RCallBaseUtil.gatherVirtualNames(this));
		}
		// extend
		if(type == StructureType.CALL_EXTEND)
		{
			return new RVirtualExtendImpl(container, RCallBaseUtil.gatherVirtualNames(this));
		}

		// attr_reader, attr_writer, attr_accessor
		if(type == StructureType.FIELD_ATTR_CALL)
		{
			return new RVirtualFieldAttrImpl(container, getFieldAttrType(), RCallBaseUtil.gatherStrings(this));
		}

		// JRuby specific: import, include Java class or package
		if(type == StructureType.CALL_IMPORT)
		{
			return new RVirtualImportJavaClassImpl(container, RCallBaseUtil.gatherVirtualNames(this));
		}
		if(type == StructureType.CALL_INCLUDE_CLASS)
		{
			final String javaQualifiedName = RCallBaseUtil.getJavaQualifiedName(this);
			if(javaQualifiedName != null)
			{
				return new RVirtualIncludeJavaClassImpl(container, javaQualifiedName);
			}
		}
		if(type == StructureType.CALL_INCLUDE_PACKAGE)
		{
			final String javaQualifiedName = RCallBaseUtil.getJavaQualifiedName(this);
			if(javaQualifiedName != null)
			{
				return new RVirtualIncludeJavaPackageImpl(container, javaQualifiedName);
			}
		}

		throw new IllegalStateException("Cannot create virtual copy for call type: " + getType());
	}


	@Override
	public StructureType getType()
	{
		return RCallBaseUtil.getType(this);
	}

	public FieldAttrType getFieldAttrType()
	{
		return RCallBaseUtil.getFieldAttrType(this);
	}

	@Override
	public boolean equalsToVirtual(@NotNull RVirtualStructuralElement element)
	{
		final StructureType myType = getType();
		if(myType == StructureType.FAKE)
		{
			return false;
		}
		if(myType != element.getType())
		{
			return false;
		}
		if(myType == StructureType.CALL_REQUIRE)
		{
			if(!(element instanceof RVirtualRequire))
			{
				return false;
			}
			final RVirtualRequire require = (RVirtualRequire) element;
			return require.getNames().equals(RCallBaseUtil.gatherUrls(this, getContainingFile().getVirtualFile()));
		}
		if(myType == StructureType.CALL_LOAD)
		{
			if(!(element instanceof RVirtualLoad))
			{
				return false;
			}
			final RVirtualLoad load = (RVirtualLoad) element;
			return load.getNames().equals(RCallBaseUtil.gatherUrls(this, getContainingFile().getVirtualFile()));
		}
		if(myType == StructureType.CALL_INCLUDE)
		{
			if(!(element instanceof RVirtualInclude))
			{
				return false;
			}
			final RVirtualInclude include = (RVirtualInclude) element;
			return include.getNames().equals(RCallBaseUtil.gatherVirtualNames(this));
		}
		if(myType == StructureType.CALL_EXTEND)
		{
			if(!(element instanceof RVirtualExtend))
			{
				return false;
			}
			final RVirtualExtend extend = (RVirtualExtend) element;
			return extend.getNames().equals(RCallBaseUtil.gatherVirtualNames(this));
		}
		if(myType == StructureType.FIELD_ATTR_CALL)
		{
			if(!(element instanceof RVirtualFieldAttr))
			{
				return false;
			}
			final RVirtualFieldAttr fieldAttr = (RVirtualFieldAttr) element;
			return fieldAttr.getFieldAttrType() == getFieldAttrType() && fieldAttr.getNames().equals(RCallBaseUtil.gatherStrings(this));
		}
		if(myType == StructureType.CALL_IMPORT)
		{
			if(!(element instanceof RVirtualImportJavaClass))
			{
				return false;
			}
			final RVirtualImportJavaClass importtt = (RVirtualImportJavaClass) element;
			return importtt.getNames().equals(RCallBaseUtil.gatherVirtualNames(this));
		}
		if(myType == StructureType.CALL_INCLUDE_CLASS)
		{
			if(!(element instanceof RVirtualIncludeJavaClass))
			{
				return false;
			}
			final RVirtualIncludeJavaClass include = (RVirtualIncludeJavaClass) element;
			return Comparing.equal(RCallBaseUtil.getJavaQualifiedName(this), include.getQualifiedName());
		}
		if(myType == StructureType.CALL_INCLUDE_PACKAGE)
		{
			if(!(element instanceof RVirtualIncludeJavaPackage))
			{
				return false;
			}
			final RVirtualIncludeJavaPackage include = (RVirtualIncludeJavaPackage) element;
			return Comparing.equal(RCallBaseUtil.getJavaQualifiedName(this), include.getQualifiedName());
		}
		return false;
	}

	@Nullable
	public Icon getIcon(final int flags)
	{
		final FieldAttrType type = getFieldAttrType();
		if(type != null)
		{
			return TargetAWT.to(RFieldAttrPresentationUtil.getAttrIcon(type));
		}

		if(getCallType() == RubyCallType.IMPORT_CALL ||
				getCallType() == RubyCallType.INCLUDE_CLASS_CALL ||
				getCallType() == RubyCallType.INCLUDE_PACKAGE_CALL)
		{
			return TargetAWT.to(JavaClassPackagePresentationUtil.getIncludeIcon());
		}
		return null;
	}

	@Override
	public ItemPresentation getPresentation()
	{
		final StructureType type = getType();
		if(type == StructureType.FIELD_ATTR_CALL)
		{
			return RFieldAttrPresentationUtil.getPresentation(this);
		}
		if(getCallType() == RubyCallType.IMPORT_CALL ||
				type == StructureType.CALL_INCLUDE_CLASS ||
				type == StructureType.CALL_INCLUDE_PACKAGE)
		{
			return JavaClassPackagePresentationUtil.getIncludeJavaPresentation(this);
		}
		return null;
	}

	@Override
	@NotNull
	public RType getType(@Nullable final FileSymbol fileSymbol)
	{
		final TypeInferenceHelper helper = TypeInferenceHelper.getInstance(getProject());
		helper.testAndSet(fileSymbol);
		return helper.inferCallType(this);
	}
}
