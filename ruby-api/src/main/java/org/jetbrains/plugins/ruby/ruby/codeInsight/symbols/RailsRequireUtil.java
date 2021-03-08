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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.rails.RailsUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.StandardRailsPaths;
import org.jetbrains.plugins.ruby.rails.nameConventions.ApisConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.GeneralConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.HelpersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.MailersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.MigrationsConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.ModelsConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.TestsConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.ViewsConventions;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.FileSymbolType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RFileUtil;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 21, 2007
 */
public class RailsRequireUtil
{
	private static final Logger LOG = Logger.getInstance(RailsRequireUtil.class.getName());

	/**
	 * Gathers rails specified symbol for rails modules
	 *
	 * @param fileSymbol FileSymbol
	 * @param rFile      Current ruby file
	 * @param fileUrl    Url of the file
	 * @param module     Module for file - rails module
	 */
	public static void requireRailsFiles(@Nullable final FileSymbol fileSymbol, @Nonnull final RVirtualFile rFile, @Nonnull final String fileUrl, @Nonnull final Module module)
	{
		if(fileSymbol == null)
		{
			return;
		}

		final ArrayList<VirtualFile> files = new ArrayList<VirtualFile>();

		final VirtualFileManager manager = VirtualFileManager.getInstance();
		final VirtualFile rFileVirtual = rFile.getVirtualFile();
		assert rFileVirtual != null;

		final String rVFileName = rFileVirtual.getNameWithoutExtension();
		final String parentDir = VirtualFileUtil.getParentDir(fileUrl);
		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for modules with Rails Support
		// Controller
		if(ControllersConventions.isControllerFile(rFile, module))
		{
			// loads ApplicationController
			addIfNotNull(files, ControllersConventions.getApplicationControllerFile(module));

			// load all controllers and helpers
			loadAllControllersAndHelpers(railsPaths, files, manager);

			//loads helper for controller
			loadHelperForController(module, files, manager, rFileVirtual, parentDir);
		}
		else
			// Helper
			if(HelpersConventions.isHelperFile(rFile, module))
			{

				// load all controllers and helpers
				loadAllControllersAndHelpers(railsPaths, files, manager);

				// loads controller for helper
				final String cName = ControllersConventions.getControllerNameByHelperFileName(rVFileName);
				final String contrUrl = ControllersConventions.getControllerURL(parentDir, cName, module);
				if(contrUrl != null)
				{
					addIfNotNull(files, manager.findFileByUrl(contrUrl));
				}

				// loads ApplicationHelper
				addIfNotNull(files, HelpersConventions.getApplicationHelperFile(module));
			}
			else
				// View
				if(ViewsConventions.isRHTMLOrRJSViewFile(rFile, module))
				{

					// loads ApplicationController
					addIfNotNull(files, ControllersConventions.getApplicationControllerFile(module));

					// loads ApplicationHelper
					addIfNotNull(files, HelpersConventions.getApplicationHelperFile(module));

					// load all controllers and helpers
					loadAllControllersAndHelpers(railsPaths, files, manager);

					final String controllerUrl = ControllersConventions.getControllerUrlByViewUrl(fileUrl, railsPaths);
					final VirtualFile controllerFile = manager.findFileByUrl(controllerUrl);
					if(controllerFile != null)
					{
						//load controller
						addIfNotNull(files, controllerFile);

						//load controller helper
						final VirtualFile contrDir = controllerFile.getParent();
						assert contrDir != null;
						final String contrDirUrl = contrDir.getUrl();
						loadHelperForController(module, files, manager, controllerFile, contrDirUrl);
					}

				}
				else
					// Model
					if(ModelsConventions.isModelFile(rFile, module))
					{
						// load all controllers and helpers
						loadAllControllersAndHelpers(railsPaths, files, manager);

						loadAllModels(railsPaths, files, manager);
					}
					else
						// Migrations
						if(MigrationsConventions.isMigrationFile(rFile, module) || TestsConventions.isTestFrameworkFile(rFile, module))
						{
							// everything is required in cache base symbol
						}

		addFilesToFilesymbol(fileSymbol, fileUrl, files);
	}

	private static void addFilesToFilesymbol(@Nonnull final FileSymbol fileSymbol, @Nullable final String fileUrl, @Nonnull final Collection<VirtualFile> files)
	{
		for(VirtualFile virtualFile : files)
		{
			final String url = virtualFile.getUrl();
			if(!url.equals(fileUrl))
			{
				FileSymbolUtil.process(fileSymbol, url, InterpretationMode.FULL, false);
			}
		}
	}


