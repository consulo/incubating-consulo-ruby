package org.jetbrains.plugins.ruby.ruby.roots;

import consulo.language.content.LanguageContentFolderScopes;
import consulo.language.content.TestContentFolderTypeProvider;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.module.content.ModuleRootManager;
import consulo.module.content.layer.ContentEntry;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 24.09.13.
 */
public class RubyModuleRootUtil
{
	public static boolean isUnderTestUnitRoot(@Nonnull Project project, @Nonnull final String url)
	{
		ModuleManager moduleManager = ModuleManager.getInstance(project);

		for(Module module : moduleManager.getModules())
		{
			ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
			for(ContentEntry contentEntry : moduleRootManager.getContentEntries())
			{
				for(String t : contentEntry.getFolderUrls(LanguageContentFolderScopes.of(TestContentFolderTypeProvider.getInstance())))
				{
					if(url.equals(t))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}
