# Created by IntelliJ IDEA.
# User: oleg, Roman.Chernyatchik
# Date: Oct 5, 2007

=begin
 This is a stub file for ActiveRecord, used for indexing
=end

require 'active_record'

module ActiveRecord
    class Base
        include ActiveRecord::Validations
        include ActiveRecord::Validations::InstanceMethods

        include ActiveRecord::Locking::Optimistic
        include ActiveRecord::Locking::Optimistic::InstanceMethods
        include ActiveRecord::Locking::Pessimistic
        include ActiveRecord::Locking::Pessimistic::InstanceMethods

        include ActiveRecord::Callbacks
        include ActiveRecord::Callbacks::InstanceMethods
        include ActiveRecord::Observing
        include ActiveRecord::Observing::InstanceMethods

        include ActiveRecord::Timestamp
        include ActiveRecord::Timestamp::InstanceMethods

        include ActiveRecord::Associations
        include ActiveRecord::Associations::InstanceMethods

        include ActiveRecord::Aggregations
        include ActiveRecord::Aggregations::InstanceMethods

        include ActiveRecord::Transactions
        include ActiveRecord::Transactions::InstanceMethods

        include ActiveRecord::Reflection
        include ActiveRecord::Reflection::InstanceMethods

        include ActiveRecord::Acts::Tree
        include ActiveRecord::Acts::Tree::InstanceMethods

        include ActiveRecord::Acts::List
        include ActiveRecord::Acts::List::InstanceMethods

        include ActiveRecord::Acts::NestedSet
        include ActiveRecord::Acts::NestedSet::InstanceMethods

        include ActiveRecord::Calculations
        include ActiveRecord::Calculations::InstanceMethods

        include ActiveRecord::XmlSerialization
        include ActiveRecord::XmlSerialization::InstanceMethods

        include ActiveRecord::AttributeMethods
        include ActiveRecord::AttributeMethods::InstanceMethods

        extend ActiveRecord::QueryCache
    end
end

# TODO: add adapters