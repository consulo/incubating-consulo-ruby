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

package org.jetbrains.plugins.ruby.addins.rspec.run.configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecModuleSettings;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunCommandLineState;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.AbstractRTestsCommandLineState;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;


/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 19.07.2007
 */
public class RSpecRunCommandLineState extends AbstractRTestsCommandLineState
{
	public static final String TEMP_FILE_PREFIX = "irplrspecconf";
	private File tempFile;

	private final RSpecRunConfiguration myConfig;

	public RSpecRunCommandLineState(final RSpecRunConfiguration config, ExecutionEnvironment executionEnvironment)
	{
		super(config, executionEnvironment);
		myConfig = config;
	}

	public GeneralCommandLine createCommandLine() throws ExecutionException
	{
		final GeneralCommandLine commandLine = createTestDefaultCmdLine(myConfig);

		//rspec args
		final String specArgs = pathcSpecArgsWithColouringArgIfNecessary(myConfig.getSpecArgs());

		//ruby executable  path
		final Sdk sdk = myConfig.getSdk();
		assert sdk != null;
		final String rubyExecutablePath = RubySdkUtil.getVMExecutablePath(sdk);

		//rspecScriptPath
		final Module module = myConfig.getModule();
		final String rspecScriptPath;
		final boolean runSeparately = myConfig.shouldRunSpecSeparately();
		if(myConfig.shouldUseCustomSpecRunner())
		{
			//custom runner
			rspecScriptPath = myConfig.getCustomSpecsRunnerPath();
		}
		else if(module != null && !myConfig.shouldUseAlternativeSdk() && RailsFacetUtil.hasRailsSupport(module) && RSpecModuleSettings.getInstance(module).getRSpecSupportType() == RSpecModuleSettings.RSpecSupportType.RAILS_PLUGIN)
		{
			//rais rspec
			final String railsApplicationRoot = RailsFacetUtil.getRailsAppHomeDirPath(module);
			rspecScriptPath = RSpecUtil.getRailsSpecScriptPathOrUrl(railsApplicationRoot);
		}
		else
		{
			//gem
			rspecScriptPath = RSpecUtil.getRSpecGemExecutablePath(sdk);
		}

		switch(myConfig.getTestType())
		{
			case ALL_IN_FOLDER:
				try
				{
					//ruby.exe $TEMP_FILE specArgs
					createRunInFolderConfScript(rspecScriptPath, rubyExecutablePath, runSeparately);
					commandLine.addParameter(tempFile.getPath());
				}
				catch(IOException e)
				{
					tempFile = null;
					throw new ExecutionException(RBundle.message("run.configuration.test.cant.create.tempory.file"), e);
				}
				break;
			case TEST_SCRIPT:
				//Script
				//ruby spec_script [$file_path] specArgs
				commandLine.addParameter(rspecScriptPath);
				commandLine.addParameter(myConfig.getTestScriptPath());
				break;
		}
		RubyRunCommandLineState.addParams(commandLine, specArgs);
		return commandLine;
	}

	private String pathcSpecArgsWithColouringArgIfNecessary(@NotNull final String args)
	{
		if(myConfig.shouldUseColoredOutput())
		{
			if(!args.startsWith(RSpecUtil.COLOURED_COMMAND_LINE_ARG) && !args.contains(" " + RSpecUtil.COLOURED_COMMAND_LINE_ARG))
			{
				return args + " " + RSpecUtil.COLOURED_COMMAND_LINE_ARG;
			}
		}

		return args;
	}

