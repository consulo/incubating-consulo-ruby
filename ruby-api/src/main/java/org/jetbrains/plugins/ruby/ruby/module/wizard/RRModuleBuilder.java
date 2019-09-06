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

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.RubyWizardSettingsHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 18.08.2006
 */
public abstract class RRModuleBuilder  implements /*SourcePathsBuilder, */RubyWizardSettingsHolder
{
	private Sdk mySdk;
	private boolean myShouldUseRSpec;
	private boolean myShouldUseTestUnit;
	private List<Pair<String, String>> mySourcePaths;
	private String myContentRootPath;

	@Override
	public abstract void setTestsUnitRootPath(@Nonnull final String contentRootPath);

	protected abstract void setupContentRoot(final ModifiableRootModel rootModel);

	@Override
	public boolean isRSpecSupportEnabled()
	{
		return myShouldUseRSpec;
	}

	public boolean isTestUnitSupportEnabled()
	{
		return myShouldUseTestUnit;
	}

	public void enableTestUnitSupport(boolean shouldUseTestUnit)
	{
		myShouldUseTestUnit = shouldUseTestUnit;
	}

	public void enableRSpecSupport(boolean shouldUseRSpec)
	{
		myShouldUseRSpec = shouldUseRSpec;
	}

	@Override
	@Nullable
	public Sdk getSdk()
	{
		return mySdk;
	}

	public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException
	{
		/*if (mySdk != null) {
            rootModel.setSdk(mySdk);
            //HACK. Oterwise for new Project first roots change event have wrong SDK!
            //      Value must be same as key, not clone!
            SdksModel.getInstance(rootModel.getModule().getProject()).getSdks().put(mySdk, mySdk);
        } else {
            rootModel.inheritJdk();
        }
          */
		setupContentRoot(rootModel);
	}

	@Override
	public void setSdk(Sdk jdk)
	{
		mySdk = jdk;
	}

	@Nullable
	public String getContentEntryPath()
	{
		return myContentRootPath;
	}

	public void setContentEntryPath(final String moduleRootPath)
	{
		myContentRootPath = moduleRootPath;
	}

	//@Override
	public void setSourcePaths(final List<Pair<String, String>> paths)
	{
		mySourcePaths = paths;
	}

	//@Override
	public List<Pair<String, String>> getSourcePaths()
	{
		return mySourcePaths;
	}

	//@Override
	public void addSourcePath(final Pair<String, String> sourcePathInfo)
	{
		if(mySourcePaths == null)
		{
			mySourcePaths = new ArrayList<Pair<String, String>>();
		}
		mySourcePaths.add(sourcePathInfo);
	}

	protected void createDir(@Nonnull final String path)
	{
		final File file = new File(path);

		if(!file.exists())
		{
			boolean wasCreated;
			try
			{
				wasCreated = file.mkdirs();
			}
			catch(Exception e)
			{
				wasCreated = false;
			}
			if(!wasCreated)
			{
				Messages.showErrorDialog(RBundle.message("file.cant.create.folder.text", path), RBundle.message("file.cant.create.folder.title"));
			}
		}
	}

}
