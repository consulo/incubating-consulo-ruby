# Created by IntelliJ IDEA.
# User: oleg
# Date: Jan 30, 2008
# Time: 2:36:36 PM
# To change this template use File | Settings | File Templates.
require File.dirname(__FILE__) + '/../util/reloadable_code_insight_action'

class ReloadableOverrideHandler < ReloadableCodeInsightAction
    include_class 'rb.override.OverrideHandler' unless defined? OverrideHandler
    include OverrideHandler

    def create_override_members(project, class_symbol)
       reload
        @instance.create_override_members project, class_symbol
    end
end
