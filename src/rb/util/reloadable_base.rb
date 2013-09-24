# Created by IntelliJ IDEA.
# User: oleg
# Date: Oct 2, 2007
# Time: 8:04:42 PM
# To change this template use File | Settings | File Templates.
# It`s a reloadable base class

module Reloadable
    def initialize(class_name, file)
        @class_name = class_name
        @instance =  (Kernel.const_get(@class_name)).new
        @file = file
        @time = File.mtime(@file)
    end
    
    private

    def reload
        return if @time == File.mtime(@file)
        if Object.const_defined?(@class_name)
            Object.send :remove_const, @class_name
        end

        load @file
        @time = File.mtime(@file)
        @instance =  (Kernel.const_get(@class_name)).new
    end
end