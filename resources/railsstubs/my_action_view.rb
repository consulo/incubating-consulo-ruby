# Created by IntelliJ IDEA.
# User: oleg, Roman.Chernyatchik
# Date: Oct 5, 2007

=begin
 This is a stub file for ActionView, used for indexing
=end
 
require 'action_view'

module ActionView
    class Base
        # patched action_view
        include ActionView::Partials
        include ActionView::Partials::InstanceMethods
        include ActionView::Partials::ClassMethods
    end
end
