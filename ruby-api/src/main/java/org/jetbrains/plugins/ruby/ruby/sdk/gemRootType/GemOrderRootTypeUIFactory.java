package org.jetbrains.plugins.ruby.ruby.sdk.gemRootType;

import consulo.annotation.component.ExtensionImpl;
import consulo.content.bundle.Sdk;
import consulo.fileChooser.FileChooserDescriptor;
import consulo.ide.ui.OrderRootTypeUIFactory;
import consulo.ide.ui.SdkPathEditor;
import consulo.localize.LocalizeValue;
import consulo.ui.image.Image;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
@ExtensionImpl
public class GemOrderRootTypeUIFactory implements OrderRootTypeUIFactory
{
	@Override
	public String getOrderRootTypeId()
	{
		return "rubyGems";
	}

	@Override
	public SdkPathEditor createPathEditor(Sdk sdk)
	{
		return new SdkPathEditor(getNodeText(), GemOrderRootType.ID, new FileChooserDescriptor(true, false, false, false, false, true),
				sdk);
	}

	@Override
	public Image getIcon()
	{
		return RubyIcons.RUBY_ICON;
	}

	@Override
	public LocalizeValue getNodeText()
	{
		return LocalizeValue.localizeTODO("Gem's");
	}
}
