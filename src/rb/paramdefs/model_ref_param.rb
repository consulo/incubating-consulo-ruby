include Java

require File.dirname(__FILE__) + '/paramdef_base.rb'

import com.intellij.openapi.util.text.StringUtil

module ParamDefs
    class ModelRefParam < ParamDefBase
        def getVariants(paramContext)
            collect_files(paramContext.project, model_root(paramContext)) do |f|
                model_name = StringUtil::pluralize(f.name_without_extension)
                create_lookup_item paramContext, model_name, LookupItemType::Symbol
            end
        end

        def resolveReference(paramContext)
            root = model_root(paramContext)
            return nil if root.nil?
            model_name = element_text(paramContext)
            virtual_file = root.find_child(StringUtil::unpluralize(model_name) + ".rb")
            find_psi_file paramContext, virtual_file
        end
    end
end