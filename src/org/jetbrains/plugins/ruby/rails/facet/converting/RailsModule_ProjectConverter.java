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

package org.jetbrains.plugins.ruby.rails.facet.converting;

import com.intellij.application.options.PathMacrosImpl;
import com.intellij.facet.FacetTypeId;
import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.highlighter.ProjectFileType;
import com.intellij.ide.highlighter.WorkspaceFileType;
import com.intellij.ide.impl.convert.JDomConvertingUtil;
import com.intellij.ide.impl.convert.ProjectConversionHelper;
import com.intellij.ide.impl.convert.ProjectConverter;
import com.intellij.ide.impl.convert.QualifiedJDomException;
import com.intellij.openapi.components.ExpandMacroToPathMap;
import com.intellij.openapi.module.impl.ModuleManagerImpl;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.SystemProperties;
import org.jdom.Document;
import org.jdom.Element;
import org.jetbrains.plugins.ruby.rails.facet.versions.BaseRailsFacet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jun 9, 2008
 */
public class RailsModule_ProjectConverter implements ProjectConverter {
    private String myProjectFilePath;
    private boolean myPrepared;

    private List<ModuleFile> myModules;
    private RailsModule_ConvertingContext myContext;
    private File myWorkspaceFile;
    private List<File> myNonexistentFiles;

    public RailsModule_ProjectConverter(final String projectFilePath) {
        myProjectFilePath = projectFilePath;
    }

    public File getBaseDirectory() {
        return new File(FileUtil.toSystemDependentName(myProjectFilePath)).getParentFile();
    }

    public void prepare() throws IOException, QualifiedJDomException {
        if (myPrepared) {
            return;
        }

        final Element root = loadProjectFileRoot();

        initModules(getModuleFiles(root));

        final String basePath = myProjectFilePath.substring(0, myProjectFilePath.length() - ProjectFileType.DOT_DEFAULT_EXTENSION.length());
        myWorkspaceFile = new File(FileUtil.toSystemDependentName(basePath + "." + WorkspaceFileType.DEFAULT_EXTENSION));

        myPrepared = true;
    }

    public File[] getAffectedFiles() {
      List<File> files = new ArrayList<File>();
      for (ModuleFile module : myModules) {
        files.add(module.myModuleFile);
      }
      if (myWorkspaceFile.exists()) {
        files.add(myWorkspaceFile);
      }
      return files.toArray(new File[files.size()]);
    }

    public void convert() throws IOException, QualifiedJDomException {
        convertModules();
        convertWorkspaceFile();
    }

    public boolean isConversionNeeded() {
        return !myModules.isEmpty();
    }

    public ProjectConversionHelper createHelper() {
        return new RailsModule_ProjectConversionHelper(myContext);
    }

    public List<File> getNonexistentFiles() {
        return myNonexistentFiles;
    }

    protected void addMacros(final ExpandMacroToPathMap macros) {
        PathMacrosImpl.getInstanceEx().addMacroExpands(macros);
    }

    private Element loadProjectFileRoot() throws QualifiedJDomException, IOException {
        final Document document = JDomConvertingUtil.loadDocument(new File(FileUtil.toSystemDependentName(myProjectFilePath)));
        return document.getRootElement();
    }


    private File[] getModuleFiles(final Element root) {
        final Element modulesManager = JDomConvertingUtil.findComponent(root, ModuleManagerImpl.COMPONENT_NAME);
        if (modulesManager == null) return new File[0];


        final Element modules = modulesManager.getChild(ModuleManagerImpl.ELEMENT_MODULES);
        if (modules == null) return new File[0];

        final ExpandMacroToPathMap macros = new ExpandMacroToPathMap();
        final String projectDir = FileUtil.toSystemIndependentName(new File(myProjectFilePath).getParent());
        macros.addMacroExpand(PathMacrosImpl.PROJECT_DIR_MACRO_NAME, projectDir);
        addMacros(macros);

        List<File> files = new ArrayList<File>();
        final List list = modules.getChildren(ModuleManagerImpl.ELEMENT_MODULE);
        for (Object o : list) {
          Element module = (Element)o;
          String filePath = module.getAttributeValue(ModuleManagerImpl.ATTRIBUTE_FILEPATH);
          filePath = macros.substitute(filePath, true, null);
          files.add(new File(FileUtil.toSystemDependentName(filePath)));
        }

        return files.toArray(new File[files.size()]);
    }

    private void initModules(final File[] moduleFiles) throws QualifiedJDomException, IOException {
        myModules = new ArrayList<ModuleFile>();
        myContext = new RailsModule_ConvertingContext();
        myNonexistentFiles = new ArrayList<File>();

        for (File moduleFile : moduleFiles) {
            if (!moduleFile.exists()) {
              myNonexistentFiles.add(moduleFile);
              continue;
            }

            final Document document = JDomConvertingUtil.loadDocument(moduleFile);
            final Element moduleRoot = document.getRootElement();

            final FacetTypeId<? extends BaseRailsFacet> type = RailsModule_ConvertingUtil.getFacetType(moduleRoot);
            if (type != null) {
              final String moduleName = getModuleName(moduleFile.getName());
              myModules.add(new ModuleFile(document, moduleName, moduleFile));
              myContext.registerModule(moduleName, type);
            }
        }
    }

    public static String getModuleName(final String fileName) {
        if (fileName.endsWith(ModuleFileType.DOT_DEFAULT_EXTENSION)) {
            return fileName.substring(0, fileName.length() - ModuleFileType.DOT_DEFAULT_EXTENSION.length());
        }
        return fileName;
    }
    private void convertModules() throws IOException {
      for (ModuleFile module : myModules) {
        final Element root = module.myDocument.getRootElement();
        RailsModule_ConvertingUtil.convertRootElement(root, module.myModuleName, myContext);
        JDOMUtil.writeDocument(module.myDocument, module.myModuleFile, SystemProperties.getLineSeparator());
      }
    }

    private void convertWorkspaceFile() throws IOException, QualifiedJDomException {
        if (myWorkspaceFile.exists()) {
            final Document document = JDomConvertingUtil.loadDocument(myWorkspaceFile);
            RailsModule_RunConfigurationConvertingUtil.convertWorkspace(document.getRootElement(), myContext);
            JDOMUtil.writeDocument(document, myWorkspaceFile, SystemProperties.getLineSeparator());
        }
    }

    private static class ModuleFile {
        private Document myDocument;
        private String myModuleName;
        private File myModuleFile;

        public ModuleFile(final Document document, final String moduleName, final File moduleFile) {
            myDocument = document;
            myModuleName = moduleName;
            myModuleFile = moduleFile;
        }
    }
}
