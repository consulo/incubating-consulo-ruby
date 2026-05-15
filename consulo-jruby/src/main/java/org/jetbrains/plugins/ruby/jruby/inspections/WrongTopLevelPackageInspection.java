package org.jetbrains.plugins.ruby.jruby.inspections;

import jakarta.annotation.Nonnull;

import consulo.localize.LocalizeValue;
import consulo.language.editor.inspection.LocalInspectionTool;
import consulo.localize.LocalizeValue;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.RBundle;
import consulo.language.psi.PsiElementVisitor;

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
	public LocalizeValue getGroupDisplayName()
	{
		return LocalizeValue.localizeTODO(RBundle.message("inspection.group.name"));
	}

	@Override
	@Nonnull
	@Nls
	public LocalizeValue getDisplayName()
	{
		return LocalizeValue.localizeTODO(RBundle.message("inspection.wrong.top.level.package"));
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
