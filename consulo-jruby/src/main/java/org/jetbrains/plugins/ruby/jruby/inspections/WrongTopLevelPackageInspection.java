package org.jetbrains.plugins.ruby.jruby.inspections;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.RBundle;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;

/**
 * @author: oleg
 */
public class WrongTopLevelPackageInspection extends LocalInspectionTool
{
	@NonNls
	private static final String SHORT_NAME = "WrongTopLevelPackage";

	@Override
	@Nonnull
	@Nls
	public String getGroupDisplayName()
	{
		return RBundle.message("inspection.group.name");
	}

	@Override
	@Nonnull
	@Nls
	public String getDisplayName()
	{
		return RBundle.message("inspection.wrong.top.level.package");
	}

	@Override
	@Nonnull
	@NonNls
	public String getShortName()
	{
		return SHORT_NAME;
	}

	@Override
	public boolean isEnabledByDefault()
	{
		return true;
	}

	@Override
	@Nonnull
	public PsiElementVisitor buildVisitor(@Nonnull ProblemsHolder holder, boolean isOnTheFly)
	{
		return new WrongTopLevelPackageInspectionVisitor(holder);
	}

	@Override
	@Nonnull
	public HighlightDisplayLevel getDefaultLevel()
	{
		return HighlightDisplayLevel.ERROR;
	}

}
