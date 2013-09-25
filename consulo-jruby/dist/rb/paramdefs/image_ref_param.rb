include Java

require File.dirname(__FILE__) + '/paramdef_base.rb'

import com.intellij.openapi.vfs.VirtualFileManager unless defined? VirtualFileManager

module ParamDefs
    class ImageRefParam < ParamDefBase
        def getVariants(context)
            collect_files(context.project, images_root(context)) do |f|
                create_lookup_item context, f.name
            end
        end

        def resolveReference(context)
            settings = module_settings(context)
            return nil if settings.nil?
            text = element_text(context)
            if text[0,1] == '/'
                url = settings.getPublicRootURL + text
                find_psi_file context, VirtualFileManager::get_instance.find_file_by_url(url)
            else
                root = images_root(context)
                return nil if root.nil?
                find_psi_file context, root.find_child(text)
            end
        end

        def images_root(context)
            settings = module_settings(context)
            return nil if settings.nil?
            VirtualFileManager::get_instance.find_file_by_url settings.getImagesRootURL
        end
    end
end