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

package org.jetbrains.plugins.ruby.rails.facet.configuration;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 15, 2008
 */
public class StandardRailsPaths
{

	private String modelRootURL;
	private String helpersRootURL;
	private String viewsRootURL;
	private String sharedPartialsRootURL;
	private String controllersRootURL;
	private String componentsRootURL;
	private String testsRootURL;
	private String layoutsRootURL;
	private String apisRootURL;
	private String servicesRootURL;
	private String scriptsRootURL;
	private String libsRootURL;
	private String pluginsRootURL;
	private String vendorRootURL;
	private String appRootURL;
	private String configRootURL;
	private String testMockEnviromentRootURL;
	private String migrationsRootURL;
	private String edgeRailsRootURL;
	private String publicRootURL;
	private String stylesheetsRootURL;
	private String javascriptsRootURL;
	private String imagesRootURL;

	private String railsApplicationHomeDirURL;

	public StandardRailsPaths(final String railsProjectModuleRootPath)
	{
		railsApplicationHomeDirURL = VirtualFileUtil.constructLocalUrl(railsProjectModuleRootPath);

		final String url = railsApplicationHomeDirURL + VirtualFileUtil.VFS_PATH_SEPARATOR;

		controllersRootURL = url + RailsConstants.CONTROLLERS_PATH;
		modelRootURL = url + RailsConstants.MODEL_PATH;
		helpersRootURL = url + RailsConstants.HELPERS_PATH;
		viewsRootURL = url + RailsConstants.VIEWS_PATH;
		componentsRootURL = buildComponentsPath(url);
		sharedPartialsRootURL = url + RailsConstants.SHARED_PARTIALS_PATH;
		testsRootURL = url + RailsConstants.TESTS_PATH;
		layoutsRootURL = url + RailsConstants.LAYOUTS_PATH;
		migrationsRootURL = url + RailsConstants.MIGRATIONS_PATH;

		appRootURL = url + RailsConstants.APP_PATH;
		apisRootURL = url + RailsConstants.APIS_PATH;
		servicesRootURL = url + RailsConstants.SERVICES_PATH;
		scriptsRootURL = url + RailsConstants.SCRIPTS_PATH;
		libsRootURL = buildLibPath(url);
		pluginsRootURL = buildPluginsPath(url);
		vendorRootURL = url + RailsConstants.VENDOR_PATH;
		configRootURL = url + RailsConstants.CONFIG_PATH;
		publicRootURL = url + RailsConstants.PUBLIC_FOLDER;
		stylesheetsRootURL = url + RailsConstants.PUBLIC_STYLESHEETS_PATH;
		javascriptsRootURL = url + RailsConstants.PUBLIC_JAVASCRIPTS_PATH;
		imagesRootURL = url + RailsConstants.PUBLIC_IMAGES_PATH;
		testMockEnviromentRootURL = url + RailsConstants.TEST_MOCKS_ENVIROMENT_PATH;
		edgeRailsRootURL = buildEdgeRailsPath(url);
	}

	@Nonnull
	public String getControllerRootURL()
	{
		return controllersRootURL;
	}

	@Nonnull
	public String getApisRootURL()
	{
		return apisRootURL;
	}

	@Nonnull
	public String getServicesRootURL()
	{
		return servicesRootURL;
	}

	@Nonnull
	public String getScriptsRootURL()
	{
		return scriptsRootURL;
	}

	@Nonnull
	public String getVendorRootURL()
	{
		return vendorRootURL;
	}

	@Nonnull
	public String getLibsRootURL()
	{
		return libsRootURL;
	}

	@Nonnull
	public String getPluginsRootURL()
	{
		return pluginsRootURL;
	}

	@Nonnull
	public String getEdgeRailsRootURL()
	{
		return edgeRailsRootURL;
	}

	@Nonnull
	public String getComponentsRootURL()
	{
		return componentsRootURL;
	}

	@Nonnull
	public String getModelRootURL()
	{
		return modelRootURL;
	}

	@Nonnull
	public String getViewsRootURL()
	{
		return viewsRootURL;
	}

	@Nonnull
	public String getDefaultSharedPartialsRootURL()
	{
		return sharedPartialsRootURL;
	}

	@Nonnull
	public String getHelpersRootURL()
	{
		return helpersRootURL;
	}

	/**
	 * @return Url for standart folder test in RailsApplication
	 */
	@Nonnull
	public String getTestsStdUnitRootURL()
	{
		return testsRootURL;
	}

	@Nonnull
	public String getLayoutsRootURL()
	{
		return layoutsRootURL;
	}

	public String getPublicRootURL()
	{
		return publicRootURL;
	}

	public String getStylesheetsRootURL()
	{
		return stylesheetsRootURL;
	}

	public String getJavascriptsRootURL()
	{
		return javascriptsRootURL;
	}

	public String getImagesRootURL()
	{
		return imagesRootURL;
	}

	@Nonnull
	public String getRailsApplicationRootURL()
	{
		return railsApplicationHomeDirURL;
	}

	@Nonnull
	public String getAppRootURL()
	{
		return appRootURL;
	}

	@Nonnull
	public String getConfigRootURL()
	{
		return configRootURL;
	}

	@Nonnull
	public String getTestMockEnviromentRootURL()
	{
		return testMockEnviromentRootURL;
	}

	@Nonnull
	public String getMigrationsRootURL()
	{
		return migrationsRootURL;
	}

	public static String buildComponentsPath(@Nonnull final String railsRootUrl)
	{
		return VirtualFileUtil.buildUrl(railsRootUrl, RailsConstants.COMPONENTS_PATH);
	}

	public static String buildDBPath(@Nonnull final String railsRootUrl)
	{
		return VirtualFileUtil.buildUrl(railsRootUrl, RailsConstants.DB_PATH);
	}

	public static String buildLibPath(@Nonnull final String railsRootUrl)
	{
		return VirtualFileUtil.buildUrl(railsRootUrl, RailsConstants.LIB_PATH);
	}

	public static String buildEdgeRailsPath(@Nonnull final String railsRootUrl)
	{
		return VirtualFileUtil.buildUrl(railsRootUrl, RailsConstants.EDGE_RAILS_PATH);
	}

	public static String buildPluginsPath(@Nonnull final String railsRootUrl)
	{
		return VirtualFileUtil.buildUrl(railsRootUrl, RailsConstants.PLUGINS_PATH);
	}

	public static String buildJavaScriptsPath(@Nonnull final String railsRootUrl)
	{
		return VirtualFileUtil.buildUrl(railsRootUrl, RailsConstants.PUBLIC_JAVASCRIPTS_PATH);
	}

	public static String buildStyleSheetsPath(@Nonnull final String railsRootUrl)
	{
		return VirtualFileUtil.buildUrl(railsRootUrl, RailsConstants.PUBLIC_STYLESHEETS_PATH);
	}

	public static String buildTmpPath(@Nonnull final String railsRootUrl)
	{
		return VirtualFileUtil.buildUrl(railsRootUrl, RailsConstants.TMP_PATH);
	}
}