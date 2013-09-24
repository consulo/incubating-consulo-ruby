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

package org.jetbrains.plugins.ruby.ruby.run.confuguration;

import com.intellij.execution.LocatableConfigurationType;
import com.intellij.execution.Location;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.presentation.RClassPresentationUtil;
import static org.jetbrains.plugins.ruby.ruby.run.confuguration.AbstractRubyRunConfiguration.TestType;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunConfigurationFactory;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.RTestUnitUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.RTestsRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.RTestsRunConfigurationFactory;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 20.07.2006
 */
public class RubyRunConfigurationType implements LocatableConfigurationType {

    private final RubyRunConfigurationFactory myAppFactory;
    private final RTestsRunConfigurationFactory myUnitTestsFactory;

    public static RubyRunConfigurationType getInstance() {
        return ApplicationManager.getApplication().getComponent(RubyRunConfigurationType.class);
    }

    public RubyRunConfigurationType() {
        myAppFactory = new RubyRunConfigurationFactory(this);
        myUnitTestsFactory = new RTestsRunConfigurationFactory(this);
    }

    public String getDisplayName() {
        return RBundle.message("run.configuration.type.name");
    }

    public String getConfigurationTypeDescription() {
        return RBundle.message("run.configuration.type.description");
    }

    public Icon getIcon() {
        return RubyIcons.RUBY_RUN_CONFIGURATION_FOLDER;
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{myAppFactory, myUnitTestsFactory};
    }

