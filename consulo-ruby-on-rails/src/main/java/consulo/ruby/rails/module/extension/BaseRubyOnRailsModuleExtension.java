package consulo.ruby.rails.module.extension;

import javax.annotation.Nonnull;
import consulo.module.extension.impl.ModuleExtensionImpl;
import consulo.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class BaseRubyOnRailsModuleExtension extends ModuleExtensionImpl<BaseRubyOnRailsModuleExtension> implements
		RubyOnRailsModuleExtension<BaseRubyOnRailsModuleExtension>
{
	public BaseRubyOnRailsModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer module)
	{
		super(id, module);
	}
}
