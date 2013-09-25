package org.consulo.jruby.rails.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.consulo.ruby.rails.module.extension.RubyOnRailsModuleExtension;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class JRubyOnRailsModuleExtension extends ModuleExtensionImpl<JRubyOnRailsModuleExtension>
		implements RubyOnRailsModuleExtension<JRubyOnRailsModuleExtension>
{
	public JRubyOnRailsModuleExtension(@NotNull String id, @NotNull Module module)
	{
		super(id, module);
	}
}