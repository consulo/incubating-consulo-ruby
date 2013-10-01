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

package org.jetbrains.plugins.ruby.jruby.facet.ui.tabs;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.jruby.facet.RSupportPerModuleSettingsImpl;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.SymbolsCache;
import org.jetbrains.plugins.ruby.ruby.module.ui.roots.loadPath.RLoadPathChooserUtil;
import org.jetbrains.plugins.ruby.support.ui.checkableDir.CheckableDirectoriesContainer;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.Ref;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 20, 2007
 */
public class JRubyLoadPathChooser extends FacetEditorTab
{
	private Ref<CheckableDirectoriesContainer> myLoadPathDirsCopyRef = new Ref<CheckableDirectoriesContainer>();

	public JRubyLoadPathChooser(@NotNull final RSupportPerModuleSettingsImpl jRubyFacetConfiguration, @NotNull final FacetEditorContext editorContext)
	{
		myJRubyFacetConfiguration = jRubyFacetConfiguration;
		myEditorContext = editorContext;
	}

	private RSupportPerModuleSettingsImpl myJRubyFacetConfiguration;
	private FacetEditorContext myEditorContext;

	@Nls
	public String getDisplayName()
	{
		return RBundle.message("module.settings.dialog.load.path.tab.title");
	}

	public JComponent createComponent()
	{
		final Module module = getModule();

		return RLoadPathChooserUtil.createLoadPathPanel(module, myLoadPathDirsCopyRef, myJRubyFacetConfiguration.getLoadPathDirs());
	}

	public boolean isModified()
	{
		final CheckableDirectoriesContainer origLoadPathDirs = myJRubyFacetConfiguration.getLoadPathDirs();

		return RLoadPathChooserUtil.loadPathDirsAreModified(myLoadPathDirsCopyRef.get(), origLoadPathDirs);
	}

	@NotNull
	private Module getModule()
	{
		final Module module = myEditorContext.getModule();
		assert module != null;
		return module;
	}

	@Override
	public void apply() throws ConfigurationException
	{
		if(isModified() && myLoadPathDirsCopyRef.get() != null)
		{
			myJRubyFacetConfiguration.setLoadPathDirs(myLoadPathDirsCopyRef.get());

			//Applay new load path to cache
			final Module module = getModule();
			SymbolsCache.getInstance(module.getProject()).clearCachesExceptBuiltIn();
		}
	}

	@Nullable
	public Icon getIcon()
	{
		return RubyIcons.RUBY_MODULE_SETTINGS_LOADPATH;
	}

	@Override
	public void reset()
	{
	}

	public void disposeUIResources()
	{
		//Do nothing
	}
}