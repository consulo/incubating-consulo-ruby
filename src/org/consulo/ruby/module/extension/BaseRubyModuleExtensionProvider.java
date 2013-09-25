package org.consulo.ruby.module.extension;

import javax.swing.Icon;

import org.consulo.module.extension.ModuleExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class BaseRubyModuleExtensionProvider implements ModuleExtensionProvider<BaseRubyModuleExtension, BaseRubyMutableModuleExtension>
{
	@Nullable
	@Override
	public Icon getIcon()
	{
		return RubyIcons.RUBY_ICON;
	}

	@NotNull
	@Override
	public String getName()
	{
		return "Ruby";
	}

	@NotNull
	@Override
	public Class<BaseRubyModuleExtension> getImmutableClass()
	{
		return BaseRubyModuleExtension.class;
	}

	@NotNull
	@Override
	public BaseRubyModuleExtension createImmutable(@NotNull String s, @NotNull Module module)
	{
		return new BaseRubyModuleExtension(s, module);
	}

	@NotNull
	@Override
	public BaseRubyMutableModuleExtension createMutable(@NotNull String s, @NotNull Module module, @NotNull BaseRubyModuleExtension baseRubyModuleExtension)
	{
		return new BaseRubyMutableModuleExtension(s, module, baseRubyModuleExtension);
	}
}
