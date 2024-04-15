package org.jetbrains.plugins.ruby.ruby.sdk.gemRootType;

import consulo.content.OrderRootType;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
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
