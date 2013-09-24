# Created by IntelliJ IDEA.
# User: oleg, Roman.Chernyatchik
# Date: Oct 5, 2007

=begin
 This is a stub file for ActionController, used for indexing
=end

require 'action_controller'
module ActionController
    class Base
# patched active_controller.rb
        include ActionController::Flash
        include ActionController::Flash::InstanceMethods
        include ActionController::Flash::ClassMethods

        include ActionController::Filters
        include ActionController::Filters::InstanceMethods
        include ActionController::Filters::ClassMethods

        include ActionController::Layout
        include ActionController::Layout::InstanceMethods
        include ActionController::Layout::ClassMethods

        include ActionController::Benchmarking
        include ActionController::Benchmarking::InstanceMethods
        include ActionController::Benchmarking::ClassMethods

        include ActionController::Rescue
        include ActionController::Rescue::InstanceMethods
        include ActionController::Rescue::ClassMethods

        include ActionController::Dependencies
        include ActionController::Dependencies::InstanceMethods
        include ActionController::Dependencies::ClassMethods

        include ActionController::MimeResponds
        include ActionController::MimeResponds::InstanceMethods
        include ActionController::MimeResponds::ClassMethods

        include ActionController::Pagination
        include ActionController::Pagination::InstanceMethods
        include ActionController::Pagination::ClassMethods

        include ActionController::Scaffolding
        include ActionController::Scaffolding::InstanceMethods
        include ActionController::Scaffolding::ClassMethods

        include ActionController::Helpers
        include ActionController::Helpers::InstanceMethods
        include ActionController::Helpers::ClassMethods

        include ActionController::Cookies
        include ActionController::Cookies::InstanceMethods
        include ActionController::Cookies::ClassMethods

        include ActionController::Caching
        include ActionController::Caching::InstanceMethods
        include ActionController::Caching::ClassMethods

        include ActionController::Verification
        include ActionController::Verification::InstanceMethods
        include ActionController::Verification::ClassMethods

        include ActionController::Streaming
        include ActionController::Streaming::InstanceMethods
        include ActionController::Streaming::ClassMethods

        include ActionController::SessionManagement
        include ActionController::SessionManagement::InstanceMethods
        include ActionController::SessionManagement::ClassMethods

        include ActionController::Components
        include ActionController::Components::InstanceMethods
        include ActionController::Components::ClassMethods

        include ActionController::Macros::AutoComplete
        include ActionController::Macros::AutoComplete::InstanceMethods
        include ActionController::Macros::AutoComplete::ClassMethods
        include ActionController::Macros::InPlaceEditing
        include ActionController::Macros::InPlaceEditing::InstanceMethods
        include ActionController::Macros::InPlaceEditing::ClassMethods

        include ActionController::RecordIdentifier
        include ActionController::RecordIdentifier::InstanceMethods
        include ActionController::RecordIdentifier::ClassMethods
        include ActionController::RequestForgeryProtection
        include ActionController::RequestForgeryProtection::InstanceMethods
        include ActionController::RequestForgeryProtection::ClassMethods
        include ActionController::HttpAuthentication::Basic::ControllerMethods
        include ActionController::HttpAuthentication::Basic::ControllerMethods::InstanceMethods
        include ActionController::HttpAuthentication::Basic::ControllerMethods::ClassMethods

# WebServices Extention
        include ActionWebService::Protocol::Discovery
        include ActionWebService::Protocol::Discovery::ClassMethods
        include ActionWebService::Protocol::Discovery::InstanceMethods
        include ActionWebService::Protocol::Soap
        include ActionWebService::Protocol::Soap::ClassMethods
        include ActionWebService::Protocol::Soap::InstanceMethods
        include ActionWebService::Protocol::XmlRpc
        include ActionWebService::Protocol::XmlRpc::ClassMethods
        include ActionWebService::Protocol::XmlRpc::InstanceMethods

        include ActionWebService::Container::Direct
        include ActionWebService::Container::Direct::ClassMethods
        include ActionWebService::Container::Direct::InstanceMethods
        include ActionWebService::Container::Delegated
        include ActionWebService::Container::Delegated::ClassMethods
        include ActionWebService::Container::Delegated::InstanceMethods
        include ActionWebService::Container::ActionController
        include ActionWebService::Container::ActionController::ClassMethods
        include ActionWebService::Container::ActionController::InstanceMethods

        include ActionWebService::Invocation
        include ActionWebService::Invocation::ClassMethods
        include ActionWebService::Invocation::InstanceMethods

        include ActionWebService::Dispatcher
        include ActionWebService::Dispatcher::ClassMethods
        include ActionWebService::Dispatcher::InstanceMethods
        include ActionWebService::Dispatcher::ActionController
        include ActionWebService::Dispatcher::ActionController::ClassMethods
        include ActionWebService::Dispatcher::ActionController::InstanceMethods

        include ActionWebService::Scaffolding
        include ActionWebService::Scaffolding::ClassMethods
        include ActionWebService::Scaffolding::InstanceMethods
    end
end
