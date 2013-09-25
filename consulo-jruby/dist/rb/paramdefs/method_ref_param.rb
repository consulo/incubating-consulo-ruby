include Java
require File.dirname(__FILE__) + '/paramdef_base.rb'

import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier
import org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.LookupItemType unless defined? LookupItemType

module ParamDefs
    class MethodRefParam < ParamDefBase
        def initialize(min_access, item_type=LookupItemType::String)
            super()
            @min_access = min_access
            @item_type = item_type
            @class_dependency = nil
        end

        attr_accessor :class_dependency

        def getVariants(context)
            target = get_target_class(context)
            collect_methods(target) do |method|
                if matches_min_access? method
                    create_lookup_item context, method.name, @item_type
                else
                    nil
                end
            end
        end

        def resolveReference(context)
            name = element_text(context)
            target = get_target_class(context)
            return nil if target.nil?
            RContainerUtil::getMethodByName target, name
        end

    protected
        def find_target_class(context)
            context_element = context.call
            PsiTreeUtil::getParentOfType context_element, RClass.java_class
        end

        def get_target_class(context)
            if not @class_dependency.nil?
                dependency_value = @class_dependency.getValue(context)
                return dependency_value if dependency_value.is_a? RClass
            end
            find_target_class context
        end

        def matches_min_access?(method)
            modifier = method.access_modifier
            if @min_access == AccessModifier::PUBLIC
                return modifier == AccessModifier::PUBLIC                
            end
            return true
        end
    end
end