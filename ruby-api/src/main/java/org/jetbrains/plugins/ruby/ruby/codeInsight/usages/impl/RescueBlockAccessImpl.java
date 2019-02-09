package org.jetbrains.plugins.ruby.ruby.codeInsight.usages.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.RescueBlockAccess;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RRescueBlock;
import com.intellij.psi.PsiReference;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 07.04.2008
 */
public class RescueBlockAccessImpl implements RescueBlockAccess
{
	private final RRescueBlock myRescueBlock;
	private final RPsiElement myUsage;

	public RescueBlockAccessImpl(final RRescueBlock rescueBlock, final RPsiElement usage)
	{
		myRescueBlock = rescueBlock;
		myUsage = usage;
	}

	@Override
	@Nullable
	public RPsiElement getTypeElement()
	{
		return myRescueBlock.getException();
	}

	@Override
	@Nonnull
	public RPsiElement getElement()
	{
		return myUsage;
	}

	public RType getType(@Nullable final FileSymbol fileSymbol)
	{
		final RPsiElement typeElement = getTypeElement();
		if(typeElement != null)
		{
			PsiReference psiReference = typeElement.getReference();
			Symbol symbol = ResolveUtil.resolveToSymbol(fileSymbol, psiReference);
			return symbol != null ? RTypeUtil.createTypeBySymbol(fileSymbol, symbol, Context.INSTANCE, true) : null;
		}
		return null;
	}
}
