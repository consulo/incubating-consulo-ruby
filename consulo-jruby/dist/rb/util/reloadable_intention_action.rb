# Created by IntelliJ IDEA.
# User: oleg
# Date: Oct 2, 2007
# Time: 7:50:46 PM
# To change this template use File | Settings | File Templates.

require File.dirname(__FILE__) + '/reloadable_base'

class ReloadableIntentionAction
    include_class 'com.intellij.codeInsight.intention.IntentionAction' unless defined? IntentionAction
    include IntentionAction
    include Reloadable

    def getFamilyName
        reload
        @instance.getFamilyName
    end

    #String
    def getText
        reload
        @instance.getText
    end

    #boolean
    def isAvailable(project, editor, psi_file)
        reload
        @instance.isAvailable(project, editor, psi_file)
    end

    #void
    def invoke(project, editor, psi_file)
        reload
        @instance.invoke(project, editor, psi_file)
    end

    #boolean
    def startInWriteAction
        reload
        @instance.startInWriteAction
    end

end