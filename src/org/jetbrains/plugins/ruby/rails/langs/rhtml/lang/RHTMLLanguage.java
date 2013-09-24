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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang;

import com.intellij.formatting.Block;
import com.intellij.formatting.FormattingDocumentModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.lang.*;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.lang.xml.XmlCommenter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.HtmlPolicy;
import com.intellij.psi.formatter.xml.ReadOnlyBlock;
import com.intellij.psi.formatter.xml.XmlBlock;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.xml.XmlPsiPolicy;
import com.intellij.psi.impl.source.xml.behavior.DefaultXmlPsiPolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileViewProvider;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.braceMatcher.RHTMLPairedBraceMatcher;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.RHTMLFormattingModelBuilder;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.highlighting.impl.RHTMLFileHighlighterImpl;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.parser.RHTMLPaserDefinition;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLFile;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLPsiUtil;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.structureView.impl.RHTMLStructureViewBuilder;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 02.04.2007
 */
public class RHTMLLanguage extends XMLLanguage {
    public static final RHTMLLanguage RHTML = new RHTMLLanguage();

    private static final String RB_MIME = "application/x-httpd-eruby";

    protected NotNullLazyValue<PairedBraceMatcher> myPairedBraceMatcher;
    protected NotNullLazyValue<ParserDefinition> myParserDefinition;
    protected NotNullLazyValue<FoldingBuilder> myFoldingBuilder;
    protected NotNullLazyValue<FormattingModelBuilder> myRHTMLFormattingModelBuilder;
    protected NotNullLazyValue<FindUsagesProvider> myFindUsagesProvider;
    protected NotNullLazyValue<Commenter> myCommenter;
    protected NotNullLazyValue<XmlPsiPolicy> myXmlPsiPolicy;

    public RHTMLLanguage() {
        super("RHTML", RB_MIME);

        myPairedBraceMatcher = new NotNullLazyValue<PairedBraceMatcher>(){
            @NotNull
            protected PairedBraceMatcher compute() {
                return new RHTMLPairedBraceMatcher();
            }
        };
        myParserDefinition = new NotNullLazyValue<ParserDefinition>(){
            @NotNull
            protected ParserDefinition compute() {
                return new RHTMLPaserDefinition();
            }
        };
        myFoldingBuilder = new NotNullLazyValue<FoldingBuilder>(){
            @NotNull
            protected FoldingBuilder compute() {
                return new RHTMLFoldingBuilder();
            }
        };

        myRHTMLFormattingModelBuilder = new NotNullLazyValue<FormattingModelBuilder>(){
            @NotNull
            protected FormattingModelBuilder compute() {
                return new RHTMLFormattingModelBuilder();
            }
        };
        myFindUsagesProvider = new NotNullLazyValue<FindUsagesProvider>(){
            @NotNull
            protected FindUsagesProvider compute() {
                return new RHTMLFindUsagesProvider();
            }
        };
        myCommenter = new NotNullLazyValue<Commenter>(){
            @NotNull
            protected Commenter compute() {
                return new XmlCommenter();
            }
        };
        myXmlPsiPolicy = new NotNullLazyValue<XmlPsiPolicy>(){
            @NotNull
            protected XmlPsiPolicy compute() {
                return new DefaultXmlPsiPolicy();
            }
        };
/*


                new FormattingModelBuilder() {
            @NotNull
            public FormattingModel createModel(final PsiElement element,
                                               final CodeStyleSettings settings) {


                final PsiFile psiFile = element.getContainingFile();
                final ASTNode node = element.getNode();

                ASTNode rootNode = node;

                // if element is RHTML_FILE then process it's html root
                // it's dummy holder with HTML_DOCUMENT 
                if (node != null && node.getElementType() == RHTMLElementTypeEx.RHTML_FILE) {
                    final RHTMLFile rhtmlFile = RHTMLPsiUtil.getRHTMLFileRoot(psiFile);
                    assert rhtmlFile != null;

                    final RHTMLFileViewProvider rhtmlViewProvider = rhtmlFile.getViewProvider();
                    final Language templateLang = rhtmlViewProvider.getTemplateDataLanguage();
                    if (templateLang == StdLanguages.HTML || templateLang == StdLanguages.XHTML) {
                        final PsiFile psiRoot = rhtmlViewProvider.getPsi(templateLang);
                        rootNode = SourceTreeToPsiMap.psiElementToTree(psiRoot);
                    }
//                    else {
//                        //return new ReadOnlyBlock(psiFile.getNode());
//                    }
                }

                //noinspection ConstantConditions
                return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(),
                        new RHTMLBlock(rootNode, Indent.getAbsoluteNoneIndent(), null, settings), settings);
            }
        };
*/
//         new FormattingModelBuilder() {
//            @NotNull
//            public FormattingModel createModel(final PsiElement element,
//                                               final CodeStyleSettings settings) {
//                final PsiFile psiFile = element.getContainingFile();
//                final ASTNode node = element.getNode();
//
//                if (node == null) {
//                    return null;
//                }
//                final IElementType elementType = node.getElementType();
//
//                if (element instanceof OuterLanguageElement ) {
//                    if (elementType == RHTMLElementType.RHTML_INJECTION_IN_HTML) {
//                        return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(),
//                                new RHTMLBlock(node, Indent.getAbsoluteNoneIndent(), null, settings), settings);
//                    } else if (elementType == RHTMLTokenType.RHTML_INJECTION_IN_HTML) {
//                        //TODO process ruby code!!!!!!
//                    }
//                } else if (elementType == XmlElementType.HTML_DOCUMENT) {
//                    // HTML_DOCUMENT
//                    final FormattingDocumentModelImpl documentModel = FormattingDocumentModelImpl.createOn(psiFile);
//                    final XmlBlock xmlBlock = new XmlBlock(node, null, null, new HtmlPolicy(settings, documentModel), null, null);
//                    return new PsiBasedFormattingModel(psiFile, xmlBlock, documentModel);
//                }
//                //for RHTML_FILE    - create block for HTML_DOCUMENT
//
//                final FormattingDocumentModelImpl documentModel = FormattingDocumentModelImpl.createOn(psiFile);
//                final Block rootBlock = createRHTMLRoot(psiFile, settings, documentModel);
//                return new PsiBasedFormattingModel(psiFile, rootBlock, documentModel);
//            }
//        };
    }

