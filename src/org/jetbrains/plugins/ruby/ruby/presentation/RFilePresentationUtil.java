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

package org.jetbrains.plugins.ruby.ruby.presentation;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import com.intellij.ide.IconDescriptorUpdaters;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.navigation.ItemPresentation;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 29.10.2006
 */
public class RFilePresentationUtil
{
	/**
	 * @param file Ruby File
	 * @return RubyFile icon
	 */
	public static Icon getIconByRFile(final RFile file)
	{
		return IconDescriptorUpdaters.getIcon(file, 0);
	}


	/**
	 * Computes icon for RVirtualFile.
	 *
	 * @return Icon
	 */
	public static Icon getIcon()
	{
		return RubyFileType.INSTANCE.getIcon();
	}

	@NotNull
	public static ItemPresentation getPresentation(final RVirtualFile rFile)
	{
		final Icon icon = getIcon();
		return new PresentationData(rFile.getName(), TextUtil.wrapInParens(getLocation(rFile)), icon, icon, null);
	}

	private static String getLocation(@NotNull final RVirtualFile rFile)
	{
		return RContainerPresentationUtil.getLocation(rFile);
	}
}
