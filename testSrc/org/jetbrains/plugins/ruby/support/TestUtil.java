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

package org.jetbrains.plugins.ruby.support;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.impl.ApplicationImpl;
import com.intellij.openapi.components.ComponentConfig;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.impl.ModuleImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.impl.ProjectImpl;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.util.LocalTimeCounter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.IntRef;
import org.jetbrains.plugins.ruby.jruby.JRubySupportLoader;
import org.jetbrains.plugins.ruby.rails.RailsSupportLoader;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLApplicationComponent;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLFile;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.lang.RubySupportLoader;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 07.09.2006
 */
public class TestUtil {
    private static final Logger LOG = Logger.getInstance(TestUtil.class.getName());

    @NonNls
    private final static String TEMP_FILE = "temp.rb";
    private final static String RHTML_TEMP_FILE = "temp.rhtml";
    /**
     * Creates pseudophysical file by given name and content
     * @param project Current project
     * @param text Content for file to be created
     * @return PsiFile - the resulting file
     */
    public static RFile createPseudoPhysicalFile(final Project project, final String text) {
        return (RFile) PsiManager.getInstance(project).getElementFactory().createFileFromText(TEMP_FILE, RubyFileType.INSTANCE,
                text, LocalTimeCounter.currentTime(), true);
    }

    public static RHTMLFile createPseudoPhysicalRHTMLFile(final Project project, final String text) {
        return (RHTMLFile) PsiManager.getInstance(project).getElementFactory().createFileFromText(RHTML_TEMP_FILE, RHTMLFileType.INSTANCE,
                text, LocalTimeCounter.currentTime(), true);
    }

    /**
     * Removes substring at pos index with length legth
     * @param s string
     * @param index The start pos of substring to delete
     * @param length length of substring to delete
     * @param wrappers Index wrappers
     * @return new String
     */
    public static String removeSubstring(final String s, final int index, final int length, IntRef... wrappers){
        for (IntRef i : wrappers) {
            if (i.get()>index){
                i.dec(length);
            }
        }
        return s.substring(0, index) + s.substring(index+length);
    }

    /**
     * Inserts substring at pos index with length legth
     * @param s string
     * @param index The start pos of substring to insert
     * @param length length of substring to insert
     * @param wrappers Index wrappers
     * @return new String
     * @param sub string to insert
     */
    public static String insertSubstring(final String s, final String sub, final int index, final int length, IntRef... wrappers){
        for (IntRef i : wrappers) {
            if (index<i.get()){
                i.inc(length);
            }
        }
        return s.substring(0, index) + sub +  s.substring(index);
    }

    public static void loadRubySupport() {
        RubySupportLoader.loadRuby();
    }

    public static void loadRailsSupport() {
        RailsSupportLoader.loadRails();
        RHTMLApplicationComponent.loadRHTML();
    }

    public static void loadJRubySupport() {
        JRubySupportLoader.loadJRuby();
    }

    public static void loadRORAppComponents() {
        final Application application = ApplicationManager.getApplication();

        loadApplicationComponentIfIsntLoaded(application, RubyModuleType.class);
//        loadApplicationComponentIfIsntLoaded(application, RApplicationSettings.class);
    }

    private static void loadApplicationComponentIfIsntLoaded(@NotNull final Application application, final Class aClass) {
        //noinspection unchecked
        if (application.getComponent(aClass) == null) {
            ((ApplicationImpl)application).registerComponent(createConfig(aClass));
        }
    }

    public static void loadProjectComponentIfIsntLoaded(@NotNull final Project project, final Class aClass) {
        //noinspection unchecked
        if (project.getComponent(aClass) == null) {
            ((ProjectImpl)project).registerComponent(createConfig(aClass));
        }
    }

    public static void loadModuleComponentIfIsntLoaded(@NotNull final Module module, final Class aClass) {
        //noinspection unchecked
        if (module.getComponent(aClass) == null) {
            ((ModuleImpl)module).registerComponent(createConfig(aClass));
        }
    }

    public static void removeModuleContentRoots(final Module module) {
        ApplicationManager.getApplication().runWriteAction(
                new Runnable() {
                    public void run() {
                        try {
                            PsiTestUtil.removeAllRoots(module, RModuleUtil.getModuleOrJRubyFacetSdk(module));
                        }
                        catch (Exception e) {
                            LOG.error(e);
                        }
                    }
                }
        );
    }

    public static ComponentConfig createConfig(final Class aClass) {
        final ComponentConfig config = new ComponentConfig();
        config.implementationClass = aClass.getName();
        config.interfaceClass = null;
        config.options = null;
        return config;
    }
}
