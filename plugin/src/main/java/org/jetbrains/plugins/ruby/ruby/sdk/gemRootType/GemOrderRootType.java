package org.jetbrains.plugins.ruby.ruby.sdk.gemRootType;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.roots.OrderRootType;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class GemOrderRootType extends OrderRootType
{
	@NotNull
	public static GemOrderRootType getInstance()
	{
		return getOrderRootType(GemOrderRootType.class);
	}

	public GemOrderRootType()
	{
		super("rubyGems");
	}
}