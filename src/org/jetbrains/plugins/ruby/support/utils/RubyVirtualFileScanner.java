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

package org.jetbrains.plugins.ruby.support.utils;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ModuleFileIndex;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.rails.nameConventions.ViewsConventions;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: oleg, Roman Chernyatchik
 * Date: 05.07.2006
 */
public class RubyVirtualFileScanner {

    @NonNls
    private static final String PREFIX = "../";
    @NonNls
    private static final String NAME_CHECK_PATTERN = "(_|[a-z]).*";

    /**
     * Searches all ruby files in directory.
     * <p/>
     * Use this method for files that doesn't belong to any module, othwerwise
     * see <code>searchRubyFilesUnderDirectory(@Nullable final Module module, @NotNull final VirtualFile dir)</code>
     *
     * @param file     File or directory
     * @param allFiles Method adds all found files into this set
     */
    public static void addRubyFiles(@Nullable final VirtualFile file,
                                    @NotNull final Set<VirtualFile> allFiles) {
        if (file == null
                || FileTypeManager.getInstance().isFileIgnored(file.getName())) {
            return;
        }
        if (!file.isDirectory()) {
            if (isRubyFile(file)) {
                allFiles.add(file);
            }
            return;
        }
        final VirtualFile[] children = file.getChildren();
        for (final VirtualFile child : children) {
            addRubyFiles(child, allFiles);
        }
    }

    public static boolean isRubyFile(@Nullable final VirtualFile fileOrDir) {
        return !(fileOrDir == null || fileOrDir.isDirectory()) &&
                isRubyFile(fileOrDir.getName());
    }

    /**
     * @param fileName file name with extension
     * @return if file with such name and extension may be ruby file
     */
    public static boolean isRubyFile(@NotNull final String fileName) {
        final FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName(fileName);
        return fileType instanceof RubyFileType;
    }

    /**
     * Searches all files in module than may be added to RubyFileCache
     *
     * @param manager ruby or rails module root manager
     * @param files
     * @return files
     */
    public static void searchRubyFileCacheFiles(@NotNull final ModuleRootManager manager, final Collection<VirtualFile> files) {
        final ModuleFileIndex moduleFileIndex = getFileIndex(manager);
        if (moduleFileIndex == null){
            return;
        }
        moduleFileIndex.iterateContent(new ContentIterator() {
            @Override
			public boolean processFile(VirtualFile fileOrDir) {
                if (isRubyFile(fileOrDir)) {
                    files.add(fileOrDir);
                }
                return true;
            }
        });
    }

    /**
     * Searches all files in manager than may be added to RailsFileCache
     *
     * @param manager ruby or rails module root manager manager
     * @param files
     * @return files
     */
    public static void searchAdditionalRailsFileCacheFiles(@NotNull final ModuleRootManager manager, final Collection<VirtualFile> files) {
        final ModuleFileIndex moduleFileIndex = getFileIndex(manager);
        if (moduleFileIndex == null){
            return;
        }
        moduleFileIndex.iterateContent(new ContentIterator() {
            @Override
			public boolean processFile(VirtualFile fileOrDir) {
                if (ViewsConventions.isViewFile(fileOrDir)) {
                    files.add(fileOrDir);
                }
                return true;
            }
        });
    }

    /**
     * Searches not recursively all files in the content under directory
     * dir (including the directory itself). Does not iterate anything if dir is not in the content.
     *
     * @param module      ruby or rails module
     * @param dirOrFile   directory
     * @param includeDirs if true diectories will be also included.
     * @return files
     */
    public static List<VirtualFile> searchFilesUnderDirectory(@Nullable final Module module,
                                                              @NotNull final VirtualFile dirOrFile,
                                                              final boolean includeDirs) {
        final List<VirtualFile> files = new LinkedList<VirtualFile>();
        final ModuleFileIndex moduleFileIndex = getFileIndex(ModuleRootManager.getInstance(module));
        if (moduleFileIndex == null){
            return files;
        }

        if (moduleFileIndex.isInContent(dirOrFile)) {
            if (dirOrFile.isDirectory()) {
                VirtualFile[] children = dirOrFile.getChildren();
                for (VirtualFile child : children) {
                    if (!moduleFileIndex.isInContent(child)) {
                        continue;
                    }
                    if (!child.isDirectory() || includeDirs) {
                        files.add(child);
                    }
                }
            }
            files.add(dirOrFile);
        }
        return files;
    }

    /**
     * Finds all the relative paths
     *
     * @param rootDirectory root File
     * @return List of relative pathes to ruby files found in rootFile
     */
    public static List<String> getRelativeUrls(@NotNull final VirtualFile rootDirectory) {
        final List<String> relativeUrls = new ArrayList<String>();
        return addRelativeUrls(rootDirectory, relativeUrls);
    }

    public static List<String> addRelativeUrls(@NotNull final VirtualFile rootDirectory,
                                               @NotNull final List<String> relativeUrls) {
        final String rootUrl = rootDirectory.getUrl() + '/';
        final int length = rootUrl.length();
        Set<VirtualFile> list = new HashSet<VirtualFile>();
        addRubyFiles(rootDirectory, list);
        for (VirtualFile file : list) {
            final String url = file.getUrl();
            relativeUrls.add(url.substring(length));
        }
        return relativeUrls;
    }

