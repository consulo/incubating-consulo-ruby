include Java

require File.dirname(__FILE__) + '/view_ref_param'

import org.jetbrains.plugins.ruby.rails.RailsUtil unless defined? RailsUtil
import com.intellij.openapi.vfs.VfsUtil unless defined? VfsUtil
import org.jetbrains.plugins.ruby.rails.nameConventions.ViewsConventions unless defined? ViewsConventions

module ParamDefs
    class PartialRefParam < ViewRefParam
        def find_view_file(paramContext, root, name)
            if name[0, 1] == '/'
                pos = name.rindex('/')
                root.path + name[0..pos] + "_" + name[pos+1..name.length]
            else
                folder = view_folder_from_context(paramContext)
                folder.path + "/_" + name
            end
        end

        def build_view_name(paramContext, root, path)
            return nil if path.name[0,1] != '_'
            view_name = path.name_without_extension[1..path.name.length]
            folder = view_folder_from_context(paramContext)
            if path.parent == folder
                view_name
            else
                s = VfsUtil::getRelativePath(path.parent, root, '/'[0])
                '/' + s + '/' + view_name
            end
        end

        private
        def view_folder_from_context(paramContext)
            containing_file = paramContext.value_element.containing_file.virtual_file
            views_root = RailsUtil::getViewsRoot paramContext.module
            if VfsUtil::isAncestor views_root, containing_file, false
                containing_file.parent
            else
                ViewsConventions::getViewsFolder(containing_file, paramContext.module)
            end
        end
    end
end
