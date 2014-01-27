package org.consulo.ruby.rails.module.extension;

import javax.swing.Icon;

import org.consulo.module.extension.ModuleExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class BaseRubyOnRailsModuleExtensionProvider implements ModuleExtensionProvider<BaseRubyOnRailsModuleExtension, BaseRubyOnRailsMutableModuleExtension>
{
	@Nullable
	@Override
	public Icon getIcon()
	{
		return RailsIcons.RAILS_SMALL;
	}

	@NotNull
	@Override
	public String getName()
	{
		return "Rails";
	}

	@NotNull
	@Override
	public BaseRubyOnRailsModuleExtension createImmutable(@NotNull String s, @NotNull Module module)
	{
		return new BaseRubyOnRailsModuleExtension(s, module);
	}

	@NotNull
	@Override
	public BaseRubyOnRailsMutableModuleExtension createMutable(@NotNull String s, @NotNull Module module)
	{
		return new BaseRubyOnRailsMutableModuleExtension(s, module);
	}
}
