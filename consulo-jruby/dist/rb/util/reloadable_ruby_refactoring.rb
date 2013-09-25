# Created by IntelliJ IDEA.
# User: oleg
# Date: Oct 2, 2007
# Time: 8:09:28 PM
# To change this template use File | Settings | File Templates.
require File.dirname(__FILE__) + '/reloadable_base'

class ReloadableRubyRefactoringHandler
    include_class 'rb.refactoring.RubyRefactoringHandler' unless defined? RubyRefactoringHandler
    include RubyRefactoringHandler
    include Reloadable

    # void invokeOutter(@NotNull Project project, @NotNull PsiElement[] elements, @Nullable DataContext dataContext);
    def invokeOutter(project, elements, dataContext)
        reload
        @instance.invokeOutter(project, elements, dataContext)
    end

    # void invoke(@NotNull Project project, Editor editor, PsiFile file, @Nullable DataContext dataContext);
    def invoke(project, editor, file, dataContext)
        reload
        @instance.invoke(project, editor, file, dataContext)
    end
end