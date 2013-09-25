package org.consulo.ruby.rails.module.extension;

import javax.swing.JComponent;

import org.consulo.module.extension.MutableModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class BaseRubyOnRailsMutableModuleExtension extends BaseRubyOnRailsModuleExtension
		implements RubyOnRailsModuleExtension<BaseRubyOnRailsModuleExtension>,
					MutableModuleExtension<BaseRubyOnRailsModuleExtension>
{
	private BaseRubyOnRailsModuleExtension myOriginalExtension;

	public BaseRubyOnRailsMutableModuleExtension(@NotNull String id, @NotNull Module module, @NotNull BaseRubyOnRailsModuleExtension originalExtension)
	{
		super(id, module);
		myOriginalExtension = originalExtension;
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull ModifiableRootModel modifiableRootModel, @Nullable Runnable runnable)
	{
		return null;
	}

	@Override
	public void setEnabled(boolean b)
	{
		myIsEnabled = b;
	}

	@Override
	public boolean isModified()
	{
		return myOriginalExtension.isEnabled() != isEnabled();
	}

	@Override
	public void commit()
	{
		myOriginalExtension.commit(this);
	}
}