	private static void loadAllModels(@Nonnull final StandardRailsPaths railsPaths, @Nonnull final List<VirtualFile> files, @Nonnull final VirtualFileManager manager)
	{
		final String modelsRootUrl = railsPaths.getModelRootURL();
		final VirtualFile modelsRoot = manager.findFileByUrl(modelsRootUrl);
		if(modelsRoot != null)
		{
			final HashSet<VirtualFile> set = new HashSet<VirtualFile>();
			RubyVirtualFileScanner.addRubyFiles(modelsRoot, set);
			files.addAll(set);
		}
	}

	public static void loadAllModels(@Nullable final FileSymbol fileSymbol, @Nonnull final Module module)
	{
		if(fileSymbol == null)
		{
			return;
		}
		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for modules with Rails Support
		final VirtualFileManager manager = VirtualFileManager.getInstance();

		final ArrayList<VirtualFile> files = new ArrayList<VirtualFile>();
		loadAllModels(railsPaths, files, manager);
		addFilesToFilesymbol(fileSymbol, null, files);
	}

	private static void loadAllControllersAndHelpers(@Nonnull final StandardRailsPaths railsPaths, @Nonnull final List<VirtualFile> files, @Nonnull final VirtualFileManager manager)
	{
		//controllers
		final String controllersRootUrl = railsPaths.getControllerRootURL();
		final VirtualFile controllersRoot = manager.findFileByUrl(controllersRootUrl);
		if(controllersRoot != null)
		{
			final HashSet<VirtualFile> set = new HashSet<VirtualFile>();
			RubyVirtualFileScanner.addRubyFiles(controllersRoot, set);
			files.addAll(set);
		}

		//helpers
		final String helpersRootUrl = railsPaths.getHelpersRootURL();
		final VirtualFile helpersRoot = manager.findFileByUrl(helpersRootUrl);
		if(helpersRoot != null)
		{
			final HashSet<VirtualFile> set = new HashSet<VirtualFile>();
			RubyVirtualFileScanner.addRubyFiles(helpersRoot, set);
			files.addAll(set);
		}
	}

	public static void loadAllControllersAndHelpers(@Nullable final FileSymbol fileSymbol, @Nonnull final Module module)
	{
		if(fileSymbol == null)
		{
			return;
		}

		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for modules with Rails Support
		final VirtualFileManager manager = VirtualFileManager.getInstance();

		final ArrayList<VirtualFile> files = new ArrayList<VirtualFile>();
		loadAllControllersAndHelpers(railsPaths, files, manager);
		addFilesToFilesymbol(fileSymbol, null, files);
	}

	private static void loadAllLibs(@Nonnull final StandardRailsPaths railsPaths, @Nonnull final List<VirtualFile> files, @Nonnull final VirtualFileManager manager)
	{
		final String libsRootUrl = railsPaths.getLibsRootURL();
		final VirtualFile libsRoot = manager.findFileByUrl(libsRootUrl);
		if(libsRoot != null)
		{
			final HashSet<VirtualFile> set = new HashSet<VirtualFile>();
			RubyVirtualFileScanner.addRubyFiles(libsRoot, set);
			files.addAll(set);
		}
	}

	public static void loadAllLibs(@Nullable final FileSymbol fileSymbol, @Nonnull final Module module)
	{
		if(fileSymbol == null)
		{
			return;
		}

		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for modules with Rails Support
		final VirtualFileManager manager = VirtualFileManager.getInstance();

		final ArrayList<VirtualFile> files = new ArrayList<VirtualFile>();
		loadAllLibs(railsPaths, files, manager);
		addFilesToFilesymbol(fileSymbol, null, files);
	}

	private static void loadAllVendorsLibsWithoutEdgeRails(@Nonnull final StandardRailsPaths railsPaths, @Nonnull final List<VirtualFile> files, @Nonnull final VirtualFileManager manager)
	{
		final String vendorRootUrl = railsPaths.getVendorRootURL();
		final VirtualFile vendorRoot = manager.findFileByUrl(vendorRootUrl);
		if(vendorRoot != null)
		{
			final HashSet<VirtualFile> set = new HashSet<VirtualFile>();
			final String edgeRails = railsPaths.getEdgeRailsRootURL();
			addPluginsAndVendorPackagesLibFilesWithoutEdgeRails(vendorRoot, set, edgeRails);
			files.addAll(set);
		}
	}

