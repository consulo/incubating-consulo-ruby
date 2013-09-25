include Java

require File.dirname(__FILE__) + '/paramdef_base.rb'

import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions

module ParamDefs
    class ControllerRefParam < ParamDefBase
        def getVariants(paramContext)
            root = controller_root(paramContext)
            collect_files(paramContext.project, root) do |f|
                dir = f.parent
                folder_path = ControllersConventions::getRelativePathOfControllerFolder dir.url, paramContext.module
                name = ControllersConventions::getControllerName f
                unless folder_path.nil? or name.nil?
                    full_path = if folder_path.length > 0 then folder_path + "/" + name else name end
                    create_lookup_item paramContext, full_path
                end
            end
        end

        def resolveReference(paramContext)
            m = paramContext.module
            return nil if m.nil?
            text = element_text(paramContext)
            ControllersConventions::getControllerClassByShortName m, text
        end
    end
end