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

import java.io.File;
import java.io.IOException;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.util.SystemInfo;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Nov 30, 2007
 */
public class RubySdkAdditionalData implements SdkAdditionalData {
	private static final String GEMS_BIN_DIR_PATH = "GEMS_BIN_DIR_PATH";

    private String myGemsBinDirectory;

    @NotNull
    public String getGemsBinDirectory() {
        return myGemsBinDirectory;
    }

    public void setGemsBinDirectory(@NotNull final String path) {
        myGemsBinDirectory = path;
    }


    @Override
	public Object clone() throws CloneNotSupportedException {
        try {
            final RubySdkAdditionalData copy = (RubySdkAdditionalData) super.clone();
            copy.setGemsBinDirectory(myGemsBinDirectory);
            return copy;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public void checkValid(SdkModel sdkModel) throws ConfigurationException {
        final File file = new File(myGemsBinDirectory);
        if (!file.exists()) {
            throw new ConfigurationException(RBundle.message("sdk.error.gems.bindir.doesnt.exist"));
        }

        if (!file.isDirectory()) {
            throw new ConfigurationException(RBundle.message("sdk.error.gems.bindir.isnt.directory"));
        }
    }

    public void save(@NotNull final Element rootElement) {
        rootElement.setAttribute(GEMS_BIN_DIR_PATH, getGemsBinDirectory());
    }

    @NotNull
    public static SdkAdditionalData load(@NotNull final Sdk sdk, @Nullable Element additional) {
        final RubySdkAdditionalData data = new RubySdkAdditionalData();

        final String value = additional != null ? additional.getAttributeValue(GEMS_BIN_DIR_PATH) : null;
        if (value == null) {
            final String patchedGemBinDir = patcedMacOsGemsBinPath(sdk);
            data.setGemsBinDirectory(patchedGemBinDir == null
                                        ? RubySdkType.getInstance().getBinPath(sdk)
                                        : patchedGemBinDir);
        } else {
            data.setGemsBinDirectory(value);
        }

        return data;
    }

    @Nullable
    private static String patcedMacOsGemsBinPath(final Sdk sdk) {
        if (SystemInfo.isMac) {
            final String rubyExecutablePath = RubySdkType.getInstance().getVMExecutablePath(sdk);

            // Gems bin directory "hack" for MacOS defauult ruby installation
            // because it differs from ruby interpreter home.
            final File rubyExecFile = new File(rubyExecutablePath);
            try {
                if (rubyExecFile.exists()) {
                    if (rubyExecFile.getCanonicalPath().startsWith(RubySdkType.MAC_OS_BUNDLED_RUBY_PATH_PREFIX)) {
                        return RubySdkType.MAC_OS_BUNDLED_RUBY_GEM_BIN_PATH;
                    }
                }
            } catch (SecurityException e) {
                // Do nothing
            } catch (IOException e) {
                // Do noting
            }
        }
        return null;
    }
}
