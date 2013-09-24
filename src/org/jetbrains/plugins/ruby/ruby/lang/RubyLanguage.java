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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.paramInfo.RubyParameterInfoHandler;
import org.jetbrains.plugins.ruby.ruby.lang.annotator.RubyFastAnnotator;
import org.jetbrains.plugins.ruby.ruby.lang.braceMatcher.RubyPairedBraceMatcher;
import org.jetbrains.plugins.ruby.ruby.lang.commenter.RubyCommenter;
import org.jetbrains.plugins.ruby.ruby.lang.documentation.RubyDocumentationProvider;
import org.jetbrains.plugins.ruby.ruby.lang.findUsages.RubyFindUsagesProvider;
import org.jetbrains.plugins.ruby.ruby.lang.folding.RubyFoldingBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.RubyFormattingModelBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.namesValidator.RubyNamesValidator;
import org.jetbrains.plugins.ruby.ruby.lang.structure.RubyStructureViewBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.surround.RubySurroundDescriptor;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.lang.Commenter;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageCodeInsightActionHandler;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.parameterInfo.ParameterInfoHandler;
import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.psi.PsiFile;
import rb.implement.ImplementHandler;
import rb.override.OverrideHandler;


public class RubyLanguage extends Language {
    public static final RubyLanguage RUBY = new RubyLanguage();


    private RefactoringSupportProvider rubyRefactoringSupportProvider;
    private OverrideHandler rubyOverrideMethodsHandler;
    private ImplementHandler rubyImplementMethodsHandler;


    protected NotNullLazyValue<PairedBraceMatcher> myPairedBraceMatcher;
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
        super("RUBY", "application/x-ruby");

        myPairedBraceMatcher = new NotNullLazyValue<PairedBraceMatcher>(){
            @NotNull
            protected PairedBraceMatcher compute() {
                return new RubyPairedBraceMatcher();
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
      /*  mySlowAnnotator = new NotNullLazyValue<ExternalAnnotator>(){
            @NotNull
            protected ExternalAnnotator compute() {
                return new RubySlowAnnotator();
            }
        }; */
        myNamesValidator = new NotNullLazyValue<NamesValidator>(){
            @NotNull
            protected NamesValidator compute() {
                return new RubyNamesValidator();
            }
        };
    }

    @NotNull
    public PairedBraceMatcher getPairedBraceMatcher() {
        return myPairedBraceMatcher.getValue();
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

