include Java

require File.dirname(__FILE__) + '/paramdef_base.rb'

module ParamDefs
    class ScriptRefParam < ParamDefBase
        def getVariants(context)
            collect_lookup_items(context, scripts_root(context)) { |f| f.name_without_extension }
        end

        def resolveReference(context)
            find_psi_file_under context, scripts_root(context), element_text(context) + ".js"
        end

        def scripts_root(context)
            rails_std_paths_file context, :getJavascriptsRootURL
        end
    end
end