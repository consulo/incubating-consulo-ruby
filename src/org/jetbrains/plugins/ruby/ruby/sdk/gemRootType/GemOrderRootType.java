package org.jetbrains.plugins.ruby.ruby.sdk.gemRootType;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.roots.PersistentOrderRootType;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class GemOrderRootType extends PersistentOrderRootType
{
	@NotNull
	public static GemOrderRootType getInstance()
	{
		return getOrderRootType(GemOrderRootType.class);
	}

	public GemOrderRootType()
	{
		super("GEM", "GEM");
	}
}
