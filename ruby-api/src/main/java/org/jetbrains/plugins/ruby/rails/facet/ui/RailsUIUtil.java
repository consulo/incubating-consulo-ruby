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

package org.jetbrains.plugins.ruby.rails.facet.ui;

import javax.swing.JLabel;

import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsUtil;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.tabs.EvaluatingComponent;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.util.Function;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 25, 2008
 */
public class RailsUIUtil
{
	public static void setupRailsVersionEvaluator(final Sdk sdk, final JLabel railsVersionLabel, final EvaluatingComponent<String> ecRailsVersionLabel, final RailsVersionComponent componet)
	{
		//noinspection ConstantConditions
		if(sdk == null)
		{
			railsVersionLabel.setText(RBundle.message("sdk.no.specified"));
		}
		else if(!RailsUtil.hasRailsSupportInSDK(sdk))
		{
			railsVersionLabel.setText(RBundle.message("sdk.error.no.rails.found"));
		}
		else
		{
			ecRailsVersionLabel.setHanlders(
					// Hides original lable text
					new Runnable()
					{
						@Override
						public void run()
						{
							railsVersionLabel.setText("");
						}
					},
					// Evaluates Rails SDK version
					new Function<Object, String>()
					{
						@Override
						public String fun(final Object o)
						{
							return RailsUtil.getRailsVersion(sdk, false, new Function<Object, Boolean>()
							{
								@Override
								public Boolean fun(final Object o)
								{
									// Cancel process if form was closed
									return componet.isCloosed();
								}
							});
						}
					},
					// Sets found SDK version
					new Function<String, Object>()
					{
						@Override
						public Object fun(final String vers)
						{
							componet.setRailsVersion(vers);
							railsVersionLabel.setText(TextUtil.isEmpty(vers) ? RBundle.message("sdk.error.rails.unknown.verson") : vers);

							return null;
						}
					}, RBundle.message("common.msgs.fetching.version")
			);

			// Starts SDK version evaluator
			ecRailsVersionLabel.run();
		}
	}

	public interface RailsVersionComponent
	{
		public boolean isCloosed();

		public void setRailsVersion(@Nullable final String railsVersion);
	}
}
