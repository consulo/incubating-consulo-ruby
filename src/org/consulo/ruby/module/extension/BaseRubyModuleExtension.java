package org.consulo.ruby.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.jruby.facet.RSupportPerModuleSettingsImpl;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkType;
import org.jetbrains.plugins.ruby.settings.RSupportPerModuleSettings;
import org.mustbe.consulo.roots.ContentFoldersSupport;
import org.mustbe.consulo.roots.impl.ProductionContentFolderTypeProvider;
import org.mustbe.consulo.roots.impl.TestContentFolderTypeProvider;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
@ContentFoldersSupport(value = {
		ProductionContentFolderTypeProvider.class,
		TestContentFolderTypeProvider.class
})
public class BaseRubyModuleExtension extends ModuleExtensionWithSdkImpl<BaseRubyModuleExtension> implements RubyModuleExtension<BaseRubyModuleExtension>
{
	private RSupportPerModuleSettings mySettings;

	public BaseRubyModuleExtension(@NotNull String id, @NotNull ModifiableRootModel module)
	{
		super(id, module);

		mySettings = new RSupportPerModuleSettingsImpl();
	}

	@NotNull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return RubySdkType.class;
	}

	@NotNull
	@Override
	public RSupportPerModuleSettings getSettings()
	{
		return mySettings;
	}
}
