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

package org.jetbrains.plugins.ruby.ruby.run.confuguration.tests;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunnerSettings;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunCommandLineState;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 19.07.2007
 */
public class RTestsRunCommandLineState extends AbstractRTestsCommandLineState {
    public static final String TEMP_FILE_PREFIX = "irplrunconf";
    public static final String TESTS_CHOOSE_METHOD_PATAMETER = "--name ";

    private final RTestsRunConfiguration myConfig;

    public RTestsRunCommandLineState(RTestsRunConfiguration config, RunnerSettings runnerSettings, ConfigurationPerRunnerSettings configurationSettings) {
        super(config, runnerSettings, configurationSettings);
        myConfig = config;
    }


    public GeneralCommandLine createCommandLine() throws ExecutionException {
        final GeneralCommandLine commandLine = createTestDefaultCmdLine(myConfig);
        
        switch (myConfig.getTestType()) {
            case ALL_IN_FOLDER:
                try {
                    // ruby.exe $TEMP_FILE
                    createRunInFolderConfScript();
                    commandLine.addParameter(tempFile.getPath());
                } catch (IOException e) {
                    tempFile = null;
                    throw new ExecutionException(RBundle.message("run.configuration.test.cant.create.tempory.file"), e);
                }
                break;
            case TEST_CLASS:
                // ruby.exe $TEMP_FILE
                try {
                    createRunClassConfScript();
                    commandLine.addParameter(tempFile.getPath());
                } catch (IOException e) {
                    tempFile = null;
                    throw new ExecutionException(RBundle.message("run.configuration.test.cant.create.tempory.file"), e);
                }
                break;
            case TEST_METHOD:
                //Method
                //ruby [$file_path] --name [$test_name]
                commandLine.addParameter(myConfig.getTestScriptPath());
                RubyRunCommandLineState.addParams(commandLine, TESTS_CHOOSE_METHOD_PATAMETER + myConfig.getTestMethodName());
                break;
            case TEST_SCRIPT:
                //Script
                //ruby [$file_path]
                commandLine.addParameter(myConfig.getTestScriptPath());
                break;
        }
        return commandLine;
    }

