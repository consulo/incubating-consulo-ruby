package consulo.jruby.rails.module.extension;

import consulo.ruby.rails.module.extension.RubyOnRailsModuleExtension;
import consulo.module.content.layer.extension.ModuleExtensionBase;
import consulo.module.content.layer.ModuleRootLayer;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class JRubyOnRailsModuleExtension extends ModuleExtensionBase<JRubyOnRailsModuleExtension>
		implements RubyOnRailsModuleExtension<JRubyOnRailsModuleExtension>
{
	public JRubyOnRailsModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer module)
	{
		super(id, module);
	}
}