    @NotNull
    public String getComponentName() {
        return RComponents.RUBY_RUN_CONFIGURATION_TYPE;
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    public boolean isConfigurationByElement(@NotNull final RunConfiguration configuration,
                                            @NotNull final Project project,
                                            @NotNull final PsiElement element) {
        return RubyRunConfigurationUtil.isConfigurationByElement(configuration, element);

    }


    @Nullable
    public RunnerAndConfigurationSettings createConfigurationByLocation(final @NotNull Location location) {
// if not in psi file
        final PsiElement locationElement = location.getPsiElement();
        final RFile containingFile = RubyPsiUtil.getRFile(locationElement);
        final OpenFileDescriptor openedFile = location.getOpenFileDescriptor();

        if (containingFile == null || openedFile == null) {
            // if in psi directory
            if (locationElement instanceof PsiDirectory) {
                return createRunAllUnitTestsInFolderConf((PsiDirectory) locationElement);
            }
            return null;
        }

        final Project project = location.getProject();
        final VirtualFile file = openedFile.getFile();
        if (!RubyVirtualFileScanner.isRubyFile(file)) {
            return null;
        }
        // Should we create unit test configuration
        FileSemantic fileSemantic = FileSemantic.RUBY_SCRIPT;
        // Unit test configuration type
        TestType testType = null;
        // Configuration name
        String name = file.getNameWithoutExtension();
        String className = null;
        String methodName = null;

// analyze selected element
        final RPsiElement rPsiElement = RubyPsiUtil.getCoveringRPsiElement(locationElement);
        if (rPsiElement != null) {
            final PsiFile psiFile = rPsiElement.getContainingFile();
            if (psiFile != null && RSpecUtil.isRSpecTestFile(psiFile.getVirtualFile())) {
                //if file is RSpec test file we should suggest to run it with rspec,
                //even if default test frame work is test_unit
                return null;
            }


            final RContainer currentContainer = (rPsiElement instanceof RContainer)
                    ? (RContainer) rPsiElement
                    : rPsiElement.getParentContainer();

            //In Ruby Class
            if (currentContainer instanceof RClass) {
                // "currentContainer" maybe nested class so we should
                // find the upper class contained him
                final RClass upperClass = RubyPsiUtil.getContainingUpperRClass(currentContainer);
                final Ref<FileSymbol> fSWrapper = new Ref<FileSymbol>();
                fileSemantic = (upperClass != null && RTestUnitUtil.isClassUnitTestCase(upperClass, fSWrapper))
                        ? FileSemantic.TEST_UNIT_TEST
                        : FileSemantic.RUBY_SCRIPT;

                if (fileSemantic == FileSemantic.TEST_UNIT_TEST) {
                    name = className =
                            RClassPresentationUtil.getRuntimeQualifiedNameInRubyTestMode(upperClass, fSWrapper);

                    testType = calculateTestTypeForClass(upperClass);
                    if (testType == null) {
                        return null;
                    }
                }
            //In Ruby Method
            } else if (currentContainer instanceof RMethod) {
                final RClass upperClass = RubyPsiUtil.getContainingUpperRClass(currentContainer);

                final Ref<FileSymbol> fSWrapper = new Ref<FileSymbol>();
                fileSemantic = (upperClass != null && RTestUnitUtil.isClassUnitTestCase(upperClass, fSWrapper))
                        ? FileSemantic.TEST_UNIT_TEST
                        : FileSemantic.RUBY_SCRIPT;

                methodName = currentContainer.getName();

                if (fileSemantic == FileSemantic.TEST_UNIT_TEST) {
                    className =
                            RClassPresentationUtil.getRuntimeQualifiedNameInRubyTestMode(upperClass, fSWrapper);
                    // Test Method is TestCase method which name starts with prefix "test"
                    if (upperClass == currentContainer.getParentContainer() &&
                            RTestUnitUtil.hasValidTestNameAndNotSingleton((RVirtualMethod) currentContainer)) {
                        testType = AbstractRubyRunConfiguration.TestType.TEST_METHOD;
                        name = methodName;
                    } else {
                        //Create configuration for class
                        name = className;

                        testType = calculateTestTypeForClass(upperClass);
                        if (testType == null) {
                            return null;
                        }
                    }
                }
            //In Ruby File
            } else if (currentContainer instanceof RFile) {
                fileSemantic = (RTestUnitUtil.checkForAnotherTestCases(null, (RFile) currentContainer))
                        ? FileSemantic.TEST_UNIT_TEST
                        : FileSemantic.RUBY_SCRIPT;
                testType = AbstractRubyRunConfiguration.TestType.TEST_SCRIPT;
            }
        }

// create corresponding configuration
        final RubyRunConfigurationFactory factory;
        switch (fileSemantic) {
            case TEST_UNIT_TEST:
                factory = myUnitTestsFactory;
                break;
            case RUBY_SCRIPT:
                factory = myAppFactory;
                break;
            default:
                factory = null;
        }

        final RunnerAndConfigurationSettings settings
                = RunManager.getInstance(project).createRunConfiguration(name, factory);

        final AbstractRubyRunConfiguration templateConfiguration =
                (AbstractRubyRunConfiguration) settings.getConfiguration();

// setupFileCache not default configuration settings
        // module
        final Module module = RubyRunConfigurationUtil.getCorrespondingModule(project, file, templateConfiguration);
        if (module == null) { //TODO module == null => choose first module's some sdk!
            return null;
        }

        //ignores template configuration's defaults
        templateConfiguration.setModule(module);

        //keeps template configuration's defaults
        if (TextUtil.isEmptyOrWhitespaces(templateConfiguration.getWorkingDirectory())) {
            // sets working dir
            final VirtualFile parentDir = file.getParent();
            final String dir = parentDir != null
                    ? parentDir.getPath()
                    : RailsFacetUtil.getRailsAppHomeDirPath(module);
            templateConfiguration.setWorkingDirectory(dir);
        }

        //specific settings
        switch (fileSemantic) {
            case RUBY_SCRIPT:
                //settings for ruby scripts
                initScriptConfiguration(file, templateConfiguration);
                break;
            case TEST_UNIT_TEST:
                // settings for unit tests
                initUnitTestConfiguration(file, testType, className, methodName, templateConfiguration);
                break;
        }
        return settings;
    }

    private RunnerAndConfigurationSettings createRunAllUnitTestsInFolderConf(@NotNull final PsiDirectory psiDirectory) {
        final Project project = psiDirectory.getProject();
        final VirtualFile folder = psiDirectory.getVirtualFile();

        final String name = RBundle.message("run.configuration.test.default.name", folder.getName());

        final RunnerAndConfigurationSettings settings
                = RunManager.getInstance(project).createRunConfiguration(name, myUnitTestsFactory);

        final RTestsRunConfiguration conf =
                (RTestsRunConfiguration) settings.getConfiguration();
        // module
        final Module module = RubyRunConfigurationUtil.getCorrespondingModule(project, folder, conf);
        if (module == null
            || !RModuleUtil.hasRubySupport(module)
            || !RModuleUtil.getRubySupportSettings(module).shouldUseTestUnitTestFramework()) {
            return null;
        }

        //ignores template's defaults
        conf.setModule(module);
        conf.setTestType(AbstractRubyRunConfiguration.TestType.ALL_IN_FOLDER);
        conf.setTestsFolderPath(folder.getPath());

        //keeps template's defaults
        if (TextUtil.isEmptyOrWhitespaces(conf.getTestFileMask())) {
            conf.setTestFileMask(RTestsRunConfiguration.DEFAULT_TESTS_SEARCH_MASK);
        }
        if (TextUtil.isEmptyOrWhitespaces(conf.getWorkingDirectory())) {
            conf.setWorkingDirectory(folder.getPath());
        }

        return settings;
    }

    /**
     * If our Unit TestCase is single testcase in script, we can run whole script
     *
     * @param rClass Given ruby class
     * @return TestType.TEST_CLASS or TestType.TEST_SCRIPT
     */
    private TestType calculateTestTypeForClass(final RClass rClass) {
        final TestType testType;
        final PsiFile psiFile = rClass.getContainingFile();
        if (psiFile instanceof RFile) {
            testType = RTestUnitUtil.checkForAnotherTestCases(rClass, (RFile) psiFile)
                    ? AbstractRubyRunConfiguration.TestType.TEST_CLASS
                    : AbstractRubyRunConfiguration.TestType.TEST_SCRIPT;
        } else {
            testType = null;
        }
        return testType;
    }

    private void initScriptConfiguration(final VirtualFile file,
                                         final AbstractRubyRunConfiguration configuration) {
        final RubyRunConfiguration conf = (RubyRunConfiguration) configuration;

        // setting script
        conf.setScriptPath(file.getPath());
    }

    private void initUnitTestConfiguration(final VirtualFile file, final TestType testType,
                                           @Nullable final String className,
                                           @Nullable final String methodName,
                                           final AbstractRubyRunConfiguration configuration) {
        final RTestsRunConfiguration conf = (RTestsRunConfiguration) configuration;

        //ignores template configuration's defaults
        assert testType != null;
        conf.setTestType(testType);
        conf.setTestScriptPath(file.getPath());

        //keeps template configuration's defaults
        // with shift (by default for all)
        RubyRunConfigurationUtil.setupRubyArgs(conf,
                RubyRunConfigurationUtil.collectArguments(RubyUtil.RUN_IN_CONSOLE_HACK_ARGUMENTS));

        //special settings
        switch (testType) {
            case TEST_CLASS:
                //ignores template configuration's defaults
                conf.setTestQualifiedClassName(className);
                //keeps template configuration's defaults
                // without shift, special for test class
                RubyRunConfigurationUtil.setupRubyArgs(conf,
                        RubyRunConfigurationUtil.collectArguments(RubyUtil.RUN_IN_CONSOLE_HACK_ARGUMENTS_NO_SHIFT));
                break;
            case TEST_METHOD:
                //ignores template configuration's defaults
                conf.setTestQualifiedClassName(className);
                conf.setTestMethodName(methodName);
                break;
            case TEST_SCRIPT:
                //Do nothing
                break;
        }
    }

    public enum FileSemantic {
        RSPEC_TEST,
        TEST_UNIT_TEST,
        RUBY_SCRIPT,
    }
}
