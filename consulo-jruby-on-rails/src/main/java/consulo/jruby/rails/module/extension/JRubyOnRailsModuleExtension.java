package consulo.jruby.rails.module.extension;

import consulo.ruby.rails.module.extension.RubyOnRailsModuleExtension;
import org.jetbrains.annotations.NotNull;
import consulo.module.extension.impl.ModuleExtensionImpl;
import consulo.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class JRubyOnRailsModuleExtension extends ModuleExtensionImpl<JRubyOnRailsModuleExtension>
		implements RubyOnRailsModuleExtension<JRubyOnRailsModuleExtension>
{
	public JRubyOnRailsModuleExtension(@NotNull String id, @NotNull ModuleRootLayer module)
	{
		super(id, module);
	}
}
