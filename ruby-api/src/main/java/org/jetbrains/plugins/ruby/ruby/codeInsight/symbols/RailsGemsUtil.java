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
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 30, 2007
 */
public class RailsGemsUtil
{

	private static final String[] RAILS_GEMS = new String[]{
			"actionmailer",
			"actionpack",
			"actionwebservice",
			"activerecord",
			"activesupport",
			"rails",
			"rake"
	};

	public static List<String> getRailsGems(@Nonnull final String gemsRootUrl, @Nonnull final VirtualFile gemsRootFile)
	{
		final ArrayList<String> railsGemsUrls = new ArrayList<String>();
		for(String gemUrl : RubySdkUtil.getAllGemsLibUrls(gemsRootFile))
		{
			if(isRailsGem(gemsRootUrl, gemUrl))
			{
				railsGemsUrls.add(gemUrl);
			}
		}
		return railsGemsUrls;
	}

	/*
	 * Checks that gemUrl is rails required!
	 */
	private static boolean isRailsGem(@Nonnull final String gemsRootUrl, @Nonnull final String gemUrl)
	{
		final String gemName = gemUrl.substring(gemsRootUrl.length() + 1);
		for(String gem : RAILS_GEMS)
		{
			if(gemName.startsWith(gem))
			{
				return true;
			}
		}
		return false;
	}

}