	private static void addPluginsAndVendorPackagesLibFilesWithoutEdgeRails(final VirtualFile rootDir, final HashSet<VirtualFile> set, @Nonnull final String edgeRailsUrl)
	{
		final ArrayList<VirtualFile> folder = new ArrayList<VirtualFile>();

		RubyVirtualFileScanner.addFilesFromDirectory(rootDir, folder, false, false, true);
		for(VirtualFile file : folder)
		{
			if(file.getUrl().startsWith(edgeRailsUrl))
			{
				continue;
			}
			final VirtualFile init = file.findChild(RailsConstants.PLUGINS_INIT_FILE_PATH);
			if(init != null)
			{
				set.add(init);
			}
			final VirtualFile mainFile = file.findChild(file.getName() + "." + RubyFileType.INSTANCE.getDefaultExtension());
			if(mainFile != null)
			{
				set.add(mainFile);
			}
			final VirtualFile lib = file.findChild(RailsConstants.PLUGINS_AND_VENDORS_PACKAGES_LIB_PATH);
			if(lib != null)
			{
				RubyVirtualFileScanner.addRubyFiles(lib, set);
			}
		}
	}

	public static void loadAllVendorsLibsWithoutEdgeRails(@Nullable final FileSymbol fileSymbol, @Nonnull final Module module)
	{
		if(fileSymbol == null)
		{
			return;
		}

		final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
		assert railsPaths != null; //Not null for modules with Rails Support
		final VirtualFileManager manager = VirtualFileManager.getInstance();

		final ArrayList<VirtualFile> files = new ArrayList<VirtualFile>();
		loadAllVendorsLibsWithoutEdgeRails(railsPaths, files, manager);
		addFilesToFilesymbol(fileSymbol, null, files);
	}


	private static void loadHelperForController(final Module module, final List<VirtualFile> files, final VirtualFileManager manager, final VirtualFile controllerVFile, final String controllerDirUrl)
	{
		final String controllerName = ControllersConventions.getControllerName(controllerVFile);
		LOG.assertTrue(controllerName != null, "Controller name for controller file: " + controllerVFile + " cant be null");

		final String helperURL = HelpersConventions.getHelperURL(controllerDirUrl, controllerName, module);
		if(helperURL != null)
		{
			addIfNotNull(files, manager.findFileByUrl(helperURL));
		}
	}

	private static void addIfNotNull(@Nonnull final List<VirtualFile> files, @Nullable final VirtualFile file)
	{
		if(file != null)
		{
			files.add(file);
		}
	}

	public static void loadBuiltInHelpers(@Nullable final FileSymbol fileSymbol, @Nonnull final InterpretationMode mode)
	{
		if(fileSymbol == null)
		{
			return;
		}

		for(String actionViewFileUrl : RFileUtil.findUrlsForName(fileSymbol, HelpersConventions.ACTION_VIEW))
		{
			//load built-in helprs
			final List<VirtualFile> helpers = HelpersConventions.getBuiltInHelpers(actionViewFileUrl);
			for(VirtualFile helper : helpers)
			{
				if(helper != null)
				{
					FileSymbolUtil.process(fileSymbol, helper.getUrl(), mode, false);
				}
			}
		}
	}

	public static void loadDBAdapters(@Nullable final FileSymbol fileSymbol, @Nonnull final InterpretationMode mode)
	{
		if(fileSymbol == null)
		{
			return;
		}

		for(String activeRecordFileUrl : RFileUtil.findUrlsForName(fileSymbol, ModelsConventions.ACTIVER_RECORD))
		{
			//load built-in helpers
			final List<VirtualFile> adapters = ModelsConventions.getBuiltInAdapters(activeRecordFileUrl);
			for(VirtualFile adapter : adapters)
			{
				if(adapter != null)
				{
					FileSymbolUtil.process(fileSymbol, adapter.getUrl(), mode, false);
				}
			}
		}
	}

