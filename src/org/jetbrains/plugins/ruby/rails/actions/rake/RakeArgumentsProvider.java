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

package org.jetbrains.plugins.ruby.rails.actions.rake;

import com.intellij.openapi.actionSystem.AnAction;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.CommandLineArgumentsProvider;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
*
* @author: Roman Chernyatchik
* @date: 25.03.2007
*/
class RakeArgumentsProvider implements CommandLineArgumentsProvider {
    private AnAction[] myActions;
    private final String[] myBeforeArgs;
    private String[] myAfterArgs;

    public RakeArgumentsProvider(final String[] beforeArgs, final String[] afterArgs) {
        myBeforeArgs = beforeArgs;
        myAfterArgs = afterArgs;
        myActions = new AnAction[]{
                new RakeCmdParamAction("--trace", " Turn on invoke/execute tracing, enable full backtrace.", RailsIcons.RAKE_PARAMS_TRACE),
                new RakeCmdParamAction("--dry-run", "Do a dry run without executing actions.", RailsIcons.RAKE_PARAMS_DRY_RUN),
                new RakeCmdParamAction("--nosearch", "Do not search parent directories for the Rakefile.", RailsIcons.RAKE_PARAMS_NOSEARCH),
                new RakeCmdParamAction("--prereqs", "Display the tasks and dependencies, then exit.", RailsIcons.RAKE_PARAMS_PREREQS),
//                    new RakeCmdParamAction("--libdir=LIBDIR", "Include LIBDIR in the search path for required modules.", RailsIcons.RAKE_PARAMS_LIBDIR),
//                    new RakeCmdParamAction("--rakelibdir=RAKELIBDIR", "Auto-import any .rake files in RAKELIBDIR. (default is 'rakelib')", RailsIcons.RAKE_PARAMS_AUTOIMPORT),
//                    new RakeCmdParamAction("--require=MODULE", "Require MODULE before executing rakefile.", RailsIcons.RAKE_PARAMS_MODULE),
//                    new RakeCmdParamAction("--usage", "Display usages.", RailsIcons.RAKE_PARAMS_USAGE),
        };
    }

    public String[] getArguments() {
        ArrayList<String> argsList = new ArrayList<String>();
        argsList.addAll(Arrays.asList(myBeforeArgs));

        addRakeParams(argsList);

        argsList.addAll(Arrays.asList(myAfterArgs));
        return argsList.toArray(new String[argsList.size()]);
    }

    public void disableParametersActions() {
        for (AnAction action : myActions) {
            if (action instanceof RakeCmdParamAction) {
                ((RakeCmdParamAction)action).disableAction();
            }
        }
    }

    private void addRakeParams(final ArrayList<String> argsList) {
        for (AnAction action : myActions) {
            if (action instanceof RakeCmdParamAction) {
                final String cmd = ((RakeCmdParamAction)action).getMyCmdArgument();
                if (!TextUtil.isEmpty(cmd)) {
                    argsList.add(cmd);
                }
            }
        }
    }


    public AnAction[] getActions() {
        return myActions;
    }
}
