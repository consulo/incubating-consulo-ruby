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

package org.jetbrains.plugins.ruby.ruby.sdk.jruby;

import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 31, 2008
 */
public class JRubySdkUtil {
    /**
     * Checks if sdk is JRuby SDK
     *
     * @param sdk sdk to inspect
     * @return true, if sdk is Ruby type
     */
    public static boolean isJRubySDK(@Nullable final ProjectJdk sdk) {
        return sdk != null && sdk.getSdkType() instanceof JRubySdkType;
    }

    /**
     * Creates mock sdk
     */
    @SuppressWarnings({"JavaDoc"})
    public static ProjectJdk getMockSdk(final String versionName) {
        return RubySdkUtil.createMockSdk(JRubySdkType.getInstance(), versionName);
    }

    /**
     * Creates mock sdk without stubs
     */
    @SuppressWarnings({"JavaDoc"})
    public static ProjectJdk getMockSdkWithoutStubs(final String versionName) {
        return RubySdkUtil.createMockSdkWithoutStubs(JRubySdkType.getInstance(), versionName);
    }

    public static ProjectJdk[] getValidSdks() {
        final ArrayList<ProjectJdk> foundSdks = new ArrayList<ProjectJdk>();
        foundSdks.add(null);
        for (ProjectJdk sdk : ProjectJdkTable.getInstance().getAllJdks()) {
            if (JRubySdkType.isJRubySDK(sdk)) {
                foundSdks.add(sdk);
            }
        }
        return foundSdks.toArray(new ProjectJdk[foundSdks.size()]);
    }
}
