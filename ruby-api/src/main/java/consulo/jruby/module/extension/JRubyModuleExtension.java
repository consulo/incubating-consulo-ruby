package consulo.jruby.module.extension;

import consulo.ruby.module.extension.RubyModuleExtension;
import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.jruby.facet.RSupportPerModuleSettingsImpl;
import org.jetbrains.plugins.ruby.ruby.sdk.jruby.JRubySdkType;
import org.jetbrains.plugins.ruby.settings.RSupportPerModuleSettings;
import com.intellij.openapi.projectRoots.SdkType;
import consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import consulo.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class JRubyModuleExtension extends ModuleExtensionWithSdkImpl<JRubyModuleExtension> implements RubyModuleExtension<JRubyModuleExtension>
{
	private RSupportPerModuleSettings mySettings;

	public JRubyModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer module)
	{
		super(id, module);
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
