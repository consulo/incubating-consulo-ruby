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

package org.jetbrains.plugins.ruby.ruby.lang.annotator;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.jruby.JRubyUtil;
import org.jetbrains.plugins.ruby.jruby.codeInsight.resolve.JavaReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RFieldAttrReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RFileReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.lang.highlighter.RubyHighlighterKeys;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RFileUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubySystemCallVisitor;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaPackage;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 6, 2007
 */
public class RubySlowAnnotatorVisitor extends RubySystemCallVisitor
{
	private AnnotationHolder myHolder;
	private RFile myFile;


	public RubySlowAnnotatorVisitor(@NotNull final AnnotationHolder holder, @NotNull final RFile rFile)
	{
		myHolder = holder;
		myFile = rFile;
	}

	// required files annotating
	@Override
	public void visitRequireCall(@NotNull final RCall rCall)
	{
		// It`s often operation
		ProgressManager.getInstance().checkCanceled();

		visitRequireOrLoad(rCall);
	}

	@Override
	public void visitLoadCall(@NotNull RCall rCall)
	{
		// It`s often operation
		ProgressManager.getInstance().checkCanceled();

		visitRequireOrLoad(rCall);
	}

	private void visitRequireOrLoad(RCall rCall)
	{
		final List<RPsiElement> args = rCall.getArguments();
		if(args.isEmpty())
		{
			myHolder.createErrorAnnotation(rCall, RBundle.message("annotation.error.no.arguments"));
			return;
		}
		final PsiReference[] requiredReferences = rCall.getReferences();
		for(PsiReference reference : requiredReferences)
		{
			if(reference instanceof RFileReference)
			{
				final RFileReference reqReference = (RFileReference) reference;
				final PsiElement ref = reqReference.getRefValue();
				final String name = RFileUtil.evaluate(rCall.getContainingFile().getVirtualFile(), ref);
				if(!(myFile.isJRubyEnabled() && JRubyUtil.JAVA.equals(name)))
				{
					if(name != null)
					{
						final ResolveResult[] results = reqReference.multiResolve(true);
						if(results.length == 0)
						{
							myHolder.createErrorAnnotation(ref, RBundle.message("annotation.error.cannot.find.required.file"));
						}
						if(results.length > 1)
						{
							final Annotation annotation = myHolder.createWarningAnnotation(ref, RBundle.message("annotation.warning.implicit.multivariant.required.item"));
							annotation.setTextAttributes(RubyHighlighterKeys.INSPECTION_MULTIPLE_RESOLVE_WARNING);
						}
					}
					else
					{
						myHolder.createWarningAnnotation(ref, RBundle.message("annotation.warning.implicit.required.item"));
					}
				}
			}
		}
	}

	@Override
	public void visitIncludeCall(@NotNull RCall rCall)
	{
		// It`s often operation
		ProgressManager.getInstance().checkCanceled();

		visitIncludeOrExtend(rCall);
	}

	@Override
	public void visitExtendCall(@NotNull RCall rCall)
	{
		// It`s often operation
		ProgressManager.getInstance().checkCanceled();

		visitIncludeOrExtend(rCall);
	}

	private void visitIncludeOrExtend(RCall rCall)
	{
		final List<RPsiElement> args = rCall.getArguments();
		if(args.isEmpty())
		{
			myHolder.createErrorAnnotation(rCall, RBundle.message("annotation.error.no.arguments"));
			return;
		}
		for(RPsiElement arg : args)
		{
			final List<Symbol> symbols = ResolveUtil.resolveToSymbols(arg);
			if(symbols.size() != 1)
			{
				myHolder.createWarningAnnotation(arg, RBundle.message("annotation.warning.cannot.resolve.include"));
			}
		}
	}

	@Override
	public void visitAttrAccessorCall(@NotNull RCall rCall)
	{
		// It`s often operation
		ProgressManager.getInstance().checkCanceled();

		visitAttrCall(rCall);
	}

	@Override
	public void visitAttrWriterCall(@NotNull RCall rCall)
	{
		// It`s often operation
		ProgressManager.getInstance().checkCanceled();

		visitAttrCall(rCall);
	}

