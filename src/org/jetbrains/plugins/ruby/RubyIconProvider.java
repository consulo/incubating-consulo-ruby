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

package org.jetbrains.plugins.ruby;

import com.intellij.ide.IconProvider;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.IconSet;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecIcons;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.jruby.JRubyUtil;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.HelpersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.ModelsConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.NamingConventions;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.RClassPresentationUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.RModulePresentationUtil;
import org.jetbrains.plugins.ruby.ruby.roots.RProjectContentRootManager;
import org.jetbrains.plugins.ruby.settings.RApplicationSettings;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 21, 2007
 */
public class RubyIconProvider implements ApplicationComponent, IconProvider {
    @Nullable
    public Icon getIcon(@NotNull PsiElement element, int flags) {
        if (element instanceof PsiDirectory) {
            final boolean expanded = (flags & Iconable.ICON_FLAG_OPEN) != 0;

            final PsiDirectory dir = (PsiDirectory)element;
            final Project project = dir.getProject();
            final VirtualFile file = dir.getVirtualFile();
            final RProjectContentRootManager manager = RProjectContentRootManager.getInstance(project);

            if (manager == null) {
                return null;
            }

            final String fileUrl = file.getUrl();
            if (manager.isUnderTestUnitRoot(fileUrl)) {
                if (manager.isTestUnitRoot(fileUrl)) {
                    return IconSet.getSourceRootIcon(true, expanded);
                } else {
                    return IconSet.getSourceFolderIcon(true, expanded);
                }
            }
        } else
        if (element instanceof RFile) {
            final RFile rFile = (RFile) element;
            final Module fileModule = rFile.getModule();

// We should show special icons only for ruby/rails or modules with JRuby facets!
            if (fileModule != null &&
                    (RubyUtil.isRubyModuleType(fileModule) || RailsFacetUtil.hasRailsSupport(fileModule) || JRubyUtil.hasJRubySupport(fileModule))){
                // RSpec check
                if (RSpecUtil.isFileWithRSpecTestFileName(rFile.getVirtualFile())) {
                    return RSpecIcons.TEST_SCRIPT_ICON;
                }

// We should just return null to use default ruby file icon if we don`t want to use useRubySpecific Project view
                if (!RApplicationSettings.getInstance().useRubySpecificProjectView){
                    return null;
                }

                final RVirtualContainer virtualContainer = RVirtualPsiUtil.findVirtualContainer(rFile);
                if (virtualContainer instanceof RVirtualFile) {
                    final RVirtualFile rVirtualFile = (RVirtualFile) virtualContainer;
                    final List<RVirtualClass> classes = RContainerUtil.getTopLevelClasses(virtualContainer);
                    final List<RVirtualModule> modules = RContainerUtil.getTopLevelModules(virtualContainer);

                    // Rails checks
                    if (RailsFacetUtil.hasRailsSupport(fileModule)) {
                        if (ControllersConventions.isControllerFile(rFile, fileModule, classes)){
                            return RailsIcons.RAILS_CONTROLLER_NODE;
                        }
                        if (ModelsConventions.isModelFile(rFile, fileModule, classes)){
                            return RailsIcons.RAILS_MODEL_NODE;
                        }
                        if (HelpersConventions.isHelperFile(rFile, fileModule, modules)){
                            return RailsIcons.RAILS_HELPER_NODE;
                        }
                    }

                    // Default ruby behavour, checking names conventions
                    //noinspection ConstantConditions
                    final String fileName = rFile.getVirtualFile().getNameWithoutExtension();
                    final String mixedFileName = NamingConventions.toMixedCase(fileName);
                    for (RVirtualStructuralElement structuralElement : rVirtualFile.getVirtualStructureElements()) {
                        final StructureType type = structuralElement.getType();
                        if (type == StructureType.CLASS) {
                            if (Comparing.equal(mixedFileName, ((RVirtualClass)structuralElement).getName())) {
                                return RClassPresentationUtil.getIcon();
                            }
                        }
                        if (type == StructureType.MODULE) {
                            if (Comparing.equal(mixedFileName, ((RVirtualModule)structuralElement).getName())) {
                                return RModulePresentationUtil.getIcon();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return RComponents.RUBY_ICON_PROVIDER;
    }

    public void initComponent() {
        // Do nothing
    }

    public void disposeComponent() {
         //Do nothing
    }
}
