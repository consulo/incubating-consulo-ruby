# Created by IntelliJ IDEA.
# User: oleg
# Date: Jan 30, 2008
# Time: 4:54:13 PM
# To change this template use File | Settings | File Templates.
require File.dirname(__FILE__) + '/../util/reloadable_code_insight_action'

class ReloadableImplementHandler < ReloadableCodeInsightAction
    include_class 'rb.implement.ImplementHandler' unless defined? ImplementHandler
    include ImplementHandler

    def create_implement_members(class_symbol)
        reload
        @instance.create_implement_members class_symbol
    end

    def execute(editor, project, element, symbol)
       reload
        @instance.execute editor, project, element, symbol
    end
end
