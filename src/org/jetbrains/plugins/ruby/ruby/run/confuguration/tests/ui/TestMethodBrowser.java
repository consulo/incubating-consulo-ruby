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

import com.intellij.execution.junit2.configuration.BrowseModuleValueActionListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.cache.RCacheUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualClassUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.RTestUnitUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.RTestsRunConfigurationForm;
import org.jetbrains.plugins.ruby.ruby.scope.SearchScope;
import org.jetbrains.plugins.ruby.ruby.scope.SearchScopeUtil;

/**
 * Created by IntelliJ IDEA.
*
* @author: Roman Chernyatchik
* @date: 06.08.2007
*/
public class TestMethodBrowser extends BrowseModuleValueActionListener {
    private final RTestsRunConfigurationForm myForm;
    public final SearchScope myScope;

    public TestMethodBrowser(final Project project, final RTestsRunConfigurationForm form) {
        super(project);

        myForm = form;
        myScope = SearchScopeUtil.getTestUnitClassSearchScope(project);
    }

    protected String showDialog() {
        //check script
        final VirtualFile file = LocalFileSystem.getInstance().findFileByPath(myForm.getTestScriptPath());
        if (file == null) {
            Messages.showMessageDialog(getField(), "set.existing.script.name.message", "cannot.browse.method.dialog.title", Messages.getInformationIcon());
            return null;
        }
        //check class name
        final String classQualifiedName = myForm.getTestQualifiedClassName();        
        if (classQualifiedName.trim().length() == 0) {
            Messages.showMessageDialog(getField(),
                    RBundle.message("set.class.name.message"),
                    RBundle.message("cannot.browse.method.dialog.title"),
                    Messages.getInformationIcon());
            return null;
        }

        final Ref<FileSymbol> fSWrapper = new Ref<FileSymbol>();

        final RVirtualClass testClass =
                RCacheUtil.getClassByNameInScriptInRubyTestMode(classQualifiedName, getProject(),
                                                                myScope, file, fSWrapper);
        if (testClass == null) {
            Messages.showMessageDialog(getField(),
                    RBundle.message("class.does.not.exists.error.message", classQualifiedName),
                    RBundle.message("cannot.browse.method.dialog.title"),
                    Messages.getInformationIcon());
            return null;
        }

        final TestMethodFilter methodFilter = new TestMethodFilter(testClass);
        final RMethodList.RMethodProvider methodProvider = new TestMethodProvider(testClass, fSWrapper);

        final RVirtualMethod psiMethod =
                RMethodList.showDialog(testClass, methodFilter, methodProvider, getField());

        return psiMethod != null ? psiMethod.getName() : null;
    }

    public static class TestMethodFilter implements Condition<RVirtualMethod> {
        public final RVirtualClass myRVClass;

        public TestMethodFilter(@NotNull final RVirtualClass rClass) {
            myRVClass = rClass;
        }

        /**
         * @param method must belong to class defined in constructor
         * @return true if is test method
         */
        public boolean value(final RVirtualMethod method) {
            return RTestUnitUtil.hasValidTestNameAndNotSingleton(method);
        }
    }

    private static class TestMethodProvider implements RMethodList.RMethodProvider {
        private static final RVirtualMethod[] EMPTY_VIRT_METHODS = new RVirtualMethod[0];

        private final RVirtualClass testClass;
        private final Ref<FileSymbol> fSWrapper;

        public TestMethodProvider(final RVirtualClass testClass, final Ref<FileSymbol> fSWrapper) {
            this.testClass = testClass;
            this.fSWrapper = fSWrapper;
        }

        public RVirtualMethod[] getAllMethods() {
            final Pair<Symbol, FileSymbol> fileSymbolPair = SymbolUtil.getSymbolByContainerRubyTestMode(testClass, fSWrapper);
            if (fileSymbolPair == null) {
                return EMPTY_VIRT_METHODS;
            }
            return RVirtualClassUtil.getAllMethods(fileSymbolPair.first, fileSymbolPair.second, Context.ALL);
        }
    }
}
