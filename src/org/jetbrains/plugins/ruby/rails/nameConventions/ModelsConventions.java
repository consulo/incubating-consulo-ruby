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

package org.jetbrains.plugins.ruby.rails.nameConventions;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.StandardRailsPaths;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 08.05.2007
 */
public class ModelsConventions {
    public static final String ACTIVER_RECORD = RailsConstants.SDK_ACTIVE_RECORD;
    public static final String ACTIVE_RECORD_MODULE = RailsConstants.SDK_ACTIVE_RECORD_MODULE;
    public static final String BASE_CLASS = RailsConstants.BASE_CLASS;

    public static boolean isModelFile(@NotNull final RVirtualFile rFile,
                                      @Nullable final Module module) {
        return isModelFile(rFile, module, RContainerUtil.getTopLevelClasses(rFile));
    }

    public static boolean isModelFile(@NotNull final RVirtualFile rFile,
                                      @Nullable final Module module,
                                      @NotNull final List<RVirtualClass> classes) {
        if (module == null){
            return false;
        }

        final String fileUrl = rFile.getContainingFileUrl();

        final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
        assert railsPaths != null; //Not null for modules with Rails Support

        final String modelsRoot =  railsPaths.getModelRootURL();
        if (!fileUrl.startsWith(modelsRoot)) {
             return false;
        }

        for (RVirtualClass virtualClass : classes) {
            if (isModelClass(virtualClass, module)){
                return true;
            }
        }
        return false;
    }

    public static boolean isModelClass(@Nullable final RVirtualClass rClass,
                                       @NotNull final Module module) {
        if (!RailsFacetUtil.hasRailsSupport(module)) {
            return false;
        }

        /**
         * TODO
         *
         * Uncomment next block when virtual file cache will be able
         * to parse complicated class names(such as Module1:Module2:ClassName)
         * into RVirtualElements hierarchy
         */

          /*  final ArrayList<RVirtualContainer> path = RVirtualPsiUtils.getVirtualPath(rClass);
            final String controllersRootURL = getControllersRootURL(module);
            assert controllersRootURL != null; // for rails modules isn't null

            final StringBuffer buff = new StringBuffer(controllersRootURL);
            for (RVirtualContainer container : path) {
                if (container instanceof RVirtualFile) {
                    continue;
                }
                buff.append(VFS_PATH_SEPARATOR);
                buff.append(RailsUtil.getControllerFileName(container.getName()));
            }
        */

        assert rClass != null; // for rails modules isn't null
        final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
        assert railsPaths != null; //Not null for modules with Rails Support
        final String fileUrl = rClass.getContainingFileUrl();

        //check directory
        final String modelsRoot =  railsPaths.getModelRootURL();
        if (!fileUrl.startsWith(modelsRoot)) {
             return false;
        }
        final String className = rClass.getName();
        final String fileName = VirtualFileUtil.removeExtension(VirtualFileUtil.getFileName(fileUrl));
        if (!NamingConventions.toUnderscoreCase(className).equals(fileName)) {
            return false;
        }

        //check superclsss
        final RVirtualName superClass = rClass.getVirtualSuperClass();
        if (superClass == null) {
            return false;
        }
        final List<String> path = superClass.getPath();
        //TODO it is HACK!!!! Doesn't work with inheritance
        return (path.size() == 2
                && ModelsConventions.ACTIVE_RECORD_MODULE.equals(path.get(0)))
                && BASE_CLASS.equals(path.get(1));

//        final String controllersRootURL = settings.getControllerRootURL();
//        assert controllersRootURL != null; // for rails modules isn't null
//
//        final StringBuffer buff = new StringBuffer(controllersRootURL);
//        for (String name : path) {
//            buff.append(VirtualFileUtil.VFS_PATH_SEPARATOR);
//            buff.append(RailsUtil.toUnderscoreCase(name));
//        }
//        buff.append('.').append(RubyFileType.RUBY.getDefaultExtension());
//        return buff.toString().equals(rClass.getContainingFileUrl());
    }

    @NotNull
    public static List<VirtualFile> getBuiltInAdapters(@NotNull final String activeRecordViewFileUrl) {
        final VirtualFileManager manager = VirtualFileManager.getInstance();

        final String url =
                VirtualFileUtil.removeExtension(activeRecordViewFileUrl)
                        + VirtualFileUtil.VFS_PATH_SEPARATOR
                        + RailsConstants.SDK_ACTIVE_RECORD_ADAPTERS_DIR_NAME;

        final VirtualFile adaptersDir = manager.findFileByUrl(url);
        if (adaptersDir == null) {
            return Collections.emptyList();
        }
        final Set<VirtualFile> files = new HashSet<VirtualFile>();
        RubyVirtualFileScanner.addRubyFiles(adaptersDir, files);
        final ArrayList<VirtualFile> filesList = new ArrayList<VirtualFile>();
        for (VirtualFile virtualFile : files) {
            filesList.add(virtualFile);
        }
        return filesList;
    }
}
