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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RClassObject;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RMethodName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpressionInParens;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RColonReference;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RDotReference;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 3, 2007
 */
public class RNameUtil {
    private static final Pattern PATTERN = Pattern.compile(" |\\n");

    @NotNull
    public static List<PsiElement> getPsiPathRec(@Nullable final PsiElement element){
        ArrayList<PsiElement> list = new ArrayList<PsiElement>();
        if (element==null){
            return list;
        }

        final PsiElement firstChild = element.getFirstChild();
        if (element instanceof RMethodName){
            list.addAll(getPsiPathRec(firstChild));
            final PsiElement lastChild = element.getLastChild();
            if (firstChild!=lastChild){
                list.addAll(getPsiPathRec(lastChild));
            }
            return list;
        }
        if (element instanceof RName){
            return getPsiPathRec(firstChild);
        }
        if (element instanceof RClassObject){
            return getPsiPathRec(((RClassObject) element).getExpression());
        }

        if (element instanceof RExpressionInParens){
            return getPsiPathRec(((RExpressionInParens) element).getExpression());
        }

        if (element instanceof RColonReference || element instanceof RDotReference){
            RReference ref = (RReference) element;
            list.addAll(getPsiPathRec(ref.getReciever()));
            list.addAll(getPsiPathRec(ref.getValue()));
            return list;
        }

        list.add(element);
        return list;
    }

    @NotNull
    public static List<String> getPath(@Nullable final PsiElement element){
        ArrayList<String> list = new ArrayList<String>();
        for (PsiElement psiElement : getPsiPathRec(element)) {
            list.add(psiElement.getText());
        }
        return list;
    }


    @NotNull
    public static String getName(@NotNull final List<String> fullPath) {
        return fullPath.size()>0 ? fullPath.get(fullPath.size()-1) : "";
    }

    @NotNull
    public static String getPresentableName(@NotNull final String text) {
        return PATTERN.matcher(text).replaceAll("");
    }

    public static boolean isGlobal(@Nullable final PsiElement element){
        if (element instanceof RName){
            return isGlobal(element.getFirstChild());
        }

        if (element instanceof RClassObject){
            return isGlobal(((RClassObject) element).getExpression());
        }

        if (element instanceof RExpressionInParens){
            return isGlobal(((RExpressionInParens) element).getExpression());
        }

        //noinspection SimplifiableIfStatement
        if (element instanceof RReference){
            return ((RReference) element).getReciever()==null;
        }
        return false;
    }

}
