include Java

require File.dirname(__FILE__) + '/paramdef_base.rb'

module ParamDefs
    class HelperRefParam < ParamDefBase
        def getVariants(context)
            helpers_root = helpers_root(context)
            helpers_root_path = helpers_root ? "#{helpers_root.get_path}/" : nil
            collect_files(context.project, helpers_root) do |f|
                relative_name = f.path.gsub(helpers_root_path , "")
                variant = relative_name.chomp("_helper.rb")
                # quote string if contains slash (/)
                if variant.index('/')
                  variant = "'#{variant}'"
                end
                create_lookup_item context, variant, LookupItemType::Symbol
            end
        end

        def resolveReference(context)
            find_psi_file_under context, helpers_root(context), element_text(context) + "_helper.rb"
        end

        def helpers_root(context)
            settings = module_settings(context)
            return nil if settings.nil?
            VirtualFileManager::get_instance.find_file_by_url settings.getHelpersRootURL
        end
    end
end