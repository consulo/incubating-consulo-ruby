#Copyright 2000-2007 JetBrains s.r.o.
#
#Licensed under the Apache License, Version 2.0 (the "License");
#you may not use this file except in compliance with the License.
#You may obtain a copy of the License at
#
#http://www.apache.org/licenses/LICENSE-2.0
#
#Unless required by applicable law or agreed to in writing, software
#distributed under the License is distributed on an "AS IS" BASIS,
#WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#See the License for the specific language governing permissions and
#limitations under the License.

# Created by IntelliJ IDEA.
# User: oleg
# Date: Dec 9, 2007


def get_content symbol
    content = []
    content += eval("#{symbol}.methods")
    content += eval("#{symbol}.singleton_methods")
    begin
        content += eval("#{symbol}.constants")
    rescue
        # ignore, Object has no constants method
    end
    content.uniq.sort
end

top_level_content = get_content :self
top_level_content = top_level_content.find_all {|m| m != :get_content}

object_content = get_content :Object
kernel_content = get_content :Kernel
class_content = get_content :Class
module_content = get_content :Module

puts "Top_Level"
p top_level_content

puts "Kenel"
p kernel_content

puts "Top_Level - Kernel"
p top_level_content - kernel_content

puts "Object - Kenel"
p object_content - kernel_content

puts "Module - Object"
#p object_methods
p module_content - object_content

puts "Class - Module"
#p module_methods
p class_content - module_content

#puts "Class"
#p class_methods