	private void createRunInFolderConfScript(@Nullable final String rspecScriptPath, final String rubyExecutablePath, final boolean runSeparately) throws IOException
	{
		tempFile = File.createTempFile(TEMP_FILE_PREFIX, "." + RubyFileType.INSTANCE.getDefaultExtension());
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
		buff.append("WORK_DIR = ");
		final String wDirectory = myConfig.getWorkingDirectory();
		if(wDirectory != null)
		{
			buff.append("\"").append(wDirectory).append("\"\n");
		}
		else
		{
			buff.append("nil\n");
		}
		buff.append("puts WORK_DIR\n");
		buff.append("FOLDER_PATH = \"");
		buff.append(myConfig.getTestsFolderPath());
		buff.append("\"\n");
		buff.append("SEARCH_MASK = \"");
		buff.append(myConfig.getTestFileMask());
		buff.append("\" # **/*_spec.rb\n");
		buff.append("RUBY_EXECUTABLE = \"");
		buff.append(rubyExecutablePath);
		buff.append("\"\n");
		buff.append("SPEC_SCRIPT = \"");
		buff.append(rspecScriptPath);
		buff.append("\"\n");
		buff.append("LOAD_SEPARATELY = ");
		buff.append(Boolean.toString(runSeparately));
		buff.append("\n\n");
		buff.append("RUBY_ARGS = []\n");
		buff.append("ruby_args_s=\"").append(myConfig.getRubyArgs()).append("\"\n");
		buff.append("ruby_args_s.split(%r{(\\s|^)-}).each {|item| (RUBY_ARGS << \"-#{item}\") unless item.strip.empty?}\n");

		buff.append("\n");
		buff.append("SEPARATOR = \"=========================================\"\n");
		buff.append("\n");
		buff.append("def search_files\n");
		buff.append("  specs = []\n");
		buff.append("\n");
		buff.append("  if (SEARCH_MASK.strip.empty?)\n");
		buff.append("    specs << FOLDER_PATH\n");
		buff.append("  else\n");
		buff.append("    puts SEPARATOR\n");
		buff.append("    Dir[\"#{FOLDER_PATH}/#{SEARCH_MASK}\"].each { |testCase|\n");
		buff.append("    #next if File.directory?(testCase)\n");
		buff.append("      specs << testCase\n");
		buff.append("    }\n");
		buff.append("  end\n");
		buff.append("  specs\n");
		buff.append("end\n");
		buff.append("\n");
		buff.append("\n");
		buff.append("def print_filenames(files)\n");
		buff.append("  i = 1\n");
		buff.append("  files.each { |file|\n");
		buff.append("    puts \"#{i}. #{file}:1\"\n");
		buff.append("    i+=1\n");
		buff.append("  }\n");
		buff.append("  puts \"\\n#{i-1} files were found.\"\n");
		buff.append("end\n");
		buff.append("\n");
		buff.append("def generateCmdLine(files)\n");
		buff.append("  cmdLine = []\n");
		buff.append("  cmdLine << RUBY_EXECUTABLE;\n");
		buff.append("  RUBY_ARGS.each { |option|\n");
		buff.append("    cmdLine << option\n");
		buff.append("  }\n");
		buff.append("  cmdLine << SPEC_SCRIPT;\n");
		buff.append("  files.each { |file|\n");
		buff.append("    cmdLine << file\n");
		buff.append("  }\n");
		buff.append("  ARGV.each {|arg|\n");
		buff.append("    cmdLine << arg\n");
		buff.append("  }\n");
		buff.append("  puts 'Exec: ' + cmdLine.join(' ')\n");
		buff.append("  cmdLine\n");
		buff.append("end\n");
		buff.append("\n");
		buff.append("puts \"Searching files.... \"\n");
		buff.append("specs = search_files\n");
		buff.append("\n");
		buff.append("#Print files list\n");
		buff.append("print_filenames(specs)\n");
		buff.append("puts SEPARATOR\n");
		buff.append("\n");
		buff.append("puts \"RSpec script : #{SPEC_SCRIPT}\\n\"\n");
		buff.append("puts \"\\nRuby Options:\"\n");
		buff.append("p RUBY_ARGS\n");
		buff.append("puts \"\\nSpec Options:\"\n");
		buff.append("p ARGV\n");
		buff.append("puts SEPARATOR\n");
		buff.append("\n");
		buff.append("puts \"Running specs...\"\n");
		buff.append("if (!LOAD_SEPARATELY)\n");
		buff.append(" Dir.chdir(WORK_DIR) if WORK_DIR\n");
		buff.append(" exec *generateCmdLine(specs)\n");
		buff.append("else\n");
		buff.append("  specs.each { |spec|\n");
		buff.append("    Dir.chdir(WORK_DIR) if WORK_DIR\n");
		buff.append("    system *generateCmdLine([spec])\n");
		buff.append("  }\n");
		buff.append("end\n");
		buff.append("\n");

		final BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
		out.write(buff.toString());
		out.close();
	}
}