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

package org.jetbrains.plugins.ruby.ruby.run;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 22.01.2007
 */
public class RubyScriptRunnerArgumentsProvider implements CommandLineArgumentsProvider {
    private String[] myArgsBeforeProvider;
    private String[] myArgsAfterProvider;
    private CommandLineArgumentsProvider myProvider;

    public RubyScriptRunnerArgumentsProvider(@Nullable final String[] argsBeforeProvider,
                                             @Nullable final CommandLineArgumentsProvider provider,
                                             @Nullable final String[] argsAfterProvider) {
        myArgsBeforeProvider = argsBeforeProvider;
        myArgsAfterProvider = argsAfterProvider;
        myProvider = provider;
    }

    /**
     * See  RubyScriptRunnerArgumentsProvider#collectArguments(String argumentsString, List<String> params)
     * @param argumentsString Arguments.
     * @return List of arguments
     */
    @NotNull
    public static List<String> collectArguments(@NotNull final String argumentsString) {
        return collectArguments(argumentsString, new ArrayList<String>());
    }
    

    /**
     * Splits arguments line by white spaces in array of strings.
     * E.g. "tes1 test2 --test3 'test4'" -> ["test1", "test2", "--test3", "'test4'"]
     * E.g. "tes1='p1, p2' 'something with whitespeces'" -> ["tes1='p1,", "p2'", "'something", "with", "whitespeces'"]
     * @param argumentsString Arguments.
     * @param params List with other params. Arguments will be appended to the end of this list.
     * @return List of arguments
     */
    @NotNull
    public static List<String> collectArguments(@NotNull final String argumentsString,
                                                @NotNull final List<String> params) {

        final StringTokenizer tokenizer = new StringTokenizer(argumentsString);
        while (tokenizer.hasMoreTokens()) {
            final String param = tokenizer.nextToken().trim();
            if (param.length() > 0) {
                params.add(param);
            }
        }
        return params;
    }

    @Override
	public String[] getArguments() {
        final ArrayList<String> argsList = new ArrayList<String>();
        appendToList(argsList, myArgsBeforeProvider);
        final String[] pArgs = myProvider == null ? null : myProvider.getArguments();
        appendToList(argsList, pArgs);
        appendToList(argsList, myArgsAfterProvider);
        return argsList.toArray(new String[argsList.size()]);
    }

    @Override
	public void disableParametersActions() {
        if (myProvider != null) {
            myProvider.disableParametersActions();
        }
    }

    private void appendToList(final ArrayList<String> argsList,
                              final String[] pArgs) {
        if (pArgs != null) {
            argsList.addAll(Arrays.asList(pArgs));
        }
    }
}
