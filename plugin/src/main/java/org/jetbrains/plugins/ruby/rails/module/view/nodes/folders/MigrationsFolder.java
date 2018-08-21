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

package org.jetbrains.plugins.ruby.rails.module.view.nodes.folders;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleNode;
import consulo.awt.TargetAWT;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.BDSchemaNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.SimpleFileNode;
import org.jetbrains.plugins.ruby.rails.nameConventions.MigrationsConventions;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 19.05.2007
 */
public class MigrationsFolder extends FolderNode
{
	private static final String MIGRATIONS_FOLDER = RBundle.message("rails.project.module.view.nodes.migrations.presentable");

	public MigrationsFolder(final Module module, final VirtualFile dir, final SimpleNode parent)
	{
		super(module, dir, parent, initPresentationData());
	}

	private static PresentationData initPresentationData()
	{
		return new PresentationData(MIGRATIONS_FOLDER, MIGRATIONS_FOLDER, TargetAWT.to(RailsIcons.RAILS_MIGRATIONS_CLOSED), null);
	}

	@Override
	public SimpleNode[] getChildren()
	{
		final VirtualFile migrDir = getVirtualFile();
		assert migrDir != null;
		final Module module = getModule();

		//Migrations
		final Set<VirtualFile> allFiles = new HashSet<VirtualFile>();
		RubyVirtualFileScanner.addRubyFiles(migrDir, allFiles);

		final ArrayList<SimpleNode> children = new ArrayList<SimpleNode>();
		for(VirtualFile file : allFiles)
		{
			children.add(new SimpleFileNode(module, file));
		}

		//schema.rb
		final VirtualFile schema = MigrationsConventions.getSchema(migrDir);
		if(schema != null)
		{
			children.add(new BDSchemaNode(module, schema));
		}
		return children.toArray(new SimpleNode[children.size()]);
	}

	@Override
	@NotNull
	public RailsProjectNodeComparator.NodeType getType()
	{
		return RailsProjectNodeComparator.NodeType.MIGRATION;
	}
}
