package consulo.ruby.module.extension;

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.jruby.facet.RSupportPerModuleSettingsImpl;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkType;
import org.jetbrains.plugins.ruby.settings.RSupportPerModuleSettings;
import com.intellij.openapi.projectRoots.SdkType;
import consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import consulo.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class BaseRubyModuleExtension extends ModuleExtensionWithSdkImpl<BaseRubyModuleExtension> implements RubyModuleExtension<BaseRubyModuleExtension>
{
	private RSupportPerModuleSettings mySettings;

	public BaseRubyModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer module)
	{
		super(id, module);

		mySettings = new RSupportPerModuleSettingsImpl();
	}

	@Nonnull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return RubySdkType.class;
	}

	@Nonnull
	@Override
	public RSupportPerModuleSettings getSettings()
	{
		return mySettings;
	}
}
