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

package org.jetbrains.plugins.ruby.ruby.lang;

import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.lang.*;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.parameterInfo.ParameterInfoHandler;
import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.paramInfo.RubyParameterInfoHandler;
import org.jetbrains.plugins.ruby.ruby.lang.annotator.RubyFastAnnotator;
import org.jetbrains.plugins.ruby.ruby.lang.annotator.RubySlowAnnotator;
import org.jetbrains.plugins.ruby.ruby.lang.braceMatcher.RubyPairedBraceMatcher;
import org.jetbrains.plugins.ruby.ruby.lang.commenter.RubyCommenter;
import org.jetbrains.plugins.ruby.ruby.lang.documentation.RubyDocumentationProvider;
import org.jetbrains.plugins.ruby.ruby.lang.findUsages.RubyFindUsagesProvider;
import org.jetbrains.plugins.ruby.ruby.lang.folding.RubyFoldingBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.RubyFormattingModelBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.highlighter.RubySyntaxHighlighter;
import org.jetbrains.plugins.ruby.ruby.lang.namesValidator.RubyNamesValidator;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyParserDefinition;
import org.jetbrains.plugins.ruby.ruby.lang.structure.RubyStructureViewBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.surround.RubySurroundDescriptor;
import rb.implement.ImplementHandler;
import rb.override.OverrideHandler;


public class RubyLanguage extends Language {
    public static final RubyLanguage RUBY = new RubyLanguage();

    private static final String RB_MIME = "application/x-ruby";

    private RefactoringSupportProvider rubyRefactoringSupportProvider;
    private OverrideHandler rubyOverrideMethodsHandler;
    private ImplementHandler rubyImplementMethodsHandler;

    protected NotNullLazyValue<ParserDefinition> myParserDefinition;
    protected NotNullLazyValue<PairedBraceMatcher> myPairedBraceMatcher;
    protected NotNullLazyValue<SyntaxHighlighter> mySyntaxHighlighter;
    protected NotNullLazyValue<Commenter> myCommenter;
    protected NotNullLazyValue<FoldingBuilder> myFoldingBuilder;
    protected NotNullLazyValue<FormattingModelBuilder> myFormattingModelBuilder;
    protected NotNullLazyValue<FindUsagesProvider> myFindUsagesProvider;
    protected NotNullLazyValue<DocumentationProvider> myRubyDocumentationProvider;
    protected NotNullLazyValue<ParameterInfoHandler[]> myParameterInfoHandlers;
    protected NotNullLazyValue<SurroundDescriptor[]> mySurroundWithDescriptor;
    protected NotNullLazyValue<Annotator> myFastAnnotator;
    protected NotNullLazyValue<ExternalAnnotator> mySlowAnnotator;
    protected NotNullLazyValue<NamesValidator> myNamesValidator;

    private RubyLanguage() {
        super("Ruby", RB_MIME);
        myParserDefinition = new NotNullLazyValue<ParserDefinition>(){
            @NotNull
            protected ParserDefinition compute() {
                return new RubyParserDefinition();
            }
        };
        myPairedBraceMatcher = new NotNullLazyValue<PairedBraceMatcher>(){
            @NotNull
            protected PairedBraceMatcher compute() {
                return new RubyPairedBraceMatcher();
            }
        };
        mySyntaxHighlighter = new NotNullLazyValue<SyntaxHighlighter>(){
            @NotNull
            protected SyntaxHighlighter compute() {
                return new RubySyntaxHighlighter();
            }
        };
        myCommenter = new NotNullLazyValue<Commenter>(){
            @NotNull
            protected Commenter compute() {
                return new RubyCommenter();
            }
        };
        myFoldingBuilder = new NotNullLazyValue<FoldingBuilder>(){
            @NotNull
            protected FoldingBuilder compute() {
                return new RubyFoldingBuilder();
            }
        };
        myFormattingModelBuilder = new NotNullLazyValue<FormattingModelBuilder>(){
            @NotNull
            protected FormattingModelBuilder compute() {
                return new RubyFormattingModelBuilder();
            }
        };
        myFindUsagesProvider = new NotNullLazyValue<FindUsagesProvider>(){
            @NotNull
            protected FindUsagesProvider compute() {
                return new RubyFindUsagesProvider();
            }
        };
        myRubyDocumentationProvider = new NotNullLazyValue<DocumentationProvider>(){
            @NotNull
            protected DocumentationProvider compute() {
                return new RubyDocumentationProvider();
            }
        };
        myParameterInfoHandlers = new NotNullLazyValue<ParameterInfoHandler[]>(){
            @NotNull
            protected ParameterInfoHandler[] compute() {
                return new ParameterInfoHandler[]{new RubyParameterInfoHandler()};
            }
        };
        mySurroundWithDescriptor = new NotNullLazyValue<SurroundDescriptor[]>(){
            @NotNull
            protected SurroundDescriptor[] compute() {
                return new SurroundDescriptor[]{new RubySurroundDescriptor()};
            }
        };
        myFastAnnotator = new NotNullLazyValue<Annotator>(){
            @NotNull
            protected Annotator compute() {
                return new RubyFastAnnotator();
            }
        };
        mySlowAnnotator = new NotNullLazyValue<ExternalAnnotator>(){
            @NotNull
            protected ExternalAnnotator compute() {
                return new RubySlowAnnotator();
            }
        };
        myNamesValidator = new NotNullLazyValue<NamesValidator>(){
            @NotNull
            protected NamesValidator compute() {
                return new RubyNamesValidator();
            }
        };
    }

