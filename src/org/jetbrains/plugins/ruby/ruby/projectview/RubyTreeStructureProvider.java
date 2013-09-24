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

package org.jetbrains.plugins.ruby.ruby.projectview;

import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.jruby.JRubyUtil;
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
import org.jetbrains.plugins.ruby.settings.RApplicationSettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 30, 2007
 */
public class RubyTreeStructureProvider implements TreeStructureProvider, ProjectComponent {
    private final Project myProject;

    public RubyTreeStructureProvider(Project project) {
        myProject = project;
    }

    public Collection<AbstractTreeNode> modify(AbstractTreeNode parent, Collection<AbstractTreeNode> children, ViewSettings settings) {
// We should just return all the children if we don`t want to use useRubySpecific Project view
        if (!RApplicationSettings.getInstance().useRubySpecificProjectView){
            return children;
        }
        ArrayList<AbstractTreeNode> result = new ArrayList<AbstractTreeNode>();
        for (final AbstractTreeNode child : children) {
            Object o = child.getValue();
            if (o instanceof RFile) {
                final RFile rFile = (RFile) o;
                final Module fileModule = rFile.getModule();

// We should show special nodes only for ruby/rails or modules with JRuby facets!
                if (fileModule != null &&
                        (RubyUtil.isRubyModuleType(fileModule) || RailsFacetUtil.hasRailsSupport(fileModule) || JRubyUtil.hasJRubySupport(fileModule))){
                    final VirtualFile file = rFile.getVirtualFile();

                    //rspec tests
                    if (RSpecUtil.isRSpecTestFile(file)) {
                        result.add(child);
                        continue;
                    }

                    final RVirtualContainer virtualContainer = RVirtualPsiUtil.findVirtualContainer(rFile);
                    if (virtualContainer instanceof RVirtualFile) {
                        final RVirtualFile rVirtualFile = (RVirtualFile) virtualContainer;
                        final List<RVirtualClass> classes = RContainerUtil.getTopLevelClasses(virtualContainer);
                        final List<RVirtualModule> modules = RContainerUtil.getTopLevelModules(virtualContainer);

                        // Rails checks
                        if (RailsFacetUtil.hasRailsSupport(fileModule)) {
                            boolean foundRailsNode = false;
                            for (RVirtualClass aClass : classes) {
                                if (ControllersConventions.isControllerClass(aClass, fileModule) ||
                                        ModelsConventions.isModelClass(aClass, fileModule)){
                                    result.add(new RClassNode(myProject, fileModule, rVirtualFile, aClass, classes, ((ProjectViewNode) parent).getSettings()));
                                    foundRailsNode = true;
                                }
                            }
                            if (!foundRailsNode) {
                                for (RVirtualModule module : modules) {
                                    if (HelpersConventions.isHelperModule(module, fileModule)){
                                        result.add(new RModuleNode(myProject, fileModule, rVirtualFile, module, ((ProjectViewNode) parent).getSettings()));
                                        foundRailsNode = true;
                                    }
                                }
                            }
                            if (foundRailsNode){
                                continue;
                            }
                        }

                        // Default ruby behavour, checking names conventions
                        final String fileName = rFile.getVirtualFile().getNameWithoutExtension();
                        final String mixedFileName = NamingConventions.toMixedCase(fileName);
                        boolean foundRubyElement = false;
                        for (RVirtualStructuralElement structuralElement : rVirtualFile.getVirtualStructureElements()) {
                            final StructureType type = structuralElement.getType();
                            if (!foundRubyElement && type == StructureType.CLASS) {
                                if (Comparing.equal(mixedFileName, ((RVirtualClass)structuralElement).getName())) {
                                    result.add(new RClassNode(myProject, fileModule, rVirtualFile, (RVirtualClass) structuralElement, classes, ((ProjectViewNode) parent).getSettings()));
                                    foundRubyElement = true;
                                }
                            }
                            if (!foundRubyElement && type == StructureType.MODULE) {
                                if (Comparing.equal(mixedFileName, ((RVirtualModule)structuralElement).getName())) {
                                    result.add(new RModuleNode(myProject, fileModule, rVirtualFile, (RVirtualModule) structuralElement, ((ProjectViewNode) parent).getSettings()));
                                    foundRubyElement = true;
                                }
                            }
                        }
                        if (foundRubyElement){
                            continue;
                        }
                    }
                }
            }
            result.add(child);
        }
        return result;
    }

    public Object getData(Collection<AbstractTreeNode> selected, String dataName) {
        return null;
    }


    public void projectOpened() {
    }

    public void projectClosed() {
    }

    @NotNull
    public String getComponentName() {
        return RComponents.RUBY_TREE_STRUCTURE_PROVIDER;
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }
}