    /**
     * Finds all the relative paths in lib subdirectories of given directories
     *
     * @param dirs Directories - array with not null files
     * @return List of relative pathes to ruby files found
     */
    public static List<String> getRelativeLibsUrls(@NotNull final VirtualFile[] dirs) {
        final List<String> relativeUrls = new ArrayList<String>();

        final List<VirtualFile> libsDirs = findLibsSubDirectories(dirs);
        for (VirtualFile lib : libsDirs) {
            addRelativeUrls(lib, relativeUrls);
        }

        return relativeUrls;
    }

    public static List<VirtualFile> findLibsSubDirectories(VirtualFile[] dirs) {
        final List<VirtualFile> libsDirs = new ArrayList<VirtualFile>();
        for (VirtualFile dir : dirs) {
            final VirtualFile lib = dir.findChild(RailsConstants.LIB_FOLDER);
            if (lib != null) {
                libsDirs.add(lib);
            }
        }
        return libsDirs;
    }


    /**
     * Finds all the relative paths in given manager for given file
     *
     * @param manager Current module root manager
     * @param onlyDirectoryFiles use only directory files, i.e. no ../../
     * @param directory current directory @return List of relative urls
     * @return list of relative urls
     */
    public static List<String> getRelativeUrlsForModule(@NotNull final ModuleRootManager manager,
                                                        final boolean onlyDirectoryFiles,
                                                        @NotNull final VirtualFile directory) {
        final List<String> urls = new ArrayList<String>();
        final ModuleFileIndex moduleFileIndex = getFileIndex(manager);
        if (moduleFileIndex == null){
            return urls;
        }

        VirtualFile anchor = directory;
        String prefix = "";
        while (anchor != null && moduleFileIndex.isInContent(anchor)) {
             for (String relativeUrl : getRelativeUrls(anchor)) {
                urls.add(prefix + relativeUrl);
            }
            if (onlyDirectoryFiles){
                return urls;
            }
            anchor = anchor.getParent();
            prefix = PREFIX + prefix;
        }
        return urls;
    }

    /**
     * Gathers all the relative urls under root with all it`s subdirectories
     * @param fileRoot Root file
     * @param checkName true if check enabled: directory name must start with an underscore or a lowercase letter
     * @return List of relative urls
     */
    public static List<String> getRelativeUrlsUnderRoot(@NotNull final VirtualFile fileRoot, final boolean checkName) {
        final ArrayList<String> list = new ArrayList<String>();
        getRelativeUrlsUnderRootRec(fileRoot, list, checkName);
        return list;
    }

    public static List<VirtualFile> getRelativeFilesUnderRoot(@NotNull final VirtualFile fileRoot,
                                                              final boolean checkName,
                                                              final boolean addRubyFiles,
                                                              final boolean addDirectories) {
        final ArrayList<VirtualFile> list = new ArrayList<VirtualFile>();
        getRelativeFilesUnderRootRec(fileRoot, list, checkName, addRubyFiles, addDirectories);
        return list;
    }

    private static void getRelativeUrlsUnderRootRec(@NotNull final VirtualFile file,
                                                    @NotNull final List<String> list,
                                                    final boolean checkName) {
        if (FileTypeManager.getInstance().isFileIgnored(file.getName())) {
            return;
        }
        if (file.isDirectory()) {
            if (checkName && !file.getName().matches(NAME_CHECK_PATTERN)) {
                return;
            }
        } else {
            if (isRubyFile(file)) {
                list.add(file.getName());
            }
            return;
        }
        for (VirtualFile child : file.getChildren()) {
            getRelativeUrlsUnderRootRec(child, list, checkName);
        }
    }
    private static void getRelativeFilesUnderRootRec(@NotNull final VirtualFile file,
                                                    @NotNull final List<VirtualFile> list,
                                                    final boolean checkName,
                                                    final boolean addRubyFiles,
                                                    final boolean addDirectories) {

        if (FileTypeManager.getInstance().isFileIgnored(file.getName())) {
            return;
        }
        if (file.isDirectory()) {
            if (checkName && !file.getName().matches(NAME_CHECK_PATTERN)) {
                return;
            }
            if (addDirectories) {
                list.add(file);
            }
        } else {
            if (isRubyFile(file) && addRubyFiles) {
                list.add(file);
            }
            return;
        }
        for (VirtualFile child : file.getChildren()) {
            getRelativeFilesUnderRootRec(child, list, checkName, addRubyFiles, addDirectories);
        }
    }

    /**
     * Adds all files and directories under given directory to given list, method isn't reqursive
     * @param dir Given dir
     * @param list Given file list
     * @param checkName Check than pattern (_|[a-z]).* accepts file name
     * @param addRubyFiles If true ruby files will be included
     * @param addDirectories If true directories will be included
     */
    public static void addFilesFromDirectory(@NotNull final VirtualFile dir,
                                           @NotNull final List<VirtualFile> list,
                                           final boolean checkName,
                                           final boolean addRubyFiles,
                                           final boolean addDirectories) {

        if (FileTypeManager.getInstance().isFileIgnored(dir.getName())) {
            return;
        }
        if (dir.isDirectory()) {
            if (checkName && !dir.getName().matches(NAME_CHECK_PATTERN)) {
                return;
            }
            if (addDirectories) {
                list.add(dir);
            }
        } else {
            if (isRubyFile(dir) && addRubyFiles) {
                list.add(dir);
            }
            return;
        }
        for (VirtualFile child : dir.getChildren()) {
            getRelativeFilesUnderRootRec(child, list, checkName, addRubyFiles, addDirectories);
        }
    }


    @Nullable
    public static ModuleFileIndex getFileIndex(@NotNull final ModuleRootManager manager){
        return manager.getModule().isDisposed() ? null : manager.getFileIndex();
    }
}
