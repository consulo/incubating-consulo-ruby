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

package org.jetbrains.plugins.ruby.rails.actions.templates;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.velocity.runtime.parser.ParseException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import com.intellij.CommonBundle;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.fileTemplates.ui.CreateFromTemplatePanel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ex.IdeFocusTraversalPolicy;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaPackage;
import com.intellij.util.IncorrectOperationException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 6, 2007
 */
public class CreateFileFromTemplateDialog extends DialogWrapper {
    @NotNull
    private final PsiDirectory myDirectory;
    @NotNull
    private final Project myProject;
    private PsiElement myCreatedElement;
    private final CreateFromTemplatePanel myAttrPanel;
    private final JComponent myAttrComponent;
    @NotNull
    private final FileTemplate myTemplate;
    private Properties myDefaultProperties;

    public CreateFileFromTemplateDialog(@NotNull final Project project,
                                        @NotNull final PsiDirectory directory,
                                        @NotNull final FileTemplate template) {
        super(project, true);
        myDirectory = directory;
        myProject = project;
        myTemplate = template;
        setTitle(RBundle.message("title.new.from.template", template.getName()));

        PsiJavaPackage aPackage = null;//myDirectory.getPackage();
        String packageName = aPackage == null ? "" : aPackage.getQualifiedName();
        myDefaultProperties = FileTemplateManager.getInstance().getDefaultProperties();
        myDefaultProperties.setProperty(FileTemplate.ATTRIBUTE_PACKAGE_NAME, packageName);

        String[] unsetAttributes = null;
        try {
            unsetAttributes = myTemplate.getUnsetAttributes(myDefaultProperties);
        }
        catch (ParseException e) {
            showErrorDialog(e);
        }

       /* if (unsetAttributes != null) {
            myAttrPanel = new CreateFromTemplatePanel(unsetAttributes, true);
            myAttrComponent = myAttrPanel.getComponent();
            init();
        } else*/ {
            myAttrPanel = null;
            myAttrComponent = null;
        }
    }

    public PsiElement create() {
        if (myAttrPanel != null) {
            if (myAttrPanel.hasSomethingToAsk()) {
                show();
            } else {
                doCreate(null);
            }
        }
        return myCreatedElement;
    }

    protected void doOKAction() {
        String fileName = myAttrPanel.getFileName();
        if (fileName != null && fileName.length() == 0) {
            Messages.showErrorDialog(myAttrComponent, RBundle.message("error.please.enter.a.file.name"), CommonBundle.getErrorTitle());
            return;
        }
        doCreate(fileName);
        if (myCreatedElement != null) {
            super.doOKAction();
        }
    }

    private void doCreate(final String fileName) {
        try {
            myCreatedElement = createFromTemplate(myTemplate, fileName,
                                                  myAttrPanel.getProperties(myDefaultProperties),
                                                  myDirectory);
        }
        catch (Exception e) {
            showErrorDialog(e);
        }
    }

    private void showErrorDialog(final Exception e) {
        Messages.showErrorDialog(myProject, filterMessage(e.getMessage()), RBundle.message("title.cannot.create.file"));
    }

    @Nullable
    private String filterMessage(String message) {
        if (message == null) return null;
        @NonNls String ioExceptionPrefix = "java.io.IOException:";
        if (message.startsWith(ioExceptionPrefix)) {
            message = message.substring(ioExceptionPrefix.length());
        }
        return RBundle.message("error.unable.to.parse.template.message", myTemplate.getName(), message);
    }

    protected JComponent createCenterPanel() {
        myAttrPanel.ensureFitToScreen(200, 200);
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(myAttrComponent, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        return centerPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return IdeFocusTraversalPolicy.getPreferredFocusedComponent(myAttrComponent);
    }

    protected PsiElement createFromTemplate(@NotNull final FileTemplate template,
                                            @NonNls @Nullable final String fileName,
                                            @Nullable Properties props,
                                            @NotNull final PsiDirectory directory) throws Exception {
        @NotNull final Project project = directory.getProject();
        if (props == null) {
            props = FileTemplateManager.getInstance().getDefaultProperties();
        }
        FileTemplateManager.getInstance().addRecentName(template.getName());
       // FileTemplateUtil.setPackageNameAttribute(props, directory);

        if (fileName != null && props.getProperty(FileTemplate.ATTRIBUTE_NAME) == null) {
            props.setProperty(FileTemplate.ATTRIBUTE_NAME, fileName);
        }

        //Set escaped references to dummy values to remove leading "\" (if not already explicitely set)
        String[] dummyRefs = FileTemplateUtil.calculateAttributes(template.getText(), props, true);
        for (String dummyRef : dummyRefs) {
            props.setProperty(dummyRef, "");
        }
        String mergedText;

        mergedText = template.getText(props);

        final String templateText = StringUtil.convertLineSeparators(mergedText);

        final Ref<Exception> commandExceptionWrapper = new Ref<Exception>();
        final Ref<PsiElement> psiFileWrapper = new Ref<PsiElement>();

        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            public void run() {
                final Runnable run = new Runnable() {
                    public void run() {
                        try {
                            psiFileWrapper.set(createPsiFile(template, project, directory, templateText, fileName));
                        }
                        catch (Exception ex) {
                            commandExceptionWrapper.set(ex);
                        }
                    }
                };
                ApplicationManager.getApplication().runWriteAction(run);
            }
        }, RBundle.message("command.create.file.from.template"), null);
        if (commandExceptionWrapper.get() != null) {
            throw commandExceptionWrapper.get();
        }
        return psiFileWrapper.get();
    }

    protected PsiFile createPsiFile(final FileTemplate template, final Project project, final PsiDirectory directory, final String templateText, final String fileName) throws IncorrectOperationException {
        return createPsiFile(project, directory, templateText, fileName, template.getExtension());
    }

    public static PsiFile createPsiFile(final Project project,
                                        final @NotNull PsiDirectory directory,
                                        final String content,
                                        final String fileName,
                                        final String extension) throws IncorrectOperationException {
        final String suggestedFileNameEnd = "." + extension;

        final String realfileName = fileName.endsWith(suggestedFileNameEnd)
                ? fileName
                : fileName + suggestedFileNameEnd;

        directory.checkCreateFile(realfileName);

        final PsiFile file = PsiFileFactory.getInstance(project).createFileFromText(realfileName, content);
        return (PsiFile) directory.add(file);
    }
}
