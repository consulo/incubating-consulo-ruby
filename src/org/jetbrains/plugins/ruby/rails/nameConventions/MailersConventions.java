/*
 * Copyright 2000-2008 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.ruby.rails.nameConventions;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.StandardRailsPaths;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.FileSymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.InterpretationMode;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RFileUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Dec 10, 2007
 */
public class MailersConventions
{
	public static final String ACTION_MAILER_MODULE = RailsConstants.SDK_ACTION_MAILER_MODULE;
	public static final String BASE_CLASS = RailsConstants.BASE_CLASS;
	private static final String ACTION_MAILER_BUILT_IN_GEMS_VENDOR_NAME = RailsConstants.ACTION_MAILER_BUILT_IN_GEMS_VENDOR_NAME;
	private static final String ACTION_MAILER_BUILT_IN_GEMS_TMAIL = RailsConstants.ACTION_MAILER_BUILT_IN_GEMS_TMAIL_NAME;


	public static boolean isMailerFile(@NotNull final RVirtualFile rFile, @Nullable final Module module)
	{
		return isModelFile(rFile, module, RContainerUtil.getTopLevelClasses(rFile));
	}

	public static boolean isModelFile(@NotNull final RVirtualFile rFile, @Nullable final Module module, @NotNull final List<RVirtualClass> classes)
	{
		if(module == null)
		{
			return false;
		}

		final String fileUrl = rFile.getContainingFileUrl();

		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for module with Rails Support

		//models root is the same as mailers root
		final String modelsRoot = railsPaths.getModelRootURL();
		if(!fileUrl.startsWith(modelsRoot))
		{
			return false;
		}

		for(RVirtualClass virtualClass : classes)
		{
			if(isMailerClass(virtualClass, module))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isMailerClass(@Nullable final RVirtualClass rClass, @NotNull final Module module)
	{
		if(!RailsFacetUtil.hasRailsSupport(module))
		{
			return false;
		}

		/**
		 * TODO
		 *
		 * Uncomment next block when virtual file cache will be able
		 * to parse complicated class names(such as Module1:Module2:ClassName)
		 * into RVirtualElements hierarchy
		 */

          /*  final ArrayList<RVirtualContainer> path = RVirtualPsiUtils.getVirtualPath(rClass);
			final String controllersRootURL = getControllersRootURL(module);
            assert controllersRootURL != null; // for rails modules isn't null

            final StringBuffer buff = new StringBuffer(controllersRootURL);
            for (RVirtualContainer container : path) {
                if (container instanceof RVirtualFile) {
                    continue;
                }
                buff.append(VFS_PATH_SEPARATOR);
                buff.append(RailsUtil.getControllerFileName(container.getName()));
            }
        */

		assert rClass != null; // for rails modules isn't null
		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for Rails Module
		final String fileUrl = rClass.getContainingFileUrl();

		//check directory, mailers directory same as models directory
		final String modelsRoot = railsPaths.getModelRootURL();
		if(!fileUrl.startsWith(modelsRoot))
		{
			return false;
		}
		final String className = rClass.getName();
		final String fileName = VirtualFileUtil.removeExtension(VirtualFileUtil.getFileName(fileUrl));
		if(!NamingConventions.toUnderscoreCase(className).equals(fileName))
		{
			return false;
		}

		//check superclsss
		final RVirtualName superClass = rClass.getVirtualSuperClass();
		if(superClass == null)
		{
			return false;
		}
		final List<String> path = superClass.getPath();
		//TODO it is HACK!!!! Doesn't work with inheritance
		return (path.size() == 2 && MailersConventions.ACTION_MAILER_MODULE.equals(path.get(0))) && MailersConventions.BASE_CLASS.equals(path.get(1));
	}

	public static void loadBuiltInGems(@Nullable final FileSymbol fileSymbol, @NotNull final InterpretationMode mode)
	{
		if(fileSymbol == null)
		{
			return;
		}

		for(String activeSupportFileUrl : RFileUtil.findUrlsForName(fileSymbol, "action_mailer"))
		{
			//load built-in gems : tmail
			final String actionMailerVendorDirUrl = VirtualFileUtil.removeExtension(activeSupportFileUrl) + VirtualFileUtil.VFS_PATH_SEPARATOR + ACTION_MAILER_BUILT_IN_GEMS_VENDOR_NAME;
			final VirtualFile actionMallerVendorDir = VirtualFileManager.getInstance().findFileByUrl(actionMailerVendorDirUrl);

			if(actionMallerVendorDir != null)
			{
				VirtualFile tMailDir = null;
				for(VirtualFile file : actionMallerVendorDir.getChildren())
				{
					if(file.isDirectory() && file.getName().startsWith(ACTION_MAILER_BUILT_IN_GEMS_TMAIL))
					{
						//TODO comparision is just lexicographically, really we should parse version suffix
						if(tMailDir == null || file.getName().compareTo(tMailDir.getName()) > 0)
						{
							tMailDir = file;
						}
					}
				}
				if(tMailDir != null)
				{
					final String tMailDirUrl = tMailDir.getUrl();
					fileSymbol.addLoadPathUrl(tMailDirUrl);
					final String tmailUrl = tMailDirUrl + VirtualFileUtil.VFS_PATH_SEPARATOR + ACTION_MAILER_BUILT_IN_GEMS_TMAIL + "." + RubyFileType.RUBY.getDefaultExtension();
					FileSymbolUtil.process(fileSymbol, tmailUrl, mode, false);
				}
			}

			//TODO text-format gem
		}
	}
}
