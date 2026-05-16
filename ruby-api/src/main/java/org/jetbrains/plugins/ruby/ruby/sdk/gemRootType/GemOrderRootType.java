package org.jetbrains.plugins.ruby.ruby.sdk.gemRootType;

import consulo.annotation.component.ExtensionImpl;
import consulo.content.OrderRootType;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
@ExtensionImpl
public class GemOrderRootType extends OrderRootType
{
	@Nonnull
	public static GemOrderRootType getInstance()
	{
		return getOrderRootType(GemOrderRootType.class);
	}

	public GemOrderRootType()
	{
		super("rubyGems");
	}
}
