package org.consulo.ruby.module.extension;

import org.consulo.module.extension.ModuleExtensionWithSdk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.settings.RSupportPerModuleSettings;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public interface RubyModuleExtension<T extends RubyModuleExtension<T>> extends ModuleExtensionWithSdk<T>
{
	@NotNull
	RSupportPerModuleSettings getSettings();
}
