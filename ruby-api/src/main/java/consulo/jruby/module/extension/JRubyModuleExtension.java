package consulo.jruby.module.extension;

import consulo.content.bundle.SdkType;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.content.layer.extension.ModuleExtensionWithSdkBase;
import consulo.ruby.module.extension.RubyModuleExtension;
import jakarta.annotation.Nonnull;
import org.jetbrains.plugins.ruby.jruby.facet.RSupportPerModuleSettingsImpl;
import org.jetbrains.plugins.ruby.ruby.sdk.jruby.JRubySdkType;
import org.jetbrains.plugins.ruby.settings.RSupportPerModuleSettings;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class JRubyModuleExtension extends ModuleExtensionWithSdkBase<JRubyModuleExtension> implements RubyModuleExtension<JRubyModuleExtension>
{
	private RSupportPerModuleSettings mySettings;

	public JRubyModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
		mySettings = new RSupportPerModuleSettingsImpl();
	}

	@Nonnull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return JRubySdkType.class;
	}

	@Nonnull
	@Override
	public RSupportPerModuleSettings getSettings()
	{
		return mySettings;
	}
}
