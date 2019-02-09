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

package org.jetbrains.plugins.ruby.rails.actions.generators;

import javax.swing.JComponent;

import javax.annotation.Nonnull;
import com.intellij.openapi.project.Project;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 04.12.2006
 */
public interface GeneratorPanel
{

	public void initPanel(final GeneratorOptions options);

	/**
	 * Returns generate script arguments with options.
	 * For example :
	 * "-f User login logout" for ruby generate/script controller
	 *
	 * @return script arguments
	 */
	@Nonnull
	public String getGeneratorArgs();

	/**
	 * Returns content for generate dialog(without buttons OK, Cancel).
	 * This component will be added to the generate dialog content.
	 *
	 * @return JComponent with GUI for generate dialog.
	 */
	@Nonnull
	public JComponent getContent();

	/**
	 * Sets focused component.
	 *
	 * @return component
	 */
	public JComponent getPreferredFocusedComponent();

	/**
	 * @return Data that must be checked by validator
	 */
	public String getMainArgument();

	/**
	 * Save settings on OK pressed
	 *
	 * @param project Project
	 */
	public void saveSettings(final Project project);
}
