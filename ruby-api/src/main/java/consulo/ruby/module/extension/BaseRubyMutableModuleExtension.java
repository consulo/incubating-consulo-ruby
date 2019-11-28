package consulo.ruby.module.extension;

import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.util.ui.JBUI;
import consulo.desktop.util.awt.component.VerticalLayoutPanel;
import consulo.extension.ui.ModuleExtensionSdkBoxBuilder;
import consulo.module.extension.MutableModuleExtensionWithSdk;
import consulo.module.extension.MutableModuleInheritableNamedPointer;
import consulo.roots.ModuleRootLayer;
import consulo.ui.annotation.RequiredUIAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class BaseRubyMutableModuleExtension extends BaseRubyModuleExtension implements RubyModuleExtension<BaseRubyModuleExtension>, MutableModuleExtensionWithSdk<BaseRubyModuleExtension>
{
	public BaseRubyMutableModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer module)
	{
		super(id, module);
	}

	@Nonnull
	@Override
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		return (MutableModuleInheritableNamedPointer<Sdk>) super.getInheritableSdk();
	}

	@RequiredUIAccess
	@Nullable
	@Override
	public JComponent createConfigurablePanel(@Nullable Runnable runnable)
	{
		VerticalLayoutPanel verticalLayoutPanel = JBUI.Panels.verticalPanel();
		verticalLayoutPanel.addComponent(ModuleExtensionSdkBoxBuilder.createAndDefine(this, runnable).build());

		return verticalLayoutPanel;
	}

	@Override
	public void setEnabled(boolean b)
	{
		myIsEnabled = b;
	}

	@Override
	public boolean isModified(@Nonnull BaseRubyModuleExtension extension)
	{
		return isModifiedImpl(extension);
	}
}
