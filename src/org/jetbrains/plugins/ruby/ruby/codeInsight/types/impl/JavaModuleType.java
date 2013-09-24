package org.jetbrains.plugins.ruby.ruby.codeInsight.types.impl;

import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.codeInsight.resolve.JavaResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.JavaSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Message;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author: oleg
 * @date: Aug 21, 2008
 */
public class JavaModuleType extends RSymbolTypeImpl{
    public JavaModuleType(@Nullable final FileSymbol fileSymbol,
                          @NotNull final Symbol symbol,
                          final Context context,
                          final boolean inReference) {
        super(fileSymbol, symbol, context, inReference);
    }

    @NotNull
    @Override
    public Collection<Message> getMessages() {
        final Collection<Message> messages = new ArrayList<Message>(super.getMessages());
        for (PsiElement element : JavaResolveUtil.getTopLevelPackagesAndClasses(getSymbol().getProject())) {
            if (element instanceof PsiClass){
                final String name = ((PsiClass) element).getName();
                if (name!=null){
                    messages.add(new MessageImpl(name, 0, true, new JavaSymbol(element, name, null, Type.JAVA_CLASS)));
                }
            }
            if (element instanceof PsiPackage){
                final String name = ((PsiPackage) element).getName();
                if (name!=null){
                    messages.add(new MessageImpl(name, 0, true, new JavaSymbol(element, name, null, Type.JAVA_PACKAGE)));
                }
            }
        }
        return messages;
    }

    @Override
    public Collection<Message> getMessagesForName(@Nullable final String name) {
        if (name == null){
            return getMessages();
        }
        final Collection<Message> messagesForName = new ArrayList<Message>(super.getMessagesForName(name));
        for (PsiElement element : JavaResolveUtil.getTopLevelPackagesAndClasses(getSymbol().getProject())) {
            if (element instanceof PsiClass){
                if (Comparing.equal(name, ((PsiClass) element).getName())){
                    messagesForName.add(new MessageImpl(name, 0, true, new JavaSymbol(element, name, null, Type.JAVA_CLASS)));
                }
            }
            if (element instanceof PsiPackage){
                if (Comparing.equal(name, ((PsiPackage) element).getName())){
                    messagesForName.add(new MessageImpl(name, 0, true, new JavaSymbol(element, name, null, Type.JAVA_CLASS)));
                }
            }
        }
        return messagesForName;
    }

    @Override
    public String toString() {
        return "Java module type: " + getSymbol();
    }
}
