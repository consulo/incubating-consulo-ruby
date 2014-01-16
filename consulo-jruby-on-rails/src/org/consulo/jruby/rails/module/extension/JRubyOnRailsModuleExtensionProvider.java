package org.consulo.jruby.rails.module.extension;

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
public class JRubyOnRailsModuleExtensionProvider implements ModuleExtensionProvider<JRubyOnRailsModuleExtension, JRubyOnRailsMutableModuleExtension>
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
	public JRubyOnRailsModuleExtension createImmutable(@NotNull String s, @NotNull Module module)
	{
		return new JRubyOnRailsModuleExtension(s, module);
	}

	@NotNull
	@Override
	public JRubyOnRailsMutableModuleExtension createMutable(@NotNull String s, @NotNull Module module, @NotNull JRubyOnRailsModuleExtension jRubyOnRailsModuleExtension)
	{
		return new JRubyOnRailsMutableModuleExtension(s, module, jRubyOnRailsModuleExtension);
	}
}
