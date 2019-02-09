package consulo.jruby.rails.module.extension;

import javax.annotation.Nonnull;

import consulo.ruby.rails.module.extension.RubyOnRailsModuleExtension;
import consulo.module.extension.impl.ModuleExtensionImpl;
import consulo.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class JRubyOnRailsModuleExtension extends ModuleExtensionImpl<JRubyOnRailsModuleExtension>
		implements RubyOnRailsModuleExtension<JRubyOnRailsModuleExtension>
{
	public JRubyOnRailsModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer module)
	{
		super(id, module);
	}
}
