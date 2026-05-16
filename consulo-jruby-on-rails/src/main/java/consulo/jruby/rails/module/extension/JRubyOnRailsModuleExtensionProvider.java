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

package consulo.jruby.rails.module.extension;

import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.content.layer.ModuleExtensionProvider;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.ModuleExtension;
import consulo.module.extension.MutableModuleExtension;
import consulo.ruby.api.icon.RubyApiIconGroup;
import consulo.ui.image.Image;
import jakarta.annotation.Nullable;

@ExtensionImpl
public class JRubyOnRailsModuleExtensionProvider implements ModuleExtensionProvider<JRubyOnRailsModuleExtension>
{
	@Override
	public String getId()
	{
		return "jruby-on-rails";
	}

	@Nullable
	@Override
	public String getParentId()
	{
		return "jruby";
	}

	@Override
	public LocalizeValue getName()
	{
		return LocalizeValue.localizeTODO("Rails");
	}

	@Override
	public Image getIcon()
	{
		return RubyApiIconGroup.railsRails();
	}

	@Override
	public ModuleExtension<JRubyOnRailsModuleExtension> createImmutableExtension(ModuleRootLayer moduleRootLayer)
	{
		return new JRubyOnRailsModuleExtension(getId(), moduleRootLayer);
	}

	@Override
	public MutableModuleExtension<JRubyOnRailsModuleExtension> createMutableExtension(ModuleRootLayer moduleRootLayer)
	{
		return new JRubyOnRailsMutableModuleExtension(getId(), moduleRootLayer);
	}
}
