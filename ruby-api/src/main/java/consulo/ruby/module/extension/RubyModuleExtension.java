package consulo.ruby.module.extension;

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.settings.RSupportPerModuleSettings;
import consulo.module.extension.ModuleExtensionWithSdk;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public interface RubyModuleExtension<T extends RubyModuleExtension<T>> extends ModuleExtensionWithSdk<T>
{
	@Nonnull
	RSupportPerModuleSettings getSettings();
}
