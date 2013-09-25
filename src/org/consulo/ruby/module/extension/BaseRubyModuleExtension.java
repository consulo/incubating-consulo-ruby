package org.consulo.ruby.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.SdkType;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class BaseRubyModuleExtension extends ModuleExtensionWithSdkImpl<BaseRubyModuleExtension> implements RubyModuleExtension<BaseRubyModuleExtension>
{
	public BaseRubyModuleExtension(@NotNull String id, @NotNull Module module)
	{
		super(id, module);
	}

	@Override
	protected Class<? extends SdkType> getSdkTypeClass()
	{
		return RubySdkType.class;
	}
}
