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

package org.jetbrains.plugins.ruby.ruby.sdk;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.ProjectRootType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ActionRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.JRubySdkTableListener;
import org.jetbrains.plugins.ruby.ruby.sdk.ui.RubySdkConfigurablePanel;
import org.jetbrains.plugins.ruby.support.utils.IdeaInternalUtil;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 28, 2007
 */
public class RubySdkConfigurable  implements AdditionalDataConfigurable {
    private Sdk mySdk;
    private RubySdkConfigurablePanel myRubyConfigurablePanel;

    private String myGemsPath;

    @Nullable
    public String getGemsPath() {
        return myGemsPath;
    }

    public void setGemsPath(@Nullable final String gemsPath) {
        myGemsPath = gemsPath;
    }

    public RubySdkConfigurable() {
        myRubyConfigurablePanel = new RubySdkConfigurablePanel();
    }

    public void setSdk(@NotNull final Sdk sdk) {
        mySdk = sdk;
    }

    public JComponent createComponent() {
        return myRubyConfigurablePanel.getPanel();
    }

    public boolean isModified() {
        return !RubySdkUtil.getGemsBinFolderPath(mySdk).equals(myRubyConfigurablePanel.getGemsBinFolder());
    }

    public void apply() throws ConfigurationException {
        //Gems bin folder
        final String newGemsBinFolder = myRubyConfigurablePanel.getGemsBinFolder();
        ((RubySdkType) mySdk.getSdkType()).setGemsBinDirectory(mySdk, newGemsBinFolder);

        //Patch source roots according to classpath
        final SdkModificator modificator = mySdk.getSdkModificator();
        modificator.removeRoots(ProjectRootType.SOURCE);
        for (VirtualFile file : modificator.getRoots(ProjectRootType.CLASS)) {
            modificator.addRoot(file, ProjectRootType.SOURCE);
        }
        RubySdkType.findAndSaveGemsRootsBy(modificator);     

        modificator.commitChanges();

// Change libraries facet libraries
        IdeaInternalUtil.runInsideWriteAction(new ActionRunner.InterruptibleRunnable(){
            public void run() throws Exception {
                JRubySdkTableListener.updateLibrary(mySdk.getName(), modificator.getRoots(ProjectRootType.CLASS));
            }
        });
    }

    public void reset() {
        final RubySdkType type = (RubySdkType)mySdk.getSdkType();
        myRubyConfigurablePanel.setRubyText(type.getVMExecutablePath(mySdk));
        myRubyConfigurablePanel.setGemsBinFolder(RubySdkUtil.getGemsBinFolderPath(mySdk));
        myRubyConfigurablePanel.getPanel().repaint();
    }

    public void disposeUIResources() {
        // do nothing. we don`t have them
    }

}
