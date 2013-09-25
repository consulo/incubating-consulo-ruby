package org.jetbrains.plugins.ruby.ruby.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RArgumentList;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RClassName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RMethodName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RModuleName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RSuperClass;
import com.intellij.codeInsight.hint.DeclarationRangeHandler;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 24.09.13.
 */
public class RubyDeclarationRangeHandler implements DeclarationRangeHandler
{
	@Override
	@NotNull
	public TextRange getDeclarationRange(@NotNull final PsiElement container)
	{
		final TextRange containerRange = container.getTextRange();
		int start = containerRange.getStartOffset();
		int end = start;
		TextRange range = null;

		if(container instanceof RClass)
		{

			final RSuperClass superClass = ((RClass) container).getPsiSuperClass();
			if(superClass != null)
			{
				end = superClass.getTextRange().getEndOffset();
			}
			else
			{
				final RClassName className = ((RClass) container).getClassName();
				if(className != null)
				{
					end = className.getTextRange().getEndOffset();
				}
			}
			return new TextRange(start, end);
		}
		else if(container instanceof RMethod)
		{
			final RArgumentList argList = ((RMethod) container).getArgumentList();
			if(argList != null)
			{
				end = argList.getTextRange().getEndOffset();
			}
			else
			{
				final RMethodName methodName = ((RMethod) container).getMethodName();
				if(methodName != null)
				{
					end = methodName.getTextRange().getEndOffset();
				}
			}
			return new TextRange(start, end);
		}
		else if(container instanceof RModule)
		{
			final RModuleName moduleName = ((RModule) container).getModuleName();
			if(moduleName != null)
			{
				range = moduleName.getTextRange();
			}
		}

		if(range == null)
		{
			range = containerRange;
		}

		return new TextRange(range.getStartOffset(), range.getEndOffset());
	}
}
