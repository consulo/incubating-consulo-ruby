# Created by IntelliJ IDEA.
# User: oleg
# Date: Sep 21, 2007
# Time: 12:08:28 PM
# To change this template use File | Settings | File Templates.

include Java

require File.dirname(__FILE__) + '/introduceVariable/introduce_var_handler'
require File.dirname(__FILE__) + '/inline/inline_handler'

include_class 'com.intellij.lang.refactoring.DefaultRefactoringSupportProvider' unless defined? DefaultRefactoringSupportProvider

class RubyRefactoringProvider < DefaultRefactoringSupportProvider

    include_class 'com.intellij.lang.refactoring.RefactoringSupportProvider' unless defined? RefactoringSupportProvider
    include RefactoringSupportProvider

    include_class 'rb.refactoring.introduceVariable.RubyIntroduceVariableHandlerWrapper' unless defined? RubyIntroduceVariableHandlerWrapper

    def initialize
        @introduceVarHandler = RubyIntroduceVariableHandlerWrapper.new
        @introduceVarHandler.setJRubyHandler IntroduceVariableHandler.rnew

        @inlineHandler = RubyInlineHandler.rnew
    end

    #boolean
    def isSafeDeleteAvailable(element)
        false
    end

    #RefactoringActionHandler
    def getIntroduceVariableHandler()
        @introduceVarHandler
    end

    # InlineHandler
    def getInlineHandler()
        @inlineHandler
    end
end