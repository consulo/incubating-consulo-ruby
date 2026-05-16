package consulo.ruby.rails.impl.module.extension;

import jakarta.annotation.Nonnull;
import consulo.module.content.layer.extension.ModuleExtensionBase;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.ruby.rails.module.extension.RubyOnRailsModuleExtension;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class BaseRubyOnRailsModuleExtension extends ModuleExtensionBase<BaseRubyOnRailsModuleExtension> implements
		RubyOnRailsModuleExtension<BaseRubyOnRailsModuleExtension>
{
	public BaseRubyOnRailsModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer module)
	{
		super(id, module);
	}
}
