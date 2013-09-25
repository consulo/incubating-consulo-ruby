package org.jetbrains.plugins.ruby.jruby.inspections;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;

/**
 * @author: oleg
 */
public class WrongTopLevelPackageInspection extends LocalInspectionTool {
    @NonNls
    private static final String SHORT_NAME = "WrongTopLevelPackage";

    @Override
	@NotNull
    @Nls
    public String getGroupDisplayName() {
        return RBundle.message("inspection.group.name");
    }

    @Override
	@NotNull
    @Nls
    public String getDisplayName() {
        return RBundle.message("inspection.wrong.top.level.package");
    }

    @Override
	@NotNull
    @NonNls
    public String getShortName() {
        return SHORT_NAME;
    }

    @Override
	public boolean isEnabledByDefault() {
        return true;
    }

    @Override
	@NotNull
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new WrongTopLevelPackageInspectionVisitor(holder);
    }

    @Override
	@NotNull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.ERROR;
    }

}
