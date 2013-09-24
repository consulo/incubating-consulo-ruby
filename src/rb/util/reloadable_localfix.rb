# Created by IntelliJ IDEA.
# User: oleg
# Date: Nov 28, 2007
# Time: 7:52:39 PM
# To change this template use File | Settings | File Templates.
require File.dirname(__FILE__) + '/reloadable_base'

class ReloadableLocalFix
    include_class 'com.intellij.codeInspection.LocalQuickFix' unless defined? LocalQuickFix
    include LocalQuickFix
    include Reloadable

    # @NotNull
    # public String getName()
    def getName
        reload
        @instance.getName
    end


    # @NotNull
    # public String getFamilyName()
    def getFamilyName
        reload
        @instance.getFamilyName
    end

    # public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
    def applyFix project, problem_descriptor
        reload
        @instance.applyFix project, problem_descriptor
        # do nothing
    end
end
