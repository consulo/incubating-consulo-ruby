package org.consulo.ruby.module.extension;

import javax.swing.JComponent;

import org.consulo.module.extension.MutableModuleExtensionWithSdk;
import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.consulo.module.extension.ui.ModuleExtensionWithSdkPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class BaseRubyMutableModuleExtension extends BaseRubyModuleExtension implements RubyModuleExtension<BaseRubyModuleExtension>, MutableModuleExtensionWithSdk<BaseRubyModuleExtension>
{
	public BaseRubyMutableModuleExtension(@NotNull String id, @NotNull ModuleRootLayer module)
	{
		super(id, module);
	}

	@NotNull
	@Override
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		return (MutableModuleInheritableNamedPointer<Sdk>) super.getInheritableSdk();
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@Nullable Runnable runnable)
	{
		return wrapToNorth(new ModuleExtensionWithSdkPanel(this, runnable));
	}

	@Override
	public void setEnabled(boolean b)
	{
		myIsEnabled = b;
	}

	@Override
	public boolean isModified(@NotNull BaseRubyModuleExtension extension)
	{
		return isModifiedImpl(extension);
	}
}