    public FileViewProvider createViewProvider(final VirtualFile file,
                                               final PsiManager manager,
                                               final boolean physical) {
        return new RHTMLFileViewProvider(manager, file, physical);
    }

    public FormattingModelBuilder getFormattingModelBuilder() {
        return myRHTMLFormattingModelBuilder.getValue();
    }

    @NotNull
    public FindUsagesProvider getFindUsagesProvider() {
        return myFindUsagesProvider.getValue();
    }

    @NotNull
    public ParserDefinition getParserDefinition() {
        return myParserDefinition.getValue();
    }

    public XmlPsiPolicy getPsiPolicy() {
        return myXmlPsiPolicy.getValue();
    }

    @NotNull
    public SyntaxHighlighter getSyntaxHighlighter(final Project project, final VirtualFile virtualFile) {
        return new RHTMLFileHighlighterImpl();
    }

    @NotNull
    public Commenter getCommenter() {
        return myCommenter.getValue();
    }

    public FoldingBuilder getFoldingBuilder() {
        return myFoldingBuilder.getValue();
    }

    //TODO  getAnnotator()

    @Nullable
    public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
        return new RHTMLStructureViewBuilder(psiFile);
    }

    @Nullable
    public PairedBraceMatcher getPairedBraceMatcher() {
        return myPairedBraceMatcher.getValue();
    }

    public static Block createRHTMLRoot(final PsiElement element,
                                        final CodeStyleSettings settings,
                                        final FormattingDocumentModel documentModel) {
        final PsiFile file = element.getContainingFile();
        final RHTMLFile rhtmlFile = RHTMLPsiUtil.getRHTMLFileRoot(file);
        assert rhtmlFile != null;

        final RHTMLFileViewProvider rhtmlViewProvider = rhtmlFile.getViewProvider();
        final Language templateLang = rhtmlViewProvider.getTemplateDataLanguage();


        if (templateLang == StdLanguages.HTML || templateLang == StdLanguages.XHTML) {
            final PsiFile psiRoot = rhtmlViewProvider.getPsi(templateLang);
            final ASTNode rootNode = SourceTreeToPsiMap.psiElementToTree(psiRoot);
            return new XmlBlock(rootNode, null, null, new HtmlPolicy(settings, documentModel), null, null);
        } else {
            return new ReadOnlyBlock(file.getNode());
        }
    }
//    public static Block createRHTMLRoot(final PsiElement element,
//                                        final CodeStyleSettings settings,
//                                        final FormattingDocumentModel documentModel) {
//        final PsiFile file = element.getContainingFile();
//        final RHTMLFile rhtmlFile = RHTMLPsiUtil.getRHTMLFileRoot(file);
//        assert rhtmlFile != null;
//
//        final RHTMLFileViewProvider rhtmlViewProvider = rhtmlFile.getViewProvider();
//        final Language templateLang = rhtmlViewProvider.getTemplateDataLanguage();
//
//        //TODO leave only HTML, not XML
//        if (templateLang == StdLanguages.HTML || templateLang == StdLanguages.XHTML) {
//            final PsiFile psiRoot = rhtmlViewProvider.getPsi(RHTMLLanguage.RHTML);
//            final ASTNode rootNode = SourceTreeToPsiMap.psiElementToTree(psiRoot);
//            return new XmlBlock(rootNode, null, null, new HtmlPolicy(settings, documentModel), null, null);
//        } else {
//            return new ReadOnlyBlock(file.getNode());
//        }
//    }

}

