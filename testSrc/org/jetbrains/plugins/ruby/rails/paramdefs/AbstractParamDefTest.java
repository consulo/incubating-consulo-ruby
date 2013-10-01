/*
 * Copyright 2000-2007 JetBrains s.r.o.
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

package org.jetbrains.plugins.ruby.rails.paramdefs;

import com.intellij.codeInsight.lookup.PresentableLookupValue;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.ModuleTestCase;
import com.intellij.testFramework.PsiTestUtil;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfiguration;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfigurationLowLevel;
import org.jetbrains.plugins.ruby.rails.facet.versions.BaseRailsFacetTestUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.*;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.RSymbolImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.RStringLiteralBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.RConstantImpl;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author yole
 */
public abstract class AbstractParamDefTest extends ModuleTestCase {
    private VirtualFile myRoot;

    protected Module createModule(final String path) {
        final Module module = super.createModule(path, RubyModuleType.getInstance());

        //rails for Ruby module
        final Ref<VirtualFile> virtualFileRef = new Ref<VirtualFile>();

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                try {
                    virtualFileRef.set(prepareTestProjectStructure("rails_app_home", module));
                } catch (Exception e) {
                    e.printStackTrace();

                    fail(e.getMessage());
                }
            }
        });


        BaseRailsFacetTestUtil.addRailsToModule(module, virtualFileRef.get().getPath());
        return module;
    }

    protected void setUp() throws Exception {
        super.setUp();
        TestUtil.loadJRubySupport();
        TestUtil.loadRailsSupport();

        // TODO test on both - rails and JRails Facets!
    }

    public void testRailsEnabled() {
        assertTrue(RailsFacetUtil.hasRailsSupport(getModule()));
    }

    private PsiReference prepareTest(String testPath, String testFilePath) throws Exception {
        final Module module = getModule();

        myRoot = prepareTestProjectStructure(testPath, module);

        final BaseRailsFacetConfiguration configuration = RailsFacetUtil.getRailsFacetConfiguration(module);
        assert configuration != null;

        ((BaseRailsFacetConfigurationLowLevel) configuration).setRailsApplicationRootPath(myRoot.getPath());
        PsiFile testFile = findPsiFile(testFilePath);
        int caretOffset = findMarkerOffset(testFile, "<caret>");
        return getParamDefReference(testFile, caretOffset);
    }

    private VirtualFile prepareTestProjectStructure(final String testPath, final Module module) throws Exception {
        final String path = PathUtil.getDataPath(getClass(), testPath);
        return PsiTestUtil.createTestProjectStructure(getProject(), module, path, myFilesToDelete);
    }

    private PsiReference getParamDefReference(final PsiFile testFile, final int caretOffset) {
        PsiElement element = testFile.findElementAt(caretOffset);
        PsiElement refOwner = PsiTreeUtil.getParentOfType(element, RSymbolImpl.class, RStringLiteralBase.class,
                RConstantImpl.class);
        ParamContext paramContext = refOwner == null ? null : ParamDefUtil.getParamContext((RPsiElement) refOwner);
        assertNotNull("No paramcontext found at cursor", paramContext);
        String calledMethod = paramContext.getCall().getPsiCommand().getText();
        ParamDef[] paramDefs = ParamDefManager.getInstance().getParamDefs(calledMethod);
        assertNotNull("No paramdefs found for method " + calledMethod, paramDefs);
        ParamDef paramDef = ParamDefUtil.findParamDefByIndex(paramContext, paramDefs);
        assertNotNull("Could not find paramdef by index with context " + paramContext, paramDef);
        return new ParamDefReference((RPsiElement) refOwner, paramDef, paramContext);
    }

    private PsiFile findPsiFile(final String testFilePath) {
        VirtualFile testVFile = myRoot.findFileByRelativePath(testFilePath);
        assertNotNull(testVFile);
        PsiFile testFile = getPsiManager().findFile(testVFile);
        assertNotNull(testFile);
        return testFile;
    }

    private int findMarkerOffset(final PsiFile testFile, String marker) {
        int caretOffset = -1;
        Document document = PsiDocumentManager.getInstance(myProject).getDocument(testFile);
        assertNotNull("couldn't find document for " + testFile.getName(), document);
        // start with line 1 - caret marker is below the cursor position
        for(int i=1; i<document.getLineCount(); i++) {
            int lineStart = document.getLineStartOffset(i);
            int lineEnd = document.getLineEndOffset(i);
            String line = document.getCharsSequence().subSequence(lineStart, lineEnd).toString();
            int caretInLinePos = line.indexOf(marker);
            if (caretInLinePos >= 0) {
                caretOffset = document.getLineStartOffset(i-1) + caretInLinePos;
                break;
            }
        }
        assertTrue("could not find " + marker, caretOffset >= 0);
        return caretOffset;
    }

    private PsiElement doTestResolve(String testPath, String testFilePath) throws Exception {
        PsiReference ref = prepareTest(testPath, testFilePath);
        return ref.resolve();
    }

    protected void doTestResolveToFile(String testPath, String testFilePath, String expectResolvedPath) throws Exception {
        PsiElement element = doTestResolve(testPath, testFilePath);
        assertNotNull("Resolve failed", element);
        VirtualFile expectFile = myRoot.findFileByRelativePath(expectResolvedPath);
        assertNotNull(expectFile);
        assertEquals(element, getPsiManager().findFile(expectFile));
    }

    protected void doTestResolveToTarget(String testPath, String testFilePath, String targetFilePath) throws Exception {
        PsiElement element = doTestResolve(testPath, testFilePath);
        assertNotNull("Resolve failed", element);
        PsiFile targetFile = findPsiFile(targetFilePath);
        int targetOffset = findMarkerOffset(targetFile, "<target>");
        PsiElement targetElement = targetFile.findElementAt(targetOffset);
        assertNotNull(targetElement);
        assertTrue(PsiTreeUtil.isAncestor(element, targetElement, false));
    }

    protected void doTestCompletion(String testPath, String testFilePath, String... expectVariants) throws Exception {
        PsiReference ref = prepareTest(testPath, testFilePath);
        Object[] objects = ref.getVariants();
        Set<String> actualSet = new TreeSet<String>();
        for(Object o: objects) {
            if (o instanceof PresentableLookupValue) {
                final PresentableLookupValue lookupValue = (PresentableLookupValue) o;
                actualSet.add(lookupValue.getPresentation());
            }
            else {
                actualSet.add(o.toString());
            }
        }
        Set<String> expectedSet = new TreeSet<String>();
        Collections.addAll(expectedSet, expectVariants);
        assertEquals(expectedSet, actualSet);
    }
}
