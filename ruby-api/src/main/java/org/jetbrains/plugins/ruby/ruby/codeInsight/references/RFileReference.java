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

package org.jetbrains.plugins.ruby.ruby.codeInsight.references;

import com.intellij.codeInsight.lookup.LookupValueWithPriority;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubyLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubySimpleLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.LastSymbolStorage;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RBaseString;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RFileUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 6, 2007
 */
public class RFileReference implements RPsiPolyvariantReference
{
	private final RPsiElement myOwner;
	private TextRange myTextRange;

	private static final int SO_SUFFIX_LENGTH = RFileUtil.SO_FILE_SUFFIX.length();
	private static final int RB_SUFFIX_LENGTH = RFileUtil.RB_FILE_SUFFIX.length();
	private boolean isRelativeToDirectory;
	private RPsiElement myFullReference;
	private RPsiElement myElement;


	public RFileReference(@Nonnull final RPsiElement owner, @Nonnull final RPsiElement fullReference, @Nonnull final RPsiElement element, final boolean relativeToDirectory)
	{
		myOwner = owner;
		myFullReference = fullReference;
		myElement = element;
		myTextRange = computeTextRange(element);
		isRelativeToDirectory = relativeToDirectory;
	}

	private TextRange computeTextRange(@Nonnull final RPsiElement element)
	{
		final int relativeStartOffset = element.getTextOffset() - myOwner.getTextOffset();
		int start = relativeStartOffset;
		if(element instanceof RBaseString)
		{
			final PsiElement firstChild = element.getFirstChild();
			if(firstChild != null)
			{
				start += firstChild.getTextLength();
			}

			int end = relativeStartOffset + element.getTextLength();
			final PsiElement lastChild = element.getLastChild();
			//noinspection ConstantConditions
			if(lastChild != null && lastChild.getNode().getElementType() == RubyTokenTypes.tSTRING_END)
			{
				end -= lastChild.getTextLength();
			}
			return new TextRange(start, end);
		}
		return new TextRange(relativeStartOffset, relativeStartOffset + element.getTextLength());
	}

	@Override
	public PsiElement getElement()
	{
		return myOwner;
	}

	public PsiElement getFullReferenceElement()
	{
		return myFullReference;
	}

	@Override
	public TextRange getRangeInElement()
	{
		return myTextRange;
	}

	@Override
	@Nullable
	public PsiElement resolve()
	{
		return ResolveUtil.resolvePolyVarReference(this);
	}


	@Override
	public String getCanonicalText()
	{
		return null;
	}

	@Override
	public PsiElement bindToElement(@Nonnull PsiElement element) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public boolean isReferenceTo(final PsiElement element)
	{
		return resolve() == element;
	}

	@Override
	public Object[] getVariants()
	{
		final FileSymbol fileSymbol = LastSymbolStorage.getInstance(myElement.getProject()).getSymbol();
		return getVariants(fileSymbol);
	}

	public Object[] getVariants(@Nullable final FileSymbol fileSymbol)
	{
		if(fileSymbol == null)
		{
			return PsiReference.EMPTY_ARRAY;
		}

		if(myElement instanceof RBaseString)
		{
			final RFile rFile = RubyPsiUtil.getRFile(myOwner);
			assert rFile != null;
			final ArrayList<RubyLookupItem> variants = new ArrayList<RubyLookupItem>();
			final HashSet<String> foundNames = new HashSet<String>();
			final VirtualFile file = rFile.getVirtualFile();
			if(file != null)
			{
				for(String requireUrl : RFileUtil.getAvailableRequiresUrls(fileSymbol, file, isRelativeToDirectory))
				{
					if(requireUrl.endsWith(RFileUtil.RB_FILE_SUFFIX))
					{
						requireUrl = requireUrl.substring(0, requireUrl.length() - RB_SUFFIX_LENGTH);
					}
					else if(requireUrl.endsWith(RFileUtil.SO_FILE_SUFFIX))
					{
						requireUrl = requireUrl.substring(0, requireUrl.length() - SO_SUFFIX_LENGTH);
					}
					if(!foundNames.contains(requireUrl))
					{
						foundNames.add(requireUrl);
						variants.add(new RubySimpleLookupItem(requireUrl, null, LookupValueWithPriority.NORMAL, false, RubyFileType.INSTANCE.getIcon()));
					}
				}
			}
			return variants.toArray();
		}
		return PsiReference.EMPTY_ARRAY;
	}

	@Override
	public boolean isSoft()
	{
		return true;
	}

	@Override
	@Nonnull
	public PsiElement getRefValue()
	{
		return myFullReference;
	}

	public boolean allowsInternalReferences()
	{
		return true;
	}

	@Override
	public PsiElement handleElementRename(@Nonnull final String newElementName) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	@Nonnull
	public List<Symbol> multiResolveToSymbols(@Nullable final FileSymbol fileSymbol)
	{
		throw new UnsupportedOperationException("multiResolveToSymbols is not implemented in org.jetbrains.plugins.ruby.ruby.codeInsight.references.RFileReference");
	}

	@Override
	@Nonnull
	public ResolveResult[] multiResolve(boolean incompleteCode)
	{
		final FileSymbol fileSymbol = LastSymbolStorage.getInstance(myElement.getProject()).getSymbol();
		return multiResolve(fileSymbol);
	}

	public ResolveResult[] multiResolve(@Nullable final FileSymbol fileSymbol)
	{
		if(fileSymbol == null)
		{
			return ResolveResult.EMPTY_ARRAY;
		}

		final RFile rFile = RubyPsiUtil.getRFile(myOwner);

		final List<String> myUrls = new ArrayList<String>();
		if(rFile != null)
		{
			myUrls.addAll(RFileUtil.getUrlsByRPsiElement(fileSymbol, rFile, isRelativeToDirectory, myElement));
		}

		final HashSet<PsiFile> foundFiles = new HashSet<PsiFile>();
		final ArrayList<ResolveResult> list = new ArrayList<ResolveResult>();
		for(String url : myUrls)
		{
			final PsiFile file = RVirtualPsiUtil.getPsiFile(url, myOwner.getProject());
			if(file != null && !foundFiles.contains(file))
			{
				foundFiles.add(file);
				list.add(new ResolveResult()
				{
					@Override
					@Nullable
					public PsiElement getElement()
					{
						return file;
					}

					@Override
					public boolean isValidResult()
					{
						return true;
					}
				});
			}
		}
		return list.toArray(new ResolveResult[list.size()]);
	}
}
