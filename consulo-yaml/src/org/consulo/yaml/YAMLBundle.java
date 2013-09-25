package org.consulo.yaml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;
import com.intellij.AbstractBundle;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class YAMLBundle extends AbstractBundle
{
	public static final String BUNDLE = "messages.YAMLBundle";

	private static final YAMLBundle INSTANCE = new YAMLBundle();

	private YAMLBundle()
	{
		super(BUNDLE);
	}

	@NotNull
	public static String message(@PropertyKey(resourceBundle = BUNDLE) String key)
	{
		return INSTANCE.getMessage(key);
	}

	@NotNull
	public static String message(@PropertyKey(resourceBundle = BUNDLE) String key, Object args)
	{
		return INSTANCE.getMessage(key, args);
	}
}