	@Override
	public void visitAttrReaderCall(@NotNull RCall rCall)
	{
		// It`s often operation
		ProgressManager.getInstance().checkCanceled();

		visitAttrCall(rCall);
	}

	@Override
	public void visitAttrInternalCall(@NotNull RCall rCall)
	{
		// It`s often operation
		ProgressManager.getInstance().checkCanceled();

		visitAttrCall(rCall);
	}

	@Override
	public void visitCAttrAccessorCall(@NotNull final RCall rCall)
	{
		// It`s often operation
		ProgressManager.getInstance().checkCanceled();

		visitAttrCall(rCall);
	}

	private void visitAttrCall(@NotNull RCall rCall)
	{
		final List<RPsiElement> args = rCall.getArguments();
		if(args.isEmpty())
		{
			myHolder.createErrorAnnotation(rCall, RBundle.message("annotation.error.no.arguments"));
			return;
		}
		final PsiReference[] fieldReferences = rCall.getReferences();
		for(PsiReference reference : fieldReferences)
		{
			final RFieldAttrReference fieldAttrReference = (RFieldAttrReference) reference;
			final ResolveResult[] results = fieldAttrReference.multiResolve(true);
			final PsiElement ref = fieldAttrReference.getReferenceContent();
			if(results.length == 0)
			{
				myHolder.createWarningAnnotation(ref, RBundle.message("annotation.warning.cannot.find.variable"));
			}
			if(results.length > 1)
			{
				myHolder.createWarningAnnotation(ref, RBundle.message("annotation.warning.multivariable.found"));
			}
		}
	}

	@Override
	public void visitImportClassCall(@NotNull final RCall rCall)
	{
		// It`s often operation
		ProgressManager.getInstance().checkCanceled();

		final List<RPsiElement> args = rCall.getArguments();
		if(args.isEmpty())
		{
			myHolder.createErrorAnnotation(rCall, RBundle.message("annotation.error.no.arguments"));
			return;
		}
		for(RPsiElement arg : args)
		{
			final List<PsiElement> list = ResolveUtil.multiResolve(arg);
			if(!(list.size() == 1 && list.get(0) instanceof PsiClass))
			{
				myHolder.createErrorAnnotation(arg, RBundle.message("annotation.error.should.be.java.class"));
			}
		}
	}

	@Override
	public void visitIncludeClassCall(@NotNull final RCall rCall)
	{
		// It`s often operation
		ProgressManager.getInstance().checkCanceled();

		visitIncludeJava(rCall, PsiClass.class, RBundle.message("annotation.error.should.be.java.class"));
	}

	@Override
	public void visitIncludePackageCall(@NotNull final RCall rCall)
	{
		// It`s often operation
		ProgressManager.getInstance().checkCanceled();

		visitIncludeJava(rCall, PsiJavaPackage.class, RBundle.message("annotation.error.should.be.java.package"));
	}

	private void visitIncludeJava(final RCall rCall, final Class clazzz, final String errorMsg)
	{
		final List<RPsiElement> args = rCall.getArguments();
		if(args.isEmpty())
		{
			myHolder.createErrorAnnotation(rCall, RBundle.message("annotation.error.no.arguments"));
			return;
		}
		final PsiReference[] references = rCall.getReferences();
		if(references.length == 0)
		{
			myHolder.createErrorAnnotation(rCall, RBundle.message("annotation.error.should.be.java"));
			return;
		}
		if(!clazzz.isInstance(references[references.length - 1].resolve()))
		{
			myHolder.createErrorAnnotation(rCall, errorMsg);
		}
	}

	@Override
	public void visitRSymbol(RSymbol rSymbol)
	{
		// It`s often operation
		ProgressManager.getInstance().checkCanceled();

		final PsiReference[] references = rSymbol.getReferences();
		for(PsiReference reference : references)
		{
			if(reference instanceof JavaReference)
			{
				if(reference.resolve() == null)
				{
					myHolder.createErrorAnnotation(rSymbol, RBundle.message("annotation.error.should.be.java"));
				}
			}
		}
	}
}
