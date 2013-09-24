include Java

require File.dirname(__FILE__) + '/paramdef_base.rb'

import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil unless defined? RailsFacetUtil
import com.intellij.openapi.vfs.VirtualFileManager unless defined? VirtualFileManager

module ParamDefs
    class StylesheetRefParam < ParamDefBase
        CSS_EXT = "css"

        def getVariants(context)
            publ_root = public_root(context)

            publ_root_path = publ_root.path
            publ_root_length = publ_root_path.length

            stylesh_root_path = stylesheets_root(context).path
            stylesh_root_length = stylesh_root_path.length

            collect_files(context.project, publ_root) do |f|
                next unless f.extension.downcase == CSS_EXT
                file_path = f.path

                name_offset = file_path[0, stylesh_root_length] == stylesh_root_path ?
                        # "+1" for removing "/" from the name relative to stylesheet
                         stylesh_root_length + 1 :
                         publ_root_length;

                # 4 = ".css".length
                create_lookup_item context, file_path[name_offset, file_path.length - name_offset - 4]
            end
        end

        def resolveReference(context)
            #path with .css extnsion
            file_relative_name = element_text(context)
            file_relative_name << '.' << CSS_EXT unless file_relative_name.match(/.+\.css/)

            if file_relative_name[0, 1] == "/"
                file_relative_name = file_relative_name[1, file_relative_name.length - 1]
                root = public_root(context)
            else
                root = stylesheets_root(context)
            end

            return nil if root.nil?
            find_psi_file context, root.find_file_by_relative_path(file_relative_name)
        end

        def stylesheets_root(context)
            rails_std_paths_file context, :getStylesheetsRootURL
        end

        def public_root(context)
            rails_std_paths_file context, :getPublicRootURL
        end
    end
end