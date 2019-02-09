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
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubyLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubySimpleLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RBaseString;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.sdk.GemInfo;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.ruby.sdk.gemRootType.GemOrderRootType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Dec 3, 2007
 */
public class GemReference implements RPsiPolyvariantReference
{
	private RCall myOwner;
	private RPsiElement myElement;
	private TextRange myTextRange;

	public GemReference(RCall rCall, RPsiElement gemName)
	{
		myOwner = rCall;
		myElement = gemName;
		myTextRange = computeTextRange(myElement);
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
	@Nonnull
	public PsiElement getRefValue()
	{
		return myElement;
	}

	@Override
	@Nonnull
	public List<Symbol> multiResolveToSymbols(@Nullable final FileSymbol fileSymbol)
	{
		throw new UnsupportedOperationException("method multiResolveToSymbols is not supported in org.jetbrains.plugins.ruby.ruby.codeInsight.references.GemReference");
	}

	@Override
	@Nonnull
	public ResolveResult[] multiResolve(boolean incompleteCode)
	{
		return ResolveResult.EMPTY_ARRAY;
	}

	@Override
	public PsiElement getElement()
	{
		return myOwner;
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
		return null;
	}

	@Override
	public String getCanonicalText()
	{
		return null;
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
	{
		throw new UnsupportedOperationException("method handleElementRename is not supported in org.jetbrains.plugins.ruby.ruby.codeInsight.references.GemReference");
	}

	@Override
	public PsiElement bindToElement(@Nonnull PsiElement element) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element)
	{
		return false;
	}

	@Override
	public Object[] getVariants()
	{
		if(myElement instanceof RBaseString)
		{
			final RFile rFile = RubyPsiUtil.getRFile(myOwner);
			assert rFile != null;
			final Sdk sdk = rFile.getSdk();
			if(sdk != null && RubySdkUtil.isKindOfRubySDK(sdk))
			{
				final String[] rootUrls = sdk.getRootProvider().getUrls(GemOrderRootType.getInstance());
				final ArrayList<RubyLookupItem> variants = new ArrayList<RubyLookupItem>();
				for(String rootUrl : rootUrls)
				{
					final VirtualFile gemsRoot = VirtualFileManager.getInstance().findFileByUrl(rootUrl);
					if(gemsRoot != null)
					{
						for(GemInfo gemInfo : RubySdkUtil.getAllGems(gemsRoot))
						{
							variants.add(new RubySimpleLookupItem(gemInfo.getName(), gemInfo.getVersion(), LookupValueWithPriority.NORMAL, true, RubyIcons.RUBY_ICON));
						}
					}
				}
				return variants.toArray();
			}
		}
		return PsiReference.EMPTY_ARRAY;
	}

	@Override
	public boolean isSoft()
	{
		return true;
	}
}
