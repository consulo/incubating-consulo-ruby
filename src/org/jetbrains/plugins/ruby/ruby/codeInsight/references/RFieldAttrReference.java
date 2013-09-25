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

package org.jetbrains.plugins.ruby.ruby.codeInsight.references;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.RFieldResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.findUsages.RubyUsageType;
import org.jetbrains.plugins.ruby.ruby.lang.findUsages.RubyUsageTypeProvider;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RBaseString;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RFieldHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 12, 2007
 */
public class RFieldAttrReference implements RPsiPolyvariantReference {

    private final RCall myCall;
    private final TextRange myTextRange;
    private final PsiElement myElement;
    private final RFieldHolder myHolder;
    private final String myName;


    public RFieldAttrReference(@NotNull final RCall call,
                           @NotNull final PsiElement element,
                           @NotNull final RFieldHolder holder) {
        myCall = call;
        myHolder = holder;
        myElement = element;

        final int relativeStartOffset = element.getTextOffset() - myCall.getTextOffset();
        myTextRange = computeTextRange(element, relativeStartOffset, relativeStartOffset + element.getTextLength());
        myName = RubyPsiUtil.evaluate(myElement);
    }

    private TextRange computeTextRange(final PsiElement element, int start, int end) {
        if (element instanceof RBaseString){
            final PsiElement firstChild = element.getFirstChild();
            if (firstChild !=null){
                start+=firstChild.getTextLength();
            }

            final PsiElement lastChild = element.getLastChild();
            //noinspection ConstantConditions
            if (lastChild !=null && lastChild.getNode().getElementType() == RubyTokenTypes.tSTRING_END){
                end-=lastChild.getTextLength();
            }
            return new TextRange(start, end);
        }
        if (element instanceof RSymbol){
            final PsiElement firstChild = element.getFirstChild();
            final PsiElement lastChild = element.getLastChild();
            if (firstChild !=null){
                start+=firstChild.getTextLength();
            }
            if (lastChild instanceof RBaseString){
                return computeTextRange(element.getLastChild(), start, end);
            }
        }
        return new TextRange(start, end);
    }

    @Override
	public PsiElement getElement() {
        return myCall;
    }

    public PsiElement getReferenceContent(){
        return myElement;
    }

    @Override
	public TextRange getRangeInElement() {
        return myTextRange;
    }

    @Override
	@Nullable
    public PsiElement resolve() {
        return ResolveUtil.resolvePolyVarReference(this);
    }


    @Override
	public String getCanonicalText() {
        return myName;
    }

    @Override
	public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        if (!TextUtil.isCID(newName)){
            throw new IncorrectOperationException("Wrong name");
        }

        return rename(myElement, newName);
    }

    private PsiElement rename(final PsiElement element, @NotNull final String newName) throws IncorrectOperationException {
        if (element instanceof RBaseString){
            final List<PsiElement> myContent = ((RBaseString) element).getPsiContent();
            if (myContent.size()!=1){
                return null;
            }
            final String text = "%Q(" + newName + ")";
            final PsiElement newString = RubyPsiUtil.getTopLevelElements(myCall.getProject(), text).get(0);
            assert newString instanceof RBaseString;
            final List<PsiElement> newStringContent = ((RBaseString) newString).getPsiContent();
            assert newStringContent.size()==1;
            final PsiElement newContext = newStringContent.get(0);
            RubyPsiUtil.replaceInParent(myContent.get(0), newContext);
            return newContext;
        }
        if (element instanceof RSymbol){
            final PsiElement myObject = ((RSymbol) element).getObject();

            if (myObject instanceof RBaseString){
                return rename(myObject, newName);
            }
            final ASTNode myNode = myObject.getNode();

            final String text = ":" + newName;
            final PsiElement newSymbol = RubyPsiUtil.getTopLevelElements(myCall.getProject(), text).get(0);
            assert newSymbol instanceof RSymbol;
            final PsiElement newObject = ((RSymbol) newSymbol).getObject() ;
            final ASTNode newNode = newObject.getNode();

            //noinspection ConstantConditions
            element.getNode().replaceChild(myNode, newNode);
            return newObject;
        }
        return null;
    }

    @Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return null;
    }

    @Override
	public boolean isReferenceTo(PsiElement element) {
        return resolve() == element;
    }

    @Override
	public Object[] getVariants() {
        final FileSymbol fileSymbol = ((RPsiElementBase) myCall).forceFileSymbolUpdate();
        return RFieldResolveUtil.getVariants(fileSymbol, myHolder);
    }

    @Override
	public boolean isSoft() {
        return true;
    }

    @Override
	@NotNull
    public PsiElement getRefValue() {
        return myElement;
    }

    @Override
	@NotNull
    public List<Symbol> multiResolveToSymbols(@Nullable final FileSymbol fileSymbol) {
        throw new UnsupportedOperationException("multiResolveToSymbols is not implemented in org.jetbrains.plugins.ruby.ruby.codeInsight.references.RFieldAttrReference");
    }

    @Override
	@NotNull
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final FileSymbol fileSymbol = ((RPsiElementBase) myCall).forceFileSymbolUpdate();
        final ArrayList<ResolveResult> list = new ArrayList<ResolveResult>();
        for (final PsiElement element : RFieldResolveUtil.resolve(fileSymbol, myHolder, myName, Types.FIELDS)) {
            list.add(new ResolveResult(){
                    @Override
					@Nullable
                    public PsiElement getElement() {
                        RubyUsageTypeProvider.setType(RFieldAttrReference.this, RubyUsageType.UNCLASSIFIED);
                        return element;
                    }

                    @Override
					public boolean isValidResult() {
                        return true;
                    }
                });
        }
        return list.toArray(new ResolveResult[list.size()]);
    }
}
