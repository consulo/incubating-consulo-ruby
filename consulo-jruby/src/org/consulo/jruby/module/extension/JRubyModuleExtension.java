package org.consulo.jruby.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import org.consulo.ruby.module.extension.RubyModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.sdk.jruby.JRubySdkType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.SdkType;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class JRubyModuleExtension extends ModuleExtensionWithSdkImpl<JRubyModuleExtension> implements RubyModuleExtension<JRubyModuleExtension>
{
	public JRubyModuleExtension(@NotNull String id, @NotNull Module module)
	{
		super(id, module);
	}

	@Override
	protected Class<? extends SdkType> getSdkTypeClass()
	{
		return JRubySdkType.class;
	}
}
