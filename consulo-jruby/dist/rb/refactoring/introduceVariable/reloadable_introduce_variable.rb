# Created by IntelliJ IDEA.
# User: oleg
# Date: Nov 23, 2007
# Time: 9:12:55 PM
# To change this template use File | Settings | File Templates.
require File.dirname(__FILE__) + '/../../util/reloadable_ruby_refactoring'

class ReloadableIntroduceVariable < ReloadableRubyRefactoringHandler 
    include_class 'rb.refactoring.introduceVariable.RubyIntroduceVariableHandler' unless defined? RubyIntroduceVariableHandler
    include RubyIntroduceVariableHandler

    # void introduceVariable(@NotNull Project project, Editor editor, PsiFile file, @Nullable DataContext dataContext,
    #                        @Nullable String name, boolean replaceAll);
    def introduceVariable(project, editor, file, dataContext, name, replace_all)
        reload
        @instance.introduceVariable(project, editor, file, dataContext, name, replace_all)
    end
end