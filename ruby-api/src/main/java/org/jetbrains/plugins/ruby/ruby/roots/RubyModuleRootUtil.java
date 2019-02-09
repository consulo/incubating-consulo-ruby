package org.jetbrains.plugins.ruby.ruby.roots;

import javax.annotation.Nonnull;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import consulo.roots.ContentFolderScopes;
import consulo.roots.impl.TestContentFolderTypeProvider;

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
				for(String t : contentEntry.getFolderUrls(ContentFolderScopes.of(TestContentFolderTypeProvider.getInstance())))
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
