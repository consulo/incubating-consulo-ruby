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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.structureView.impl;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.impl.StructureViewWrapperImpl;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.ProjectViewImpl;
import com.intellij.ide.structureView.*;
import com.intellij.ide.structureView.impl.StructureViewState;
import com.intellij.ide.structureView.impl.jsp.StructureViewComposite;
import com.intellij.ide.structureView.impl.xml.XmlStructureViewTreeModel;
import com.intellij.ide.structureView.newStructureView.StructureViewComponent;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Ref;
import com.intellij.peer.PeerFactory;
import com.intellij.psi.*;
import com.intellij.util.Alarm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileViewProvider;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLFile;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.lang.structure.RubyStructureViewModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 21.05.2007
 */
//TODO Refactor with JSP Structure View Builder
public class RHTMLStructureViewBuilder  implements StructureViewBuilder {
    private final RHTMLFile myRHTMLFile;
    private PsiTreeChangeAdapter myPsiTreeChangeAdapter;
    private Language myBaseLanguage;
    private StructureViewComposite.StructureViewDescriptor myBaseStructureViewDescriptor;
    private FileEditor myFileEditor;
    private int myMyBaseLanguageViewDescriptorIndex;
    private StructureViewComposite myStructureViewComposite;
    public Project myProject;

    public RHTMLStructureViewBuilder(final PsiFile psiFile) {
        myRHTMLFile = RHTMLPsiUtil.getRHTMLFileRoot(psiFile);
        assert myRHTMLFile != null;

        myProject = myRHTMLFile.getProject();
        installBaseLanguageListener();
    }

    private void installBaseLanguageListener() {
      myPsiTreeChangeAdapter = new PsiTreeChangeAdapter() {
        final Alarm myAlarm = new Alarm();
        public void childAdded(PsiTreeChangeEvent event) {
          childrenChanged(event);
        }

        public void childRemoved(PsiTreeChangeEvent event) {
          childrenChanged(event);
        }

        public void childReplaced(PsiTreeChangeEvent event) {
          childrenChanged(event);
        }

        public void childMoved(PsiTreeChangeEvent event) {
          childrenChanged(event);
        }

        public void childrenChanged(PsiTreeChangeEvent event) {
          myAlarm.cancelAllRequests();
          myAlarm.addRequest(new Runnable(){
            public void run() {
              if (myBaseStructureViewDescriptor != null && ((StructureViewComponent)myBaseStructureViewDescriptor.structureView).getTree() == null) return;
              if (!myRHTMLFile.isValid()) return;
              ApplicationManager.getApplication().runReadAction(new Runnable(){
                public void run() {
                  Language baseLanguage = myRHTMLFile.getViewProvider().getTemplateDataLanguage();
                  if (baseLanguage == myBaseLanguage) {
                    updateBaseLanguageView();
                  }
                  else {
                    myBaseLanguage = baseLanguage;
                    StructureViewWrapper structureViewWrapper = StructureViewFactoryEx.getInstance(myProject).getStructureViewWrapper();
                    ((StructureViewWrapperImpl)structureViewWrapper).rebuild();
                    ((ProjectViewImpl)ProjectView.getInstance(myProject)).rebuildStructureViewPane();
                  }
                }
              });
            }
          }, 300, ModalityState.NON_MODAL);
        }
      };
      myBaseLanguage = myRHTMLFile.getViewProvider().getTemplateDataLanguage();
      myRHTMLFile.getManager().addPsiTreeChangeListener(myPsiTreeChangeAdapter);
    }

    private void updateBaseLanguageView() {
        if (myBaseStructureViewDescriptor == null || !myProject.isOpen()) {
            return;
        }
        final StructureViewComponent view = (StructureViewComponent)myBaseStructureViewDescriptor.structureView;
        if (view.isDisposed())  {
            return;
        }

        final StructureViewState state = view.getState();
        final Object[] expandedElements = state.getExpandedElements();
        List<PsiAnchor> expanded = new ArrayList<PsiAnchor>(expandedElements == null ? 0 : expandedElements.length);
        if (expandedElements != null) {
            for (Object element : expandedElements) {
                if (element instanceof PsiElement) {
                    expanded.add(new PsiAnchor((PsiElement)element));
                }
            }
        }
        Object[] selectedElements = state.getSelectedElements();
        List<PsiAnchor> selected = new ArrayList<PsiAnchor>(selectedElements == null ? 0 : selectedElements.length);
        if (selectedElements != null) {
            for (Object element : selectedElements) {
                if (element instanceof PsiElement && ((PsiElement)element).isValid()) {
                    selected.add(new PsiAnchor((PsiElement)element));
                }
            }
        }
        resetFileContents();

        if (view.isDisposed()) return;

        for (PsiAnchor pointer : expanded) {
            PsiElement element = pointer.retrieve();
            if (element != null) {
                view.expandPathToElement(element);
            }
        }
        for (PsiAnchor pointer : selected) {
            PsiElement element = pointer.retrieve();
            if (element != null) {
                view.addSelectionPathTo(element);
            }
        }
    }

    private void removeBaseLanguageListener() {
        myRHTMLFile.getManager().removePsiTreeChangeListener(myPsiTreeChangeAdapter);
    }

