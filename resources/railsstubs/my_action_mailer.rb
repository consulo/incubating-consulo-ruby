# Created by IntelliJ IDEA.
# User: Roman.Chernyatchik
# Date: Dec 5, 2007

=begin
 This is a stub file for ActionMailer, used for indexing
=end
require 'action_mailer'

module ActionMailer
    class Base
        include ActionMailer::Quoting
        include ActionMailer::Helpers
        include ActionMailer::Helpers::InstanceMethods
        include ActionMailer::Helpers::ClassMethods
    end
end