# Created by IntelliJ IDEA.
# User: Roman.Chernyathcik
# Date: Dec 5, 2007

=begin
 This is a stub file for ActionWebService, used for indexing
=end

require 'action_web_service'

module ActionWebService
    class Base
        include ActionWebService::Container::Direct
        include ActionWebService::Container::Direct
        include ActionWebService::Container::Direct::InstanceMethods
        include ActionWebService::Invocation::ClassMethods
        include ActionWebService::Invocation::InstanceMethods
        include ActionWebService::Invocation::ClassMethods
    end
end

#also see my_action_controller.rb