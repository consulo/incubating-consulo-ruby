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

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTable;

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
    public static boolean isJRubySDK(@Nullable final Sdk sdk) {
        return sdk != null && sdk.getSdkType() instanceof JRubySdkType;
    }

    /**
     * Creates mock sdk
     */
    @SuppressWarnings({"JavaDoc"})
    public static Sdk getMockSdk(final String versionName) {
        return RubySdkUtil.createMockSdk(JRubySdkType.getInstance(), versionName);
    }

    /**
     * Creates mock sdk without stubs
     */
    @SuppressWarnings({"JavaDoc"})
    public static Sdk getMockSdkWithoutStubs(final String versionName) {
        return RubySdkUtil.createMockSdkWithoutStubs(JRubySdkType.getInstance(), versionName);
    }

    public static Sdk[] getValidSdks() {
        final ArrayList<Sdk> foundSdks = new ArrayList<Sdk>();
        foundSdks.add(null);
        for (Sdk sdk : SdkTable.getInstance().getAllSdks()) {
            if (JRubySdkType.isJRubySDK(sdk)) {
                foundSdks.add(sdk);
            }
        }
        return foundSdks.toArray(new Sdk[foundSdks.size()]);
    }
}