	public static void loadCoreExtentions(@Nullable final FileSymbol fileSymbol, @Nonnull final InterpretationMode mode)
	{
		if(fileSymbol == null)
		{
			return;
		}

		for(String activeSupportFileUrl : RFileUtil.findUrlsForName(fileSymbol, GeneralConventions.ACTIVE_SUPPORT))
		{
			//load built-in helprs
			final List<VirtualFile> files = GeneralConventions.getBuiltCoreExtention(activeSupportFileUrl);
			for(VirtualFile file : files)
			{
				if(file != null)
				{
					FileSymbolUtil.process(fileSymbol, file.getUrl(), mode, false);
				}
			}
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////// Rails cache util methods ///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Nullable
	public static FileSymbolType getRailsLayerType(@Nonnull final RVirtualFile file, @Nonnull final Module module)
	{
		// Controller
		if(ControllersConventions.isControllerFile(file, module))
		{
			return FileSymbolType.MODELS_LAYER;
		}
		// Helper or View
		if(HelpersConventions.isHelperFile(file, module) || ViewsConventions.isRHTMLOrRJSViewFile(file, module))
		{
			return FileSymbolType.MODELS_LAYER;
		}
		// Model or Migration
		if(ModelsConventions.isModelFile(file, module) || MigrationsConventions.isMigrationFile(file, module))
		{
			return FileSymbolType.LIBS_LAYER;
		}
		// Tests
		if(TestsConventions.isTestFrameworkFile(file, module))
		{
			return FileSymbolType.CONTROLLERS_AND_HELPERS_LAYER;
		}
		// ActionMailer
		if(MailersConventions.isMailerFile(file, module))
		{
			return FileSymbolType.MAILERS_LAYER;
		}
		// WebService
		if(ApisConventions.isApisFile(file, module))
		{
			return FileSymbolType.WEBSERVICES_LAYER;
		}
		return null;
	}

	public static void addRailsLoadPath(@Nullable final FileSymbol fileSymbol, @Nonnull final Module module)
	{
		if(fileSymbol == null)
		{
			return;
		}

		for(String railsRoot : getRailsAdditionalLoadPathes(module))
		{
			FileSymbolUtil.addLoadPath(fileSymbol, railsRoot);
		}
	}

	public static List<String> getRailsAdditionalLoadPathes(@Nonnull final Module myModule)
	{
		final List<String> list = new ArrayList<String>();
		// adding test/mock/environment
		addUrlIfNotNull(list, RailsUtil.getTestMockEnviromentRoot(myModule));

		// adding app
		addUrlIfNotNull(list, RailsUtil.getAppRoot(myModule));

		// adding app/services
		addUrlIfNotNull(list, RailsUtil.getServicesRoot(myModule));

		// adding app/apis
		addUrlIfNotNull(list, RailsUtil.getApisRoot(myModule));

		// adding config
		addUrlIfNotNull(list, RailsUtil.getConfigRoot(myModule));

		// adding lib
		addUrlIfNotNull(list, RailsUtil.getLibsRoot(myModule));

		// adding vendor
		addUrlIfNotNull(list, RailsUtil.getVendorRoot(myModule));

		// adding app/controllers and all of it`s subdirectories
		addRelativeDirsUnderRoot(list, RailsUtil.getControllersRoot(myModule), false);

		// adding app/models and all of it`s subdirectories with name starting with _ or lowercase letters
		addRelativeDirsUnderRoot(list, RailsUtil.getModelRoot(myModule), true);

		// adding components and all of it`s subdirectories with name starting with _ or lowercase letters
		addRelativeDirsUnderRoot(list, RailsUtil.getComponentsRoot(myModule), true);

		// adding Edge Rails folder: vendor/rails/[directory_name]/lib
		addAdditionalLoadPathLibsRoots(list, RailsUtil.getEdgeRailsRoot(myModule));

		// adding Edge Rails folder: vendor/plugins/[plugin_name]/lib
		addAdditionalLoadPathLibsRoots(list, RailsUtil.getPluginsRoot(myModule));

		// adding Edge Rails folder: vendor/vendor/[plugin_name]/lib
		addAdditionalLoadPathLibsRoots(list, RailsUtil.getVendorRoot(myModule));

		return list;
	}

	private static void addUrlIfNotNull(@Nonnull final List<String> list, @Nullable final VirtualFile file)
	{
		if(file != null)
		{
			list.add(file.getUrl());
		}
	}

	private static void addRelativeDirsUnderRoot(@Nonnull final List<String> list, @Nullable final VirtualFile root, final boolean checkName)
	{
		if(root != null)
		{
			for(VirtualFile virtualFile : RubyVirtualFileScanner.getRelativeFilesUnderRoot(root, checkName, false, true))
			{
				addUrlIfNotNull(list, virtualFile);
			}
		}
	}

	private static void addAdditionalLoadPathLibsRoots(@Nonnull final List<String> list, @Nullable final VirtualFile root)
	{
		if(root != null)
		{
			for(VirtualFile virtualFile : RubyVirtualFileScanner.findLibsSubDirectories(root.getChildren()))
			{
				addUrlIfNotNull(list, virtualFile);
			}
		}
	}

}