    @NotNull
    public ParserDefinition getParserDefinition() {
        return myParserDefinition.getValue();
    }

    @NotNull
    public PairedBraceMatcher getPairedBraceMatcher() {
        return myPairedBraceMatcher.getValue();
    }

    @NotNull
    public SyntaxHighlighter getSyntaxHighlighter(Project project, final VirtualFile virtualFile) {
        return mySyntaxHighlighter.getValue();
    }

    @NotNull
    public Commenter getCommenter() {
        return myCommenter.getValue();
    }

    public FoldingBuilder getFoldingBuilder() {
        return myFoldingBuilder.getValue();
    }

    @NotNull
    public StructureViewBuilder getStructureViewBuilder(@NotNull final PsiFile psiFile) {
        return new RubyStructureViewBuilder(psiFile);
    }

   @Nullable
   public FormattingModelBuilder getFormattingModelBuilder() {
       return myFormattingModelBuilder.getValue();
    }

    @NotNull
    public FindUsagesProvider getFindUsagesProvider() {
        return myFindUsagesProvider.getValue();
    }

    @Nullable
    public DocumentationProvider getDocumentationProvider() {
        return myRubyDocumentationProvider.getValue();
    }

    @Nullable
    public ParameterInfoHandler[] getParameterInfoHandlers() {
        return myParameterInfoHandlers.getValue();
    }

    @NotNull
    public SurroundDescriptor[] getSurroundDescriptors() {
        return mySurroundWithDescriptor.getValue();
    }

    public Annotator getAnnotator(){
        return myFastAnnotator.getValue();
    }

    @Nullable
    public ExternalAnnotator getExternalAnnotator() {
        return mySlowAnnotator.getValue();
    }

    @NotNull
    public NamesValidator getNamesValidator() {
        return myNamesValidator.getValue();
    }

    @NotNull
    public RefactoringSupportProvider getRefactoringSupportProvider() {
        return rubyRefactoringSupportProvider;
    }

    /**
     * Method to set JRuby refactoringSupportProvider
     * @param provider refactoring support provider
     */
    public void setRubyRefactoringSupportProvider(@NotNull final RefactoringSupportProvider provider) {
        rubyRefactoringSupportProvider = provider;
    }

    @Nullable
    public LanguageCodeInsightActionHandler getOverrideMethodsHandler() {
        return rubyOverrideMethodsHandler;
    }

    /**
     * Method to set JRuby handler
     * @param handler JRuby override handler
     */
    public void setRubyOverrideMethodsHandler(@NotNull final OverrideHandler handler) {
        rubyOverrideMethodsHandler = handler;
    }

    @Nullable
    public LanguageCodeInsightActionHandler getImplementMethodsHandler() {
        return rubyImplementMethodsHandler;
    }

    /**
     * Method to set JRuby handler
     * @param handler JRuby implement handler
     */
    public void setRubyImplementMethodsHandler(@NotNull final ImplementHandler handler) {
        rubyImplementMethodsHandler = handler;
    }
}

