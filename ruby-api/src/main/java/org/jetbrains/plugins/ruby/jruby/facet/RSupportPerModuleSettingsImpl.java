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

package org.jetbrains.plugins.ruby.jruby.facet;

import javax.annotation.Nonnull;

import org.jdom.Element;

import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.jruby.facet.ui.tabs.JRubyLoadPathChooser;
import org.jetbrains.plugins.ruby.jruby.facet.ui.tabs.JRubyRTestFrameworkChooser;
import org.jetbrains.plugins.ruby.jruby.facet.ui.tabs.JRubySdkEditorTab;
import org.jetbrains.plugins.ruby.settings.RSupportPerModuleSettings;
import org.jetbrains.plugins.ruby.support.ui.checkableDir.CheckableDirectoriesContainer;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;

/**
 * Created by IntelliJ IDEA.
 * User: Roman.Chernyatchik
 * Date: Sep 11, 2007
 */

public class RSupportPerModuleSettingsImpl implements RSupportPerModuleSettings
{

	private boolean changed;
	private boolean shouldUseRSpecTestFramework = true;
	private CheckableDirectoriesContainer myLoadPathDirs;
	private String myUnitTestsRootUrl;

	public RSupportPerModuleSettingsImpl()
	{
		myLoadPathDirs = new CheckableDirectoriesContainer();
	}

	public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager)
	{
		changed = false;
		return new FacetEditorTab[]{
				new JRubySdkEditorTab(this, editorContext),
				new JRubyRTestFrameworkChooser(this, editorContext),
				new JRubyLoadPathChooser(this, editorContext)
		};
	}

	public void readExternal(Element element) throws InvalidDataException
	{
		JRubyFacetExternalizer.getInstance().readExternal(this, element);
	}

	public void writeExternal(Element element) throws WriteExternalException
	{
		JRubyFacetExternalizer.getInstance().writeExternal(this, element);
	}

	@Override
	@Nonnull
	public CheckableDirectoriesContainer getLoadPathDirs()
	{
		return myLoadPathDirs;
	}

	public boolean isChanged()
	{
		return changed;
	}

	public void setChanged(boolean changed)
	{
		this.changed = changed;
	}

	@Override
	public void setLoadPathDirs(@Nonnull final CheckableDirectoriesContainer loadPathDirs)
	{
		myLoadPathDirs = loadPathDirs;
	}

	@Override
	public boolean shouldUseTestUnitTestFramework()
	{
		return shouldUseRSpecTestFramework;
	}

	@Override
	public void setShouldUseTestUnitTestFramework(final boolean shouldUse)
	{
		shouldUseRSpecTestFramework = shouldUse;
	}

	public void setUnitTestsRootUrl(final String testsUnitRootUrl)
	{
		myUnitTestsRootUrl = testsUnitRootUrl;
	}

	@Nullable
	public String getUnitTestsRootUrl()
	{
		return myUnitTestsRootUrl;
	}
}
