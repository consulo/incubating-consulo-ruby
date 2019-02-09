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

package org.jetbrains.plugins.ruby.ruby.module.wizard;

import static org.jetbrains.plugins.ruby.addins.rspec.RSpecModuleSettings.RSpecSupportType.GEM;
import static org.jetbrains.plugins.ruby.addins.rspec.RSpecModuleSettings.RSpecSupportType.NONE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.JDOMException;
import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecModuleSettings;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.settings.RSupportPerModuleSettings;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExistsException;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: 16.08.2006
 */
public class RubyModuleBuilder extends RRModuleBuilder
{

	private String myTestUtinRootPath;

	@Override
	@Nonnull
	public Module createModule(ModifiableModuleModel moduleModel) throws InvalidDataException, IOException, ModuleWithNameAlreadyExistsException, JDOMException, ConfigurationException
	{
		final Module myModule = super.createModule(moduleModel);

		///////////////// Setup default tests folder /////////////////////////////////////////////////////////////////////////
		final List<String> testUnitFolderUrls = new ArrayList<String>();

		//TestUnit
		final String testUnitRootPath = getTestUtinRootPath();
		createDir(testUnitRootPath);

		final String testsUnitFolderUrl = VirtualFileUtil.constructLocalUrl(testUnitRootPath);
		testUnitFolderUrls.add(testsUnitFolderUrl);
		if(!RailsFacetUtil.hasRailsSupport(myModule))
		{
			//RubyModuleContentRootManagerImpl.getInstance(myModule).setTestUnitFolderUrls(testUnitFolderUrls);
		}
		///////////////// Module Settings /////////////////////////
		//Test:Unit
		final RSupportPerModuleSettings rubySupportSettings = RModuleUtil.getRubySupportSettings(myModule);
		assert rubySupportSettings != null;
		rubySupportSettings.setShouldUseTestUnitTestFramework(isTestUnitSupportEnabled());

		//RSpec for non Rails project
		if(!RailsFacetUtil.hasRailsSupport(myModule))
		{
			RSpecModuleSettings.getInstance(myModule).setRSpecSupportType(isRSpecSupportEnabled() ? GEM : NONE);
		}

		///////////////// Synch files /////////////////
		RModuleUtil.refreshRubyModuleTypeContent(myModule);

		return myModule;
	}

    /* private void installRSpecGem(final Module module) {
		if (!wizardRubyShouldUseRSpecFramework()) {
            return;
        }

        if (shouldCreateRSpecGem()) {
            RSpecUtil.installRSpecGem(module, getSdk());
        }
    }*/

	@Override
	public void setTestsUnitRootPath(@Nonnull final String path)
	{
		myTestUtinRootPath = path;
	}

	private String getTestUtinRootPath()
	{
		return myTestUtinRootPath == null ? getContentEntryPath() : myTestUtinRootPath;
	}

	@Override
	protected void setupContentRoot(ModifiableRootModel rootModel)
	{
		String moduleRootPath = getContentEntryPath();
		if(moduleRootPath != null)
		{
			LocalFileSystem lfs = LocalFileSystem.getInstance();
			VirtualFile moduleContentRoot = lfs.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(moduleRootPath));
			if(moduleContentRoot != null)
			{
				rootModel.addContentEntry(moduleContentRoot);
			}
		}
	}
}