    @NotNull
    public StructureView createStructureView(FileEditor fileEditor, Project project) {
        myFileEditor = fileEditor;
        final List<StructureViewComposite.StructureViewDescriptor> descriptors = new ArrayList<StructureViewComposite.StructureViewDescriptor>();

//RHTML Structure view tab
        final Icon rhtmlIcon = myRHTMLFile.getFileType().getIcon();
        final String rhtmlTitle = RBundle.message("tab.structurevew.rhtml.view");
        final StructureView rhtmlView = new TreeBasedStructureViewBuilder() {
            @NotNull
            public StructureViewModel createStructureViewModel() {
                return new XmlStructureViewTreeModel(myRHTMLFile);
            }
        }.createStructureView(fileEditor, project);

        final StructureViewComposite.StructureViewDescriptor rhtmlViewDescriptor =
                new StructureViewComposite.StructureViewDescriptor(rhtmlTitle, rhtmlView, rhtmlIcon);
        descriptors.add(rhtmlViewDescriptor);

        StructureViewComposite.StructureViewDescriptor descriptor = resetFileContents();
        if (descriptor != null) {
            descriptors.add(descriptor);
            myMyBaseLanguageViewDescriptorIndex = descriptors.size() - 1;
        } else {
            myMyBaseLanguageViewDescriptorIndex = -1;
        }

//Ruby structure view tab
        final Icon rubyIcon = RubyFileType.RUBY.getIcon();
        final String rubyTitle = RBundle.message("tab.structurevew.ruby.view");
        final StructureView rubyView = new TreeBasedStructureViewBuilder() {
            @NotNull
            public StructureViewModel createStructureViewModel() {
                return new RubyStructureViewModel(RHTMLPsiUtil.getRubyFileRoot(myRHTMLFile));
            }
        }.createStructureView(fileEditor, project);

        final StructureViewComposite.StructureViewDescriptor rubyViewDescriptor =
                new StructureViewComposite.StructureViewDescriptor(rubyTitle, rubyView, rubyIcon);
        descriptors.add(rubyViewDescriptor);

//Composite view
        StructureViewComposite.StructureViewDescriptor[] array = descriptors.toArray(new StructureViewComposite.StructureViewDescriptor[descriptors.size()]);
        myStructureViewComposite = new StructureViewComposite(array) {
            public void dispose() {
                removeBaseLanguageListener();
                super.dispose();
            }
        };
        return myStructureViewComposite;
    }

    private StructureViewComposite.StructureViewDescriptor createBaseLanguageStructureView(final FileEditor fileEditor, final Language baseLanguage) {
        final Icon icon = findFileType(baseLanguage).getIcon();

        final RHTMLFileViewProvider viewProvider = myRHTMLFile.getViewProvider();
        final PsiFile templateLanguagePsiFile = viewProvider.getPsi(viewProvider.getTemplateDataLanguage());
        if (templateLanguagePsiFile == null) {
            return null;
        }

        final TreeBasedStructureViewBuilder baseViewBuilder =
                (TreeBasedStructureViewBuilder)baseLanguage.getStructureViewBuilder(templateLanguagePsiFile);
        if (baseViewBuilder == null) {
            return null;
        }

        final StructureViewModel modelWrapper =
                new RHTMLStructureViewModelWrapper(baseViewBuilder.createStructureViewModel(), myRHTMLFile);
        final StructureView structureView =
                PeerFactory.getInstance().getStructureViewFactory().createStructureView(fileEditor, modelWrapper, myProject);
        //noinspection UnresolvedPropertyKey
        return new StructureViewComposite.StructureViewDescriptor(IdeBundle.message("tab.structureview.baselanguage.view", baseLanguage.getID()),
                                                                  structureView, icon);
    }

    private StructureViewComposite.StructureViewDescriptor resetFileContents() {
        final Ref<StructureViewComposite.StructureViewDescriptor> ret = new Ref<StructureViewComposite.StructureViewDescriptor>();
        new WriteCommandAction(myProject) {
            protected void run(Result result) throws Throwable {
                if (myBaseStructureViewDescriptor != null) {
                    Disposer.dispose(myBaseStructureViewDescriptor.structureView);
                }

                final Language baseLanguage = myRHTMLFile.getViewProvider().getTemplateDataLanguage();
                StructureViewComposite.StructureViewDescriptor descriptor = createBaseLanguageStructureView(myFileEditor, baseLanguage);
                myBaseStructureViewDescriptor = descriptor;
                if (myStructureViewComposite != null) {
                    myStructureViewComposite.setStructureView(myMyBaseLanguageViewDescriptorIndex, myBaseStructureViewDescriptor);
                }
                ret.set(descriptor);
            }
        }.execute();
        return ret.get();
    }

    @NotNull
    private static FileType findFileType(final Language language) {
        FileType[] registeredFileTypes = FileTypeManager.getInstance().getRegisteredFileTypes();
        for (FileType fileType : registeredFileTypes) {
            if (fileType instanceof LanguageFileType && ((LanguageFileType)fileType).getLanguage() == language) {
                return fileType;
            }
        }
        return StdFileTypes.UNKNOWN;
    }
}
