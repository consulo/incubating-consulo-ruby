# Created by IntelliJ IDEA.
# User: oleg
# Date: Oct 2, 2007
# Time: 7:58:14 PM
# To change this template use File | Settings | File Templates.

require File.dirname(__FILE__) + '/reloadable_base'

class ReloadableCodeInsightAction
    include_class 'com.intellij.lang.LanguageCodeInsightActionHandler' unless defined? LanguageCodeInsightActionHandler
    include LanguageCodeInsightActionHandler
    include Reloadable

    #boolean isValidFor(Editor editor, PsiFile file);
    def isValidFor(editor, file)
        reload
        @instance.isValidFor(editor, file)
    end

    #void invoke(Project project, Editor editor, PsiFile file);
    def invoke(project, editor, file)
        reload
        @instance.invoke(project, editor, file)
    end

    #boolean startInWriteAction();
    def startInWriteAction
        reload
        @instance.startInWriteAction
    end

end