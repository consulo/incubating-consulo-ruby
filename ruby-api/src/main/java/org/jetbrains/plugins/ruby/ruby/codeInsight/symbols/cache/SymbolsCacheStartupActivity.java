package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache;

import consulo.annotation.component.ExtensionImpl;
import consulo.project.Project;
import consulo.project.startup.BackgroundStartupActivity;
import consulo.ui.UIAccess;

/**
 * @author VISTALL
 * @since 2026-05-16
 */
@ExtensionImpl
public class SymbolsCacheStartupActivity implements BackgroundStartupActivity {
    @Override
    public void runActivity(Project project, UIAccess uiAccess) {
        SymbolsCache.getInstance(project).recreateAllBuiltInCaches();
    }
}
