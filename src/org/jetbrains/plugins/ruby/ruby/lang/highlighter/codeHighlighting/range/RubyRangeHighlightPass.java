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

package org.jetbrains.plugins.ruby.ruby.lang.highlighter.codeHighlighting.range;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.HighlightPassConstants;
import org.jetbrains.plugins.ruby.ruby.lang.highlighter.RubyHighlightInfoType;
import org.jetbrains.plugins.ruby.ruby.lang.highlighter.codeHighlighting.AbstractRubyHighlighterPass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.AccessModifiersUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.references.RReferenceNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubySystemCallVisitor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 25, 2007
 */
public class RubyRangeHighlightPass extends AbstractRubyHighlighterPass {
    private Collection<HighlightInfo> myRangeHighlighters = Collections.emptyList();

    public RubyRangeHighlightPass(@NotNull final Project project,
                  @NotNull final RFile psiFile,
                  @NotNull final Editor editor) {
        super(project, psiFile, editor, true, HighlightPassConstants.RUBY_HIGHLIGHTER_RANGE_GROUP);
    }

    @Override
	public void doCollectInformation(final ProgressIndicator progress) {
        ApplicationManager.getApplication().assertReadAccessAllowed();
        myRangeHighlighters = collectRangeInfos();
    }

    @Override
	public void doApplyInformationToEditor() {
        RubyRangeHighlightingUtil.setHighlightInfosToEditor(myProject, myDocument, myRangeHighlighters);
    }

    private Collection<HighlightInfo> collectRangeInfos() {
        final HashSet<HighlightInfo> highlightInfos = new HashSet<HighlightInfo>();
        final RangeHighlightingVisitor myVisitor = new RangeHighlightingVisitor(highlightInfos);
        for (PsiElement element : getElementsInRange()) {
            // Hope it`s often enough
            ProgressManager.getInstance().checkCanceled();
            element.accept(myVisitor);
        }
        return highlightInfos;
    }

    class RangeHighlightingVisitor extends RubySystemCallVisitor {
        private Collection<HighlightInfo> myCollection;

        public RangeHighlightingVisitor(final Collection<HighlightInfo> collection){
            myCollection = collection;
        }

        @Override
		public void visitRConstant(RConstant rConstant) {
            if (rConstant.isRealConstant()){
                myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_CONSTANT_DEF_HIGHLIGHT, rConstant, null));
            }
        }

        @Override
		public void visitRIdentifier(RIdentifier rIdentifier){
            if (rIdentifier.isParameter() || rIdentifier.isLocalVariable()){
                myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_LOCAL_VARIABLE_HIGHTLIGHT, rIdentifier, null));
                return;
            }
// If its not a command
            if (RReferenceNavigator.getReferenceByRightPart(rIdentifier)==null) {
                final String text = rIdentifier.getText();
                AccessModifier mod = AccessModifiersUtil.getModifierByName(text);
                if (mod == AccessModifier.PUBLIC) {
                    myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_PUBLIC_CALL_HIGHLIGHT, rIdentifier, null));
                }
                if (mod == AccessModifier.PRIVATE) {
                    myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_PRIVATE_CALL_HIGHLIGHT, rIdentifier, null));
                }
                if (mod == AccessModifier.PROTECTED) {
                    myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_PROTECTED_CALL_HIGHLIGHT, rIdentifier, null));
                }
