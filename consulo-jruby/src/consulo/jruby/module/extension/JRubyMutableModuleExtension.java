package consulo.jruby.module.extension;

import javax.swing.JComponent;

import consulo.jruby.module.extension.JRubyModuleExtension;
import consulo.ruby.module.extension.RubyModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.util.ui.JBUI;
import consulo.extension.ui.ModuleExtensionSdkBoxBuilder;
import consulo.module.extension.MutableModuleExtensionWithSdk;
import consulo.module.extension.MutableModuleInheritableNamedPointer;
import consulo.roots.ModuleRootLayer;
import consulo.util.ui.components.VerticalLayoutPanel;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class JRubyMutableModuleExtension extends JRubyModuleExtension implements RubyModuleExtension<JRubyModuleExtension>, MutableModuleExtensionWithSdk<JRubyModuleExtension>
{
	public JRubyMutableModuleExtension(@NotNull String id, @NotNull ModuleRootLayer module)
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
	public boolean isModified(@NotNull JRubyModuleExtension extension)
	{
		return isModifiedImpl(extension);
	}
}
