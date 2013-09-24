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

package org.jetbrains.plugins.ruby.ruby.ri;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.util.Ref;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.gems.GemUtil;
import org.jetbrains.plugins.ruby.addins.gems.GemsRunner;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.Output;
import org.jetbrains.plugins.ruby.ruby.run.Runner;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 2, 2006
 */
class RIUtil {
    public static final String WRONG_JDK_OR_RI_MESSAGE = "<font color=\"red\">" + RBundle.message("ruby.ri.wrong.project.jdk.prompt") + "</font>";
    public static final String VERSION = "--version";

    @NonNls
    private static final String RI = "ri";
    @NonNls
    private static final String DIRECTLY_TO_STDOUT = "--no-pager";
    @NonNls
    private static final String FORMAT = "--format";
    @NonNls
    private static final String WIDTH = "--width";
    @NonNls
    private static final String PLAIN = "plain";
    @NonNls
    private static final String USE_DIRECTORY = "--doc-dir";

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Hardcoded ri strings!!! DO NOT MODIFY!!!
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @NonNls
    private static final String CLASS_METHODS = "Class methods:\n--------------\n";
    @NonNls
    private static final String INSTANCE_METHODS = "Instance methods:\n-----------------\n";
    @NonNls
    private static final String ATTRIBUTES = "Attributes:\n";
    @NonNls
    private static final String MULTIPLE_DOCS = "More than one method matched your request. You can refine\nyour search by asking for information on one of:";

    /**
     * Looks for name using ri
     *
     * @param project        Current project
     * @param jdk            Current project sdk
     * @param item           item to look for
     * @param doUseDefaults  use defaults ri directories to search documentation or user defined
     * @param docDirectories user defined directories
     * @param displayWidth   width of info pane
     * @return Search result
     */
    public static String lookup(@NotNull final Project project,
                                @Nullable final ProjectJdk jdk,
                                @NotNull final String item,
                                final boolean doUseDefaults,
                                @NotNull final String[] docDirectories,
                                final int displayWidth) {
        final Ref<String> result = new Ref<String>();
// Running add with indefinite progressBar
        final String progressTitle = RBundle.message("ruby.ri.search.title", item);

        if (!RubySdkUtil.isKindOfRubySDK(jdk)) {
            return WRONG_JDK_OR_RI_MESSAGE;
        }
        ArrayList<String> args = new ArrayList<String>();
        args.add(DIRECTLY_TO_STDOUT);
        args.add(FORMAT);
        args.add(PLAIN);
        args.add(WIDTH);
        args.add(String.valueOf(displayWidth));
        args.add(item);
        if (!doUseDefaults) {
            for (String directory : docDirectories) {
                args.add(USE_DIRECTORY);
                args.add(directory);
            }
        }

        final Output output = getRiOutput(jdk, project, progressTitle, args.toArray(new String[args.size()]));
        if (output.getStderr().length() != 0) {
            return RBundle.message("ruby.ri.error") + "\n" + output.getStderr();
        }

        final String parsedRDoc = processRiOutput(output, item);
        result.set(parsedRDoc);

        return result.get();
    }

    /**
     * Parses RI output and sets links to code
     *
     * @param output     returned by RI command
     * @param lookupItem lookupItem
     * @return resulting string
     */
    public static String processRiOutput(@NotNull final Output output, @NotNull final String lookupItem) {
        final String outStr = TextUtil.replaceEOLS(output.getStdout());

        final StringBuilder sb = new StringBuilder("<pre>");

        if (outStr.indexOf(CLASS_METHODS) != -1) {
            processClassMethods(sb, outStr, lookupItem);
            if (outStr.indexOf(INSTANCE_METHODS) != -1) {
                processInstanceMethods(sb, outStr, lookupItem);
            }
            if (outStr.indexOf(ATTRIBUTES) != -1) {
                processAttributes(sb, outStr);
            }
        } else if (outStr.indexOf(INSTANCE_METHODS) != -1) {
            processInstanceMethods(sb, outStr, lookupItem);
            if (outStr.indexOf(ATTRIBUTES) != -1) {
                processAttributes(sb, outStr);
            }
        } else if (outStr.indexOf(MULTIPLE_DOCS) != -1) {
            processMultipleRefinements(sb, outStr);
        } else {
            sb.append(outStr);
        }

        return sb.append("</pre>").toString();
    }

