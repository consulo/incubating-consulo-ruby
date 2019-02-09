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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.StandardRailsPaths;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.openapi.module.Module;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Dec 10, 2007
 */
public class ApisConventions
{
	public static final String ACTION_WEB_SERVICE_MODULE = RailsConstants.SDK_ACTION_WEB_SERVICE;
	public static final String API_MODULE = RailsConstants.SDK_ACTION_WEB_SERVICE_API_MODULE_NAME;
	public static final String BASE_CLASS = RailsConstants.BASE_CLASS;

	public static boolean isApisFile(@Nonnull final RVirtualFile rFile, @Nullable final Module module)
	{
		return isWebServiceApiFile(rFile, module, RContainerUtil.getTopLevelClasses(rFile));
	}

	public static boolean isWebServiceApiFile(@Nonnull final RVirtualFile rFile, @Nullable final Module module, @Nonnull final List<RVirtualClass> classes)
	{
		if(module == null)
		{
			return false;
		}

		final String fileUrl = rFile.getContainingFileUrl();

		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for Rails Module

		final String apisRoot = railsPaths.getApisRootURL();
		if(!fileUrl.startsWith(apisRoot))
		{
			return false;
		}

		for(RVirtualClass virtualClass : classes)
		{
			if(isApiWebServiceClass(virtualClass, module))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isApiWebServiceClass(@Nullable final RVirtualClass rClass, @Nonnull final Module module)
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

		//check superclass
		final RVirtualName superClass = rClass.getVirtualSuperClass();
		if(superClass == null)
		{
			return false;
		}
		final List<String> path = superClass.getPath();

		//TODO it is HACK!!!! Doesn't work with inheritance
		return (path.size() == 3 && ApisConventions.ACTION_WEB_SERVICE_MODULE.equals(path.get(0))) && ApisConventions.API_MODULE.equals(path.get(1)) && ApisConventions.BASE_CLASS.equals(path.get(2));
	}
}
