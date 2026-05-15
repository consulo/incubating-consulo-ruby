package consulo.ruby.rails.module.extension;

import jakarta.annotation.Nonnull;

import consulo.disposer.Disposable;
import consulo.module.extension.MutableModuleExtension;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class BaseRubyOnRailsMutableModuleExtension extends BaseRubyOnRailsModuleExtension implements
		RubyOnRailsModuleExtension<BaseRubyOnRailsModuleExtension>, MutableModuleExtension<BaseRubyOnRailsModuleExtension>
{
	public BaseRubyOnRailsMutableModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer module)
	{
		super(id, module);
	}

	@RequiredUIAccess
	@Nullable
	@Override
	public Component createConfigurationComponent(@Nonnull Disposable disposable, @Nonnull Runnable runnable)
	{
		return null;
	}

	@Override
	public void setEnabled(boolean b)
	{
		myIsEnabled = b;
	}

	@Override
	public boolean isModified(@Nonnull BaseRubyOnRailsModuleExtension extension)
	{
		return extension.isEnabled() != isEnabled();
	}
}
