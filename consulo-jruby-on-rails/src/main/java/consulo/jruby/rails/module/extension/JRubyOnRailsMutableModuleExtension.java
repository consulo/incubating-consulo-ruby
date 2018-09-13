package consulo.jruby.rails.module.extension;

import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.module.extension.ModuleExtension;
import consulo.module.extension.MutableModuleExtension;
import consulo.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class JRubyOnRailsMutableModuleExtension extends JRubyOnRailsModuleExtension
		implements MutableModuleExtension<JRubyOnRailsModuleExtension>, ModuleExtension<JRubyOnRailsModuleExtension>
{
	public JRubyOnRailsMutableModuleExtension(@NotNull String id, @NotNull ModuleRootLayer module)
	{
		super(id, module);
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@Nullable Runnable runnable)
	{
		return null;
	}

	@Override
	public void setEnabled(boolean b)
	{
		myIsEnabled = b;
	}

	@Override
	public boolean isModified(@NotNull JRubyOnRailsModuleExtension extension)
	{
		return extension.isEnabled() != isEnabled();
	}
}
