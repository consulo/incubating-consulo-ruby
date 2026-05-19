package org.jetbrains.plugins.ruby.ruby.sdk.gemRootType;

import consulo.annotation.component.ExtensionImpl;
import consulo.content.OrderRootType;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
@ExtensionImpl
public class GemOrderRootType extends OrderRootType
{
	public static final String ID = "rubyGems";

	public GemOrderRootType()
	{
		super(ID);
	}
}
