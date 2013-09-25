require File.dirname(__FILE__) + '/method_ref_param'

import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions unless defined? ControllersConventions

module ParamDefs
    class ControllerMethodRefParam < MethodRefParam
    protected
        def find_target_class(context)
            m = context.module
            unless m.nil? then
                context_element = context.call
                file = context_element.containing_file
                controller_class = ControllersConventions::get_controller_by_view_file file, context.module
                return controller_class unless controller_class.nil?
            end
            super
        end
    end
end