package org.consulo.jruby.module.extension;

import javax.swing.Icon;

import org.consulo.jruby.JRubyIcons;
import org.consulo.module.extension.ModuleExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class JRubyModuleExtensionProvider implements ModuleExtensionProvider<JRubyModuleExtension, JRubyMutableModuleExtension>
{
	@Nullable
	@Override
	public Icon getIcon()
	{
		return JRubyIcons.JRuby;
	}

	@NotNull
	@Override
	public String getName()
	{
		return "JRuby";
	}

	@NotNull
	@Override
	public JRubyModuleExtension createImmutable(@NotNull String s, @NotNull Module module)
	{
		return new JRubyModuleExtension(s, module);
	}

	@NotNull
	@Override
	public JRubyMutableModuleExtension createMutable(@NotNull String s, @NotNull Module module)
	{
		return new JRubyMutableModuleExtension(s, module);
	}
}