    private static void processMultipleRefinements(@NotNull final StringBuilder sb, @NotNull final String outStr) {
        sb.append(MULTIPLE_DOCS).append("\n\n");
        int startPos = outStr.indexOf(MULTIPLE_DOCS) + MULTIPLE_DOCS.length();
        int endPos = outStr.length();
        String csvStr = outStr.substring(startPos, endPos).trim();
        String[] items = csvStr.split(",");
        for (String item : items) {
            item = trimAndEscape(item);
            if (item.length() > 0) {
                sb.append("    <a href='")
                        .append(item)
                        .append("'>")
                        .append(item)
                        .append("</a>\n");
            }
        }
    }

    private static void processClassMethods(@NotNull final StringBuilder sb, @NotNull final String outStr,
                                            @NotNull final String lookupItem) {
        int classMethodsPos = outStr.indexOf(CLASS_METHODS);
        sb.append(outStr.substring(0, classMethodsPos));
        sb.append(CLASS_METHODS);

        int startPos = classMethodsPos + CLASS_METHODS.length();
        int endPos;
        if (outStr.indexOf(INSTANCE_METHODS) != -1) {
            endPos = outStr.indexOf(INSTANCE_METHODS);
        } else if (outStr.indexOf(ATTRIBUTES) != -1) {
            endPos = outStr.indexOf(ATTRIBUTES);
        } else {
            endPos = outStr.length();
        }

        String csvMethods = outStr.substring(startPos, endPos);
        csvToLinkedMethodList(sb, csvMethods.trim(), lookupItem, '.');
    }

    private static void processInstanceMethods(@NotNull final StringBuilder sb, @NotNull final String outStr,
                                               @NotNull final String lookupItem) {
        int instanceMethodsPos = outStr.indexOf(INSTANCE_METHODS);
        sb.append("\n\n").append(INSTANCE_METHODS);
        int startPos = instanceMethodsPos + INSTANCE_METHODS.length();
        int endPos;
        if (outStr.indexOf(ATTRIBUTES) != -1) {
            endPos = outStr.indexOf(ATTRIBUTES);
        } else {
            endPos = outStr.length();
        }
        String methodsStr = outStr.substring(startPos, endPos);
        csvToLinkedMethodList(sb, methodsStr, lookupItem, '.');
    }

    private static void processAttributes(@NotNull final StringBuilder sb,
                                          @NotNull final String outStr) {
        sb.append("\n\n").append(ATTRIBUTES);
        int attrPost = outStr.indexOf(ATTRIBUTES);
        String attrStr = outStr.substring(attrPost + ATTRIBUTES.length(), outStr.length());
        sb.append(attrStr);
    }

    private static StringBuilder csvToLinkedMethodList(@NotNull final StringBuilder sb,
                                                       @NotNull final String csvStr,
                                                       @NotNull final String lookupItem,
                                                       final char riDelim) {
        String[] methods = csvStr.split(",");
        for (String method : methods) {
            method = trimAndEscape(method);
            if (method.length() > 0) {
                sb.append("    <a href='")
                        .append(lookupItem)
                        .append(riDelim)
                        .append(method)
                        .append("'>")
                        .append(method)
                        .append("</a>\n");
            }
        }
        return sb;
    }

    private static String trimAndEscape(@NotNull final String str) {
        String out = str.trim();
        if (out.indexOf("<<") != -1) {
            out = out.replaceAll("<<", "&lt;&lt;");
        }
        return out;
    }


    /**
     * Returns RI output by arguments
     *
     * @param jdk           Ruby SDK
     * @param project       Project
     * @param progressTitle Progress title
     * @param arguments     command line arguments @return Output of ri command
     * @return Output
     */
    public static Output getRiOutput(final ProjectJdk jdk, final Project project,
                                     final String progressTitle,
                                     final String... arguments) {
        final Runner.ModalProgressMode mode = new Runner.ModalProgressMode(progressTitle);
        return GemsRunner.runGemsExecutableScript(jdk, project, RI, null, mode, false, null, arguments);
    }

    public static boolean checkIfRiExists(ProjectJdk jdk) {
        //It seems that RI is located in gems bin directory
        return RubySdkUtil.isKindOfRubySDK(jdk) && GemUtil.isGemExecutableRubyScriptExists(jdk, RI);
    }
}
