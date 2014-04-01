package org.consulo.jruby.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import org.consulo.ruby.module.extension.RubyModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.jruby.facet.RSupportPerModuleSettingsImpl;
import org.jetbrains.plugins.ruby.ruby.sdk.jruby.JRubySdkType;
import org.jetbrains.plugins.ruby.settings.RSupportPerModuleSettings;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class JRubyModuleExtension extends ModuleExtensionWithSdkImpl<JRubyModuleExtension> implements RubyModuleExtension<JRubyModuleExtension>
{
	private RSupportPerModuleSettings mySettings;

	public JRubyModuleExtension(@NotNull String id, @NotNull ModifiableRootModel module)
	{
		super(id, module);
		mySettings = new RSupportPerModuleSettingsImpl();
	}

	@NotNull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return JRubySdkType.class;
	}

	@NotNull
	@Override
	public RSupportPerModuleSettings getSettings()
	{
		return mySettings;
	}
}
