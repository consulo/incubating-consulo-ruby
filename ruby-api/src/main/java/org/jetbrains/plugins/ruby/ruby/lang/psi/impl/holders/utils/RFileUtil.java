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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NonNls;

import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.RubySdkCachesManager;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyModuleFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RBaseString;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RBinaryExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 08.08.2006
 */
public class RFileUtil
{
	@NonNls
	public static final String RB_FILE_SUFFIX = "." + RubyFileType.INSTANCE.getDefaultExtension();
	@NonNls
	public static final String SO_FILE_SUFFIX = "." + "so";
	@NonNls
	public static final String FILE_DIRNAME = "File.dirname(__FILE__)";
	@NonNls
	public static final String FILE_EXPAND_PATH = "File.expand_path";
	@NonNls
	private static final String FILE_JOIN = "File.join";

	@Nonnull
	public static List<String> getUrlsByRPsiElement(@Nonnull final FileSymbol fileSymbol, @Nonnull final RVirtualFile file, final boolean relativeToDirectory, @Nonnull final RPsiElement requirement)
	{
		return findUrlsForName(fileSymbol, evaluate(file.getVirtualFile(), requirement), file, relativeToDirectory);
	}


	public static String evaluate(@Nullable final VirtualFile file, @Nullable final PsiElement requirement)
	{
		if(requirement == null)
		{
			return null;
		}

		// 'some_path'
		if(isSimpleString(requirement))
		{
			return ((RBaseString) requirement).getContent().trim();
		}

		// File.dirname(__FILE__)
		if(FILE_DIRNAME.equals(requirement.getText().replace(" ", "")))
		{
			return file != null ? getDirectoryPath(file) : null;
		}

		// 'some_path'+'some_path'
		if(requirement instanceof RBinaryExpression && ((RBinaryExpression) requirement).getOperationType() == RubyTokenTypes.tPLUS)
		{
			final String left = evaluate(file, ((RBinaryExpression) requirement).getLeftOperand());
			final String rigth = evaluate(file, ((RBinaryExpression) requirement).getRightOperand());

			if(left != null && rigth != null)
			{
				return left + rigth;
			}
		}

		// File.join(1,2,3)
		// File.expand_path(File.dirname(__FILE__) + ..)
		if(requirement instanceof RCall)
		{
			final String cmdText = ((RCall) requirement).getCommand().trim();
			if(FILE_JOIN.equals(cmdText) || FILE_EXPAND_PATH.equals(cmdText))
			{
				final StringBuilder result = new StringBuilder();
				boolean first = true;
				for(RPsiElement arg : ((RCall) requirement).getArguments())
				{
					final String s = evaluate(file, arg);
					if(s == null)
					{
						return null;
					}
					if(!first)
					{
						result.append("/");
					}
					else
					{
						first = false;
					}
					result.append(s);
				}
				return result.toString();
			}
		}
		return null;
	}

	@Nonnull
	public static List<String> findUrlsForName(@Nonnull final FileSymbol fileSymbol, @Nullable final String content)
	{
		return findUrlsForName(fileSymbol, content, null, false);
	}

	@Nonnull
	public static List<String> findUrlsForName(@Nonnull final FileSymbol fileSymbol, @Nullable final String content, @Nullable final RVirtualFile file)
	{
		return findUrlsForName(fileSymbol, content, file, false);
	}

	@Nonnull
	public static List<String> findUrlsForName(@Nonnull final FileSymbol fileSymbol, @Nullable final String content, @Nullable final RVirtualFile file, final boolean relativeToDirectory)
	{
		final ArrayList<String> urls = new ArrayList<String>();
		if(content == null)
		{
			return urls;
		}

		// The great part of requirements is written without any extensions
		urls.addAll(findUrlsForFileName(fileSymbol, file, content + RB_FILE_SUFFIX, relativeToDirectory));
		urls.addAll(findUrlsForFileName(fileSymbol, file, content + SO_FILE_SUFFIX, relativeToDirectory));
		urls.addAll(findUrlsForFileName(fileSymbol, file, content, relativeToDirectory));

		return urls;
	}

