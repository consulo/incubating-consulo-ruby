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

package org.jetbrains.plugins.ruby.jruby.facet;

import java.util.Collection;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.jruby.JRubyIcons;
import org.jetbrains.plugins.ruby.jruby.JRubyUtil;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.autodetecting.FacetDetector;
import com.intellij.facet.autodetecting.FacetDetectorRegistry;
import com.intellij.facet.impl.autodetecting.FacetDetectorRegistryEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.PsiFile;

public class JRubyFacetType extends FacetType<JRubyFacet, JRubyFacetConfiguration> {
    public static final JRubyFacetType INSTANCE = new JRubyFacetType();


    public static void load(){

    }

    public JRubyFacetType() {
        super(JRubyFacet.ID, JRubyFacet.ID.toString(), RBundle.message("jruby.facet"));
    }

    @Override
	public JRubyFacetConfiguration createDefaultConfiguration() {
        return new JRubyFacetConfiguration();
    }

    public JRubyFacet createFacet(@NotNull Module module, String name, @NotNull JRubyFacetConfiguration configuration, @Nullable Facet underlyingFacet) {
        return new JRubyFacet(this, module, name, configuration, underlyingFacet);
    }

    public Icon getIcon() {
        return JRubyIcons.JRUBY_ICON;
    }



    public void registerDetectors(@NotNull final FacetDetectorRegistry<JRubyFacetConfiguration> registry) {
        final FacetDetectorRegistryEx<JRubyFacetConfiguration> detectorRegistry = (FacetDetectorRegistryEx<JRubyFacetConfiguration>) registry;

        final VirtualFileFilter jrubyFacetFilter = new VirtualFileFilter(){
            @Override
			public boolean accept(VirtualFile virtualFile) {
                return JRubyFacetStructure.isValidForJRubyFacet(virtualFile);
            }
        };

        final Condition<PsiFile> condition = new Condition<PsiFile>() {
            @Override
			public boolean value(PsiFile psiFile) {
                final VirtualFile vFile = psiFile.getVirtualFile();
                if (vFile == null) {
                    return false;
                }
                final Module module = ModuleUtil.findModuleForFile(vFile, psiFile.getProject());
                return  module != null
                        && !RubyUtil.isRubyModuleType(module)
                        && !JRubyUtil.hasJRubySupport(module)
                        && JRubyFacetStructure.isValidForJRubyFacet(vFile);
            }
        };
        detectorRegistry.registerOnTheFlyDetector(RubyFileType.RUBY, jrubyFacetFilter, condition, new FacetDetector<PsiFile, JRubyFacetConfiguration>() {
            public JRubyFacetConfiguration detectFacet(PsiFile source, Collection<JRubyFacetConfiguration> existentFacetConfigurations) {
                return JRubyFacetType.this.detectFacet(existentFacetConfigurations);
            }
        });

        detectorRegistry.registerDetectorForWizard(RubyFileType.RUBY, jrubyFacetFilter, new FacetDetector<VirtualFile, JRubyFacetConfiguration>() {
            public JRubyFacetConfiguration detectFacet(VirtualFile source, Collection<JRubyFacetConfiguration> existentFacetConfigurations) {
                return JRubyFacetType.this.detectFacet(existentFacetConfigurations);
            }
        });
    }

    private JRubyFacetConfiguration detectFacet(Collection<JRubyFacetConfiguration> existentFacetConfigurations) {
        if (!existentFacetConfigurations.isEmpty()) {
            return existentFacetConfigurations.iterator().next();
        }
        return createDefaultConfiguration();
    }
}
