package consulo.ruby.rails.module.extension;

import org.jetbrains.annotations.NotNull;
import consulo.module.extension.impl.ModuleExtensionImpl;
import consulo.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class BaseRubyOnRailsModuleExtension extends ModuleExtensionImpl<BaseRubyOnRailsModuleExtension> implements
		RubyOnRailsModuleExtension<BaseRubyOnRailsModuleExtension>
{
	public BaseRubyOnRailsModuleExtension(@NotNull String id, @NotNull ModuleRootLayer module)
	{
		super(id, module);
	}
}
