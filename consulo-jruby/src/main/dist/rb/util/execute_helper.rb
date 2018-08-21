# Created by IntelliJ IDEA.
# User: oleg
# Date: Sep 24, 2007
# Time: 4:40:37 PM
# To change this template use File | Settings | File Templates.

include_class 'com.intellij.util.ActionRunner' unless defined? ActionRunner

module ExecuteHelper
    include_class 'com.intellij.openapi.command.CommandProcessor' unless defined? CommandProcessor

    class MyRunnable
        include java.lang.Runnable
        include ActionRunner::InterruptibleRunnable

        def initialize proc
            @proc = proc
        end

        def run
            @proc.call
        end
    end

    def self.run_as_command(project, command_name, &proc)
        runnable = MyRunnable.new proc
        CommandProcessor.instance.executeCommand project, runnable, command_name, nil
    end

    def self.run_in_write_action(&proc)
        ActionRunner.runInsideWriteAction(MyRunnable.new(proc))
    end
end