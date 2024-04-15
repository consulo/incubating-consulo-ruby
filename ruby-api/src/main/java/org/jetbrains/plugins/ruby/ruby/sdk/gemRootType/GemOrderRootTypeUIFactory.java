package org.jetbrains.plugins.ruby.ruby.sdk.gemRootType;

import consulo.fileChooser.FileChooserDescriptor;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import consulo.content.bundle.Sdk;
import consulo.ide.ui.SdkPathEditor;
import consulo.ide.ui.OrderRootTypeUIFactory;
import consulo.ui.image.Image;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class GemOrderRootTypeUIFactory implements OrderRootTypeUIFactory
{
	@Override
	public SdkPathEditor createPathEditor(Sdk sdk)
	{
		return new SdkPathEditor(getNodeText(), GemOrderRootType.getInstance(), new FileChooserDescriptor(true, false, false, false, false, true),
				sdk);
	}

	@Override
	public Image getIcon()
	{
		return RubyIcons.RUBY_ICON;
	}

	@Override
	public String getNodeText()
	{
		return "Gem's";
	}
}
