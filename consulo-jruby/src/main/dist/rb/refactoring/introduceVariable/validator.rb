# Created by IntelliJ IDEA.
# User: oleg
# Date: Sep 21, 2007
# Time: 7:49:34 PM
# To change this template use File | Settings | File Templates.
class RubyValidator
    include_class 'org.jetbrains.plugins.ruby.ruby.refactoring.introduceVariable.IntroduceVariableValidator' unless
            defined? IntroduceVariableValidator
    include IntroduceVariableValidator
    include_class 'org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeUtil' unless defined? ScopeUtil
    include_class 'org.jetbrains.plugins.ruby.RBundle' unless defined? RBundle
    include_class 'org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeUtil' unless defined? ScopeUtil
    include_class 'org.jetbrains.plugins.ruby.RBundle' unless defined? RBundle

    def initialize expression, occurences
        @expression = expression
        @occurences = occurences
    end

    #String check(RubyIntroduceVariableSettings settings);
    def check(settings)
        name = settings.getName

        # Scope check
        # check that new name will not conflict with local variables
        error_message = RBundle.message("refactoring.introduce.variable.scope.error", [].to_java)
        if settings.doReplaceAllOccurrences
            @occurences.each do |expr|
                ScopeUtil.gatherScopeVariables(expr).each do |scope_variable|
                    if (scope_variable.name == name) then
                        # if we found scopevariable with the same name
                        return error_message
                    end
                end
            end
        else
            ScopeUtil.gatherScopeVariables(@expression).each do |scope_variable|
                if (scope_variable.name == name) then
                    # if we found scopevariable with the same name
                    return error_message
                end
            end
        end
        # otherwise return nil
        nil
    end

    #String check(RubyIntroduceVariableSettings settings);
    def check_possible_name(name)
        # Scope check
        # check that new name will not conflict with local variables
        error_message = RBundle.message("refactoring.introduce.variable.scope.error", [].to_java)
        @occurences.each do |expr|
            ScopeUtil.gatherScopeVariables(expr).each do |scope_variable|
                if (scope_variable.name == name) then
                    # if we found scopevariable with the same name
                    return error_message
                end
            end
        end
        # otherwise return nil
        nil
    end
end