// commands
                if (RCall.ATTR_ACCESSOR_COMMAND.equals(text)){
                    myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_ATTR_ACCESSOR_CALL_HIGHLIGHT, rIdentifier, null));
                }
                if (RCall.ATTR_READER_COMMAND.equals(text)){
                    myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_ATTR_READER_CALL_HIGHLIGHT, rIdentifier, null));
                }
                if (RCall.ATTR_WRITER_COMMAND.equals(text)){
                    myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_ATTR_WRITER_CALL_HIGHLIGHT, rIdentifier, null));
                }

                if (RCall.EXTEND_COMMAND.equals(text) || RCall.INCLUDE_COMMAND.equals(text)){
                    myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_INCLUDE_OR_EXTEND_CALL_HIGHLIGHT, rIdentifier, null));
                }

                final RFile rFile = RubyPsiUtil.getRFile(rIdentifier);
                assert rFile!=null;
                if (rFile.isJRubyEnabled() &&
                        (RCall.IMPORT_COMMAND.equals(text) ||
                                RCall.INCLUDE_CLASS_COMMAND.equals(text) ||
                                RCall.INCLUDE_PACKAGE_COMMAND.equals(text))){
                    myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.JRUBY_INCLUDE_JAVA_HIGHTLIGHT, rIdentifier, null));
                }

                if (RCall.LOAD_COMMAND.equals(text) || RCall.REQUIRE_COMMAND.equals(text)){
                    myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_REQUIRE_OR_LOAD_CALL_HIGHLIGHT, rIdentifier, null));
                }

                if (RCall.GEM_COMMAND.equals(text) || RCall.REQUIRE_GEM_COMMAND.equals(text)){
                    myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_REQUIRE_OR_LOAD_CALL_HIGHLIGHT, rIdentifier, null));
                }
            }
        }

        @Override
		public void visitRequireCall(@NotNull RCall rCall) {
            visitRequireOrLoad(rCall);
        }

        @Override
		public void visitLoadCall(@NotNull RCall rCall) {
            visitRequireOrLoad(rCall);
        }

        private void visitRequireOrLoad(@NotNull RCall rCall) {
            myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_REQUIRE_OR_LOAD_CALL_HIGHLIGHT, rCall.getPsiCommand(), null));
            for (RPsiElement arg : rCall.getArguments()) {
                myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_REQUIRE_OR_LOAD_CALL_ARG_HIGHLIGHT, arg, null));
            }
        }

        @Override
		public void visitIncludeCall(@NotNull RCall rCall) {
            visitIncludeOrExtend(rCall);
        }

        @Override
		public void visitExtendCall(@NotNull RCall rCall) {
            visitIncludeOrExtend(rCall);
        }

        private void visitIncludeOrExtend(RCall rCall) {
            myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_INCLUDE_OR_EXTEND_CALL_HIGHLIGHT, rCall.getPsiCommand(), null));
        }

        @Override
		public void visitAttrAccessorCall(@NotNull RCall rCall) {
            myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_ATTR_ACCESSOR_CALL_HIGHLIGHT, rCall.getPsiCommand(), null));
        }

        @Override
		public void visitAttrWriterCall(@NotNull RCall rCall) {
            myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_ATTR_WRITER_CALL_HIGHLIGHT, rCall.getPsiCommand(), null));
        }

        @Override
		public void visitAttrReaderCall(@NotNull RCall rCall) {
            myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_ATTR_READER_CALL_HIGHLIGHT, rCall.getPsiCommand(), null));
        }

        @Override
		public void visitAttrInternalCall(@NotNull RCall rCall) {
            myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_RAILS_ATTR_CALL_HIGHLIGHT, rCall.getPsiCommand(), null));
        }

        @Override
		public void visitCAttrAccessorCall(@NotNull final RCall rCall) {
            myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_RAILS_ATTR_CALL_HIGHLIGHT, rCall.getPsiCommand(), null));
        }

        @Override
		public void visitPublicCall(@NotNull RCall rCall) {
            myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_PUBLIC_CALL_HIGHLIGHT, rCall.getPsiCommand(), null));
        }

        @Override
		public void visitProtectedCall(@NotNull RCall rCall) {
            myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_PROTECTED_CALL_HIGHLIGHT, rCall.getPsiCommand(), null));
        }

        @Override
		public void visitPrivateCall(@NotNull RCall rCall) {
            myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_PRIVATE_CALL_HIGHLIGHT, rCall.getPsiCommand(), null));
        }

        @Override
		public void visitRSymbol(@NotNull RSymbol rSymbol) {
            myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_SYMBOL_HIGHLIGHT, rSymbol, null));
        }

        @Override
		public void visitImportClassCall(@NotNull final RCall rCall) {
            final RFile rFile = RubyPsiUtil.getRFile(rCall);
            assert rFile!=null;
            if (rFile.isJRubyEnabled()) {
                myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.JRUBY_INCLUDE_JAVA_HIGHTLIGHT, rCall.getPsiCommand(), null));
            }
        }

        @Override
		public void visitIncludeClassCall(@NotNull RCall rCall) {
            final RFile rFile = RubyPsiUtil.getRFile(rCall);
            assert rFile!=null;
            if (rFile.isJRubyEnabled()) {
                myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.JRUBY_INCLUDE_JAVA_HIGHTLIGHT, rCall.getPsiCommand(), null));
            }
        }

        @Override
		public void visitIncludePackageCall(@NotNull RCall rCall) {
            final RFile rFile = RubyPsiUtil.getRFile(rCall);
            assert rFile!=null;
            if (rFile.isJRubyEnabled()) {
                myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.JRUBY_INCLUDE_JAVA_HIGHTLIGHT, rCall.getPsiCommand(), null));
            }
        }

        @Override
		public void visitGemCall(@NotNull RCall rCall) {
            myCollection.add(HighlightInfo.createHighlightInfo(RubyHighlightInfoType.RUBY_REQUIRE_GEM_CALL_HIGHLIGHT, rCall.getPsiCommand(), null));
        }
    }
}