    private void createRunInFolderConfScript() throws IOException {
        tempFile = File.createTempFile(TEMP_FILE_PREFIX,"." + RubyFileType.RUBY.getDefaultExtension());
        tempFile.deleteOnExit();
        // Write to temp file
        final StringBuilder buff = new StringBuilder();
        buff.append("# Copyright 2000-2007 JetBrains s.r.o.\n");
        buff.append("#\n");
        buff.append("# Licensed under the Apache License, Version 2.0 (the \"License\");\n");
        buff.append("# you may not use this file except in compliance with the License.\n");
        buff.append("# You may obtain a copy of the License at\n");
        buff.append("# \n");
        buff.append("# http://www.apache.org/licenses/LICENSE-2.0\n");
        buff.append("# \n");
        buff.append("# Unless required by applicable law or agreed to in writing, software\n");
        buff.append("# distributed under the License is distributed on an \"AS IS\" BASIS,\n");
        buff.append("# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
        buff.append("# See the License for the specific language governing permissions and\n");
        buff.append("# limitations under the License.\n");
        buff.append("\n");
        buff.append("FOLDER_PATH = \"");
        buff.append(myConfig.getTestsFolderPath());
        buff.append("\"\n");
        buff.append("SEARCH_MASK = \"");
        buff.append(myConfig.getTestFileMask());
        buff.append("\" # **/*.rb\n");
        buff.append("SEPARATOR = \"=========================================\"\n");
        buff.append("puts \"Loading files.... \"\n");
        buff.append("puts SEPARATOR\n");
        buff.append("i = 1\n");
        buff.append("Dir[\"#{FOLDER_PATH}/#{SEARCH_MASK}\"].each { |testCase|\n");
        buff.append("      next if File.directory?(testCase)\n");
        buff.append("      begin\n");
        buff.append("            require testCase\n");
        buff.append("            puts \"#{i}. #{testCase}:1\"\n");
        buff.append("            i+=1\n");
        buff.append("      rescue Exception => e\n");
        buff.append("            puts \"Fail to load: #{testCase}:1\\n      Exception message: #{e}\\n        #{e.backtrace.join(\"\\n        \")}\"\n");
        buff.append("      end\n");
        buff.append("}\n");
        buff.append("puts \"#{i-1} files were loaded.\"\n");
        buff.append("puts SEPARATOR\n");
        buff.append("puts \"Searching test suites...\"\n");
        buff.append("\n");
        buff.append("i = 1\n");
        buff.append("elapsed_time = 0\n");
        buff.append("test_count = 0\n");
        buff.append("assertion_count = 0\n");
        buff.append("failure_count = 0\n");
        buff.append("error_count = 0\n");
        buff.append("require 'test/unit'\n");
        buff.append("\n");
        buff.append("ObjectSpace.each_object { |obj|\n");
        buff.append("      if (obj.kind_of?(Class) && obj.ancestors.include?(Test::Unit::TestCase) && obj != Test::Unit::TestCase)\n");
        buff.append("            require 'test/unit/ui/console/testrunner' if i == 1\n");
        buff.append("            puts SEPARATOR\n");
        buff.append("            puts \"Test suite \\##{i}: #{obj}\"\n");
        buff.append("            i+=1\n");
        buff.append("            puts SEPARATOR\n");
        buff.append("            begin_time = Time.now\n");
        buff.append("            result =  Test::Unit::UI::Console::TestRunner.run(obj)\n");
        buff.append("            elapsed_time += Time.now - begin_time\n");
        buff.append("            test_count += result.run_count\n");
        buff.append("            assertion_count += result.assertion_count\n");
        buff.append("            error_count += result.error_count\n");
        buff.append("            failure_count += result.failure_count\n");
        buff.append("      end\n");
        buff.append("}\n");
        buff.append("puts SEPARATOR\n");
        buff.append("puts \"#{i-1} test suites, #{test_count} tests, #{assertion_count} assertions, #{failure_count} failures, #{error_count} errors\"\n");
        buff.append("puts \"Finished in #{elapsed_time} seconds.\"");
        buff.append("\n");
        buff.append("at_exit do\n");
        buff.append("  Test::Unit.run = true;\n");
        buff.append("end");

        final BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
        out.write(buff.toString());
        out.close();
    }

    private void createRunClassConfScript() throws IOException {
        tempFile = File.createTempFile(TEMP_FILE_PREFIX,"." + RubyFileType.RUBY.getDefaultExtension());
        tempFile.deleteOnExit();

        final StringBuilder buff = new StringBuilder();
        buff.append("# Copyright 2000-2007 JetBrains s.r.o.\n");
        buff.append("#\n");
        buff.append("# Licensed under the Apache License, Version 2.0 (the \"License\");\n");
        buff.append("# you may not use this file except in compliance with the License.\n");
        buff.append("# You may obtain a copy of the License at\n");
        buff.append("# \n");
        buff.append("# http://www.apache.org/licenses/LICENSE-2.0\n");
        buff.append("# \n");
        buff.append("# Unless required by applicable law or agreed to in writing, software\n");
        buff.append("# distributed under the License is distributed on an \"AS IS\" BASIS,\n");
        buff.append("# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
        buff.append("# See the License for the specific language governing permissions and\n");
        buff.append("# limitations under the License.\n");
        buff.append("\n");
        buff.append("require '");
        buff.append(myConfig.getTestScriptPath());
        buff.append("'\n");
        buff.append("require 'test/unit/ui/console/testrunner'\n");
        buff.append("Test::Unit::UI::Console::TestRunner.run(");
        buff.append(myConfig.getTestQualifiedClassName());
        buff.append(")");
        // Write to temp file
        final BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
        out.write(buff.toString());
        out.close();
    }
}