	@Nonnull
	private static List<String> findUrlsForFileName(@Nonnull final FileSymbol fileSymbol, @Nullable final RVirtualFile file, @Nonnull String name, final boolean relativeToDirectory)
	{
		final ArrayList<String> urls = new ArrayList<String>();

		final VirtualFile vFile = file != null ? file.getVirtualFile() : null;
		final VirtualFile parent = vFile != null ? vFile.getParent() : null;

		// Try to find absolutely name
		if(relativeToDirectory && parent != null)
		{
			if(name.indexOf('/') == 0)
			{
				name = name.substring(1);
			}
			final VirtualFile child = parent.findFileByRelativePath(name);
			if(child != null)
			{
				urls.add(child.getUrl());
			}
		}
		if(!relativeToDirectory)
		{
			// Try to find file by absolute name
			final VirtualFile absFile = VirtualFileUtil.findFileByLocalPath(name);
			if(absFile != null)
			{
				urls.add(absFile.getUrl());
			}
			else
			{
				// LOAD_PATH and Current directory
				if(parent != null)
				{
					final VirtualFile child = parent.findFileByRelativePath(name);
					if(child != null)
					{
						urls.add(child.getUrl());
					}
				}
				for(VirtualFile rootFile : fileSymbol.getLoadPathFiles())
				{
					final VirtualFile child = rootFile.findFileByRelativePath(name);
					if(child != null)
					{
						urls.add(child.getUrl());
					}
				}
			}
		}
		return urls;
	}

	private static String getDirectoryPath(@Nonnull final VirtualFile file)
	{
		//noinspection ConstantConditions
		return file.getParent().getPath();
	}

	private static boolean isSimpleString(PsiElement requirement)
	{
		return requirement instanceof RBaseString && ((RBaseString) requirement).getExpressionSubstitutions().size() == 0;
	}


	@Nullable
	public static Module getModule(@Nonnull final Project project, @Nonnull VirtualFile file)
	{
		return ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(file);
	}


	@Nullable
	public static Sdk getSdk(@Nonnull final Project project, @Nonnull VirtualFile file)
	{
		Sdk mySdk = getSdkByModule(project, file);
		if(mySdk == null)
		{
			mySdk = tryToFindSdk(project, file);
		}
		return mySdk;
	}

	private static Sdk getSdkByModule(@Nonnull final Project project, @Nonnull VirtualFile file)
	{
		final Module module = getModule(project, file);
		return module != null ? RModuleUtil.getModuleOrJRubyFacetSdk(module) : null;
	}

	private static Sdk tryToFindSdk(@Nonnull final Project project, @Nonnull final VirtualFile file)
	{
		// in tests cachesManager isn`t loaded
		final RubySdkCachesManager cachesManager = RubySdkCachesManager.getInstance(project);
		if(cachesManager != null)
		{
			return cachesManager.getFirstSdkForFile(file);
		}
		return null;
	}

	@Nonnull
	public static String getCurrentDirUrl(@Nonnull final VirtualFile file)
	{
		// Add own directory
		final VirtualFile parent = file.getParent();
		assert parent != null;
		return parent.getUrl();
	}

	@Nonnull
	public static List<String> getAvailableRequiresUrls(@Nonnull final FileSymbol fileSymbol, @Nonnull final VirtualFile file, final boolean relativeToDirectory)
	{
		final RubyFilesCache[] caches = fileSymbol.getCaches();

		final ArrayList<String> list = new ArrayList<String>();
		if(!relativeToDirectory)
		{
			// LOAD_PATH
			for(VirtualFile root : fileSymbol.getLoadPathFiles())
			{
				list.addAll(getRelativeUrls(caches, root, true));
			}
			// Current directory
			final VirtualFile parent = file.getParent();
			if(parent != null)
			{
				list.addAll(getRelativeUrls(caches, parent, false));
			}
		}
		else
		{
			for(String s : getRelativeUrls(caches, file, false))
			{
				list.add('/' + s);
			}
		}
		return list;
	}

	private static List<String> getRelativeUrls(@Nonnull final RubyFilesCache[] caches, @Nonnull final VirtualFile file, boolean onlyDirectoryFiles)
	{
		final ArrayList<String> requires = new ArrayList<String>();
		final VirtualFile dir = file.isDirectory() ? file : file.getParent();
		for(RubyFilesCache cache : caches)
		{
			if(cache instanceof RubyModuleFilesCache)
			{
				requires.addAll(((RubyModuleFilesCache) cache).getAllRelativeUrlsForDirectory(dir, onlyDirectoryFiles));
			}
			else if(cache != null)
			{
				requires.addAll(cache.getAllRelativeUrlsForDirectory(dir));
			}
		}
		return requires;
	}

}
