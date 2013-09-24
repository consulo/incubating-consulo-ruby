package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.arguments;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RPredefinedArgument;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class RPredefinedArgumentImpl extends RPsiElementBase implements RPredefinedArgument {
    public RPredefinedArgumentImpl(ASTNode astNode) {
        super(astNode);
    }

    @NotNull
    public String getName(){
        final RIdentifier identifier = getIdentifier();
        //noinspection ConstantConditions
        return identifier!=null ? identifier.getName() : "";
    }

    public RIdentifier getIdentifier(){
        return RubyPsiUtil.getChildByType(this, RIdentifier.class, 0);
    }

    public ArgumentInfo.Type getType() {
        return ArgumentInfo.Type.PREDEFINED;
    }

    @Nullable
    public String getValueText() {
        final RPsiElement value = getValue();
        return value!=null ? value.getText() : null;
    }

    public RPsiElement getValue() {
        return getChildByType(RPsiElement.class, 1);
    }
}
