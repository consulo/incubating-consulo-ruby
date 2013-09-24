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

package org.jetbrains.plugins.ruby.settings;

import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 22.04.2007
 */
public class RPluginInfoUtil {
    // The same as in manifest
    @NonNls public static final String REVISION = "Revision";
    @NonNls public static final String BUILD = "Build";

    @NonNls public static final String RUBY_KEY = "RUBY";
    @NonNls public static final String JIRA = "http://www.jetbrains.net/jira";
    @NonNls public static final String JIRA_BROWSE = JIRA + "/browse/";
    @NonNls public static final String JIRA_BROWSE_RUBY_URL = JIRA_BROWSE + "/"+RUBY_KEY;

    @NonNls public static final String HOME_PAGE_ROOT_URL =     "http://www.jetbrains.net/confluence/display/RUBYDEV";
    @NonNls public static final String HOME_PAGE_URL =          HOME_PAGE_ROOT_URL + "/IntelliJ+IDEA+Ruby+Plugin";
    @NonNls public static final String RECENT_CHANGES_URL =     HOME_PAGE_ROOT_URL + "/Recent+changes";
    @NonNls public static final String FORUM_URL =              "http://www.intellij.net/forums/forum.jspa?forumID=75";
    @NonNls public static final String PLUGIN_REPOSITORY_URL =  "http://plugins.intellij.net/plugin/?id=1293";

    @NonNls private static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";

    @Nullable
    public static Manifest getManifest() {
        final String jarPath = PathUtil.getJarPathForClass(RPluginInfoUtil.class);
        if (jarPath == null || !jarPath.endsWith(".jar")) {
            return null;
        }
        final ZipFile jarFile;
        try {
            jarFile = new ZipFile(jarPath);
            final InputStream inputStream = jarFile.getInputStream(jarFile.getEntry(MANIFEST_PATH));
            return new Manifest(inputStream);
        } catch (final IOException e) {
            return null;
        }
    }

    @Nullable
    public static String getRevision(@Nullable final Manifest manifest) {
        return manifest == null ? null : manifest.getMainAttributes().getValue(REVISION);
    }

    @Nullable
    public static String getBuild(@Nullable final Manifest manifest) {
        return manifest == null ? null : manifest.getMainAttributes().getValue(BUILD);
    }
}
