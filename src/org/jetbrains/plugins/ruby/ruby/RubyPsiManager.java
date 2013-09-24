package org.jetbrains.plugins.ruby.ruby;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerEx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.TypeInferenceHelper;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 18, 2008
 */
public class RubyPsiManager implements ProjectComponent {
    private final TypeInferenceHelper myTypeInferenceHelper;
    protected Project myProject;

    public RubyPsiManager(@NotNull final Project project) {
        myProject = project;
        myTypeInferenceHelper = new TypeInferenceHelper();
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    @NotNull
    public String getComponentName() {
        return RComponents.RUBY_PSI_MANAGER;

    }

    public void initComponent() {
        ((PsiManagerEx) PsiManager.getInstance(myProject)).registerRunnableToRunOnAnyChange(new Runnable() {
          public void run() {
            myTypeInferenceHelper.clearContext();
          }
        });
    }

    public void disposeComponent() {
    }

    public TypeInferenceHelper getTypeInferenceHelper() {
        return myTypeInferenceHelper;
    }

    public static RubyPsiManager getInstance(@NotNull final Project project) {
        return project.getComponent(RubyPsiManager.class);
    }
}
