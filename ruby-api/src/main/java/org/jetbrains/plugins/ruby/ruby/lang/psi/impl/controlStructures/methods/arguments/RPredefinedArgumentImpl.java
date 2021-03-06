package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.arguments;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RPredefinedArgument;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import com.intellij.lang.ASTNode;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class RPredefinedArgumentImpl extends RPsiElementBase implements RPredefinedArgument
{
	public RPredefinedArgumentImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	@Nonnull
	public String getName()
	{
		final RIdentifier identifier = getIdentifier();
		//noinspection ConstantConditions
		return identifier != null ? identifier.getName() : "";
	}

	@Override
	public RIdentifier getIdentifier()
	{
		return RubyPsiUtil.getChildByType(this, RIdentifier.class, 0);
	}

	@Override
	public ArgumentInfo.Type getType()
	{
		return ArgumentInfo.Type.PREDEFINED;
	}

	@Override
	@Nullable
	public String getValueText()
	{
		final RPsiElement value = getValue();
		return value != null ? value.getText() : null;
	}

	@Override
	public RPsiElement getValue()
	{
		return getChildByType(RPsiElement.class, 1);
	}
}
