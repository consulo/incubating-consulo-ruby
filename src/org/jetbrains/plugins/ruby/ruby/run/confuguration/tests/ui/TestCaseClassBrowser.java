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

package org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.ui;

import java.util.HashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.cache.RCacheUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.presentation.RClassPresentationUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.RTestUnitUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.RTestsRunConfigurationForm;
import org.jetbrains.plugins.ruby.ruby.scope.SearchScope;
import org.jetbrains.plugins.ruby.ruby.scope.SearchScopeUtil;
import org.jetbrains.plugins.ruby.ruby.ui.TreeRClassChooserDialog;
import com.intellij.execution.configuration.BrowseModuleValueActionListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.reference.SoftReference;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 06.08.2007
 */
public class TestCaseClassBrowser extends BrowseModuleValueActionListener
{
    private final String myTitle;
    public SearchScope mySearchScope;
    public RTestsRunConfigurationForm myForm;
    public final TestCachingFilter myClassCachingFilter;

    public TestCaseClassBrowser(final Project project, final RTestsRunConfigurationForm form) {
        super(project);
        myForm = form;

        myTitle = RBundle.message("choose.test.class.dialog.title");
        mySearchScope = SearchScopeUtil.getTestUnitClassSearchScope(getProject());
        myClassCachingFilter = new TestCachingFilter();
    }

    @Override
	@Nullable
    protected String showDialog() {
        final TreeRClassChooserDialog dialog =
                new TreeRClassChooserDialog(getProject(), myTitle, mySearchScope, null, myClassCachingFilter);
//        final TreeClassChooser dialog = TreeClassChooserFactory.getInstance(getProject()).createWithInnerClassesScopeChooser(myTitle, classFilter.getScope(), classFilter, null);
        configureDialog(dialog);

        // get result
        if (!dialog.showDialog()) {
            //on cancel
            return null;
        }
        // on ok
        final RVirtualClass rClass = dialog.getSelectedClass();
        onClassChoosen(rClass);
        if (rClass == null) {
            return RBundle.message("run.configuration.tests.no.data");
        }
        final String qualifiedName =
                RClassPresentationUtil.getRuntimeQualifiedNameInRubyTestMode(rClass, null);
        // Here assertion is more correct, but I don't believe, that
        // somebody will be able to post all necessary about his project in error submiter.
        // maybe such error message induce him to ask us for help 
        if (TextUtil.isEmpty(qualifiedName)) {
            return RBundle.message("run.configuration.tests.no.qualified.name");
        }
        return qualifiedName;
    }

    protected void onClassChoosen(@Nullable final RVirtualClass psiClass) {
        String testScriptPath;
        if (psiClass == null) {
            testScriptPath = RBundle.message("run.configuration.tests.no.data");
        } else {
            final VirtualFile file = psiClass.getVirtualFile();
            assert file!= null;
            testScriptPath = file.getPath();
        }
        myForm.setTestScriptPath(testScriptPath);
    }

    private void configureDialog(final TreeRClassChooserDialog dialog) {
        final String qualifiedName = getText();

        final String path = myForm.getTestScriptPath();
        final VirtualFile file = TextUtil.isEmpty(path)
                ? null
                : LocalFileSystem.getInstance().findFileByPath(path);

        if (file == null) {
            return;
        }
        
        final RClass rClass = findClass(qualifiedName, file);
        if (rClass != null) {
            dialog.selectClass(rClass);
        }

        if (file.isDirectory()) {
            dialog.selectFile(PsiManager.getInstance(getProject()).findDirectory(file));
        } else {
            dialog.selectFile(PsiManager.getInstance(getProject()).findFile(file));
        }
    }

    @Nullable
    protected RClass findClass(@NotNull final String qualifiedNameClassName,
                               @NotNull final VirtualFile scriptFile) {

        final Project project = getProject();
        final RVirtualClass rVClass =
                RCacheUtil.getClassByNameInScriptInRubyTestMode(qualifiedNameClassName, project,
                                                     mySearchScope, scriptFile, null);
        if (rVClass != null) {
            final RPsiElement psiElem = RVirtualPsiUtil.findPsiByVirtualElement(rVClass, project);
            if (psiElem instanceof RClass) {
                return (RClass)psiElem;
            }
        }
        return null;
    }

//    protected TreeClassChooser.ClassFilterWithScope getFilter() throws NoFilterException {
//        final ConfigurationModuleSelector moduleSelector = getModuleSelector();
//        final Module module = moduleSelector.getModule();
//        if (module == null) {
//            throw NoFilterException.moduleDoesntExist(moduleSelector);
//        }
//        final TreeClassChooser.ClassFilterWithScope classFilter;
//        try {
//            final JUnitConfiguration configurationCopy = new JUnitConfiguration(ExecutionBundle.message("default.junit.configuration.name"), getProject(), JUnitConfigurationType.getInstance().getConfigurationFactories()[0]);
//            applyEditorTo(configurationCopy);
//            classFilter = TestClassFilter.create(configurationCopy.getTestObject().getSourceScope(), configurationCopy.getConfigurationModule().getModule());
//        }
//        catch (JUnitUtil.NoJUnitException e) {
//            throw NoFilterException.noJUnitInModule(module);
//        }
//        return classFilter;
//    }



    // Use this filter only if PsiElements can't be changed at that time
    // e.g. in modal select smth. dialog
    private static class TestCachingFilter implements TreeRClassChooserDialog.ClassFilter {
        private HashMap<Integer, Pair<SoftReference<RVirtualClass>, Boolean>> processedElements =
                new HashMap<Integer, Pair<SoftReference<RVirtualClass>, Boolean>>();

        @Override
		public boolean isAccepted(@NotNull final RVirtualClass rVClass) {
            final VirtualFile virtualFile = rVClass.getVirtualFile();
            if (virtualFile == null) {
                return false;
            }
            
            final int key = rVClass.hashCode();
            final Pair<SoftReference<RVirtualClass>, Boolean> pair = processedElements.get(key);
            boolean isTestCase;
            if (pair == null || pair.first.get() != rVClass) {
                isTestCase = RTestUnitUtil.isClassUnitTestCase(rVClass, null);
                //TODO replace rVClass with its path..
                processedElements.put(key,
                                      new Pair<SoftReference<RVirtualClass>, Boolean>(new SoftReference<RVirtualClass>(rVClass), isTestCase));

            } else {
                isTestCase = pair.second;
            }
            return isTestCase;
        }
    }
}
