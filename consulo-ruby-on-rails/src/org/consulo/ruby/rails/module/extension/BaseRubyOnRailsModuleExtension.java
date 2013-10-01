package org.consulo.ruby.rails.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class BaseRubyOnRailsModuleExtension extends ModuleExtensionImpl<BaseRubyOnRailsModuleExtension> implements RubyOnRailsModuleExtension<BaseRubyOnRailsModuleExtension>
{
	public BaseRubyOnRailsModuleExtension(@NotNull String id, @NotNull Module module)
	{
		super(id, module);
	}
}
