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

package org.jetbrains.plugins.ruby.rails.module.view;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RComponents;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 22, 2008
 */


@State(
		name = "RailsViewFoldersManager",
		storages = {
				@Storage(
						file = "$MODULE_FILE$"
				)
		}
)

/**
 * Manages custom folders for RailsView
 */
public class RailsViewFoldersManager implements PersistentStateComponent<Element>
{
	protected static final String URL_ATTR = "url";
	private static final String USER_URLS = "USER_URLS";

	private Set<String> myViewUserUrls = Collections.synchronizedSet(new HashSet<String>());

	public static RailsViewFoldersManager getInstance(@NotNull final Module module)
	{
		return ModuleServiceManager.getService(module, RailsViewFoldersManager.class);
	}

	@NonNls
	@NotNull
	public String getComponentName()
	{
		return RComponents.RAILS_VIEW_FOLDERS_MANAGER;
	}

	public Set<String> getRailsViewUserFolderUrls()
	{
		return Collections.unmodifiableSet(myViewUserUrls);
	}

	public void setRailsViewUserFolderUrls(@NotNull final List<String> urls)
	{
		myViewUserUrls.clear();
		myViewUserUrls.addAll(urls);
	}

	public void readExternal(final Element element)
	{
		final List list = element.getChildren(USER_URLS);
		for(Object o : list)
		{
			myViewUserUrls.add(((Element) o).getAttribute(URL_ATTR).getValue());
		}
	}

	public void writeExternal(final Element element)
	{
		for(String url : myViewUserUrls)
		{
			final Element child = new Element(USER_URLS);
			child.setAttribute(URL_ATTR, url);

			element.addContent(child);
		}
	}

	@Override
	public Element getState()
	{
		final Element e = new Element("state");
		writeExternal(e);
		return e;
	}

	@Override
	public void loadState(final Element element)
	{
		readExternal(element);
	}
}

