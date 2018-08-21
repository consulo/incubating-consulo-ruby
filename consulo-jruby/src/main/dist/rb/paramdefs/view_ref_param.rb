include Java

require File.dirname(__FILE__) + '/paramdef_base.rb'

import com.intellij.openapi.vfs.LocalFileSystem

module ParamDefs
    class ViewRefParam < ParamDefBase
        def getVariants(paramContext)
            root = view_root(paramContext)
            return collect_files(paramContext.project, root) do |f|
                build_item(root, f, paramContext)
            end
        end

        def resolveReference(paramContext)
            root = view_root(paramContext)
            return nil if root.nil?
            name = element_text(paramContext)
            virtualFile = LocalFileSystem::getInstance.findFileByPath find_view_file(paramContext, root, name + ".rhtml")
            if virtualFile.nil?
                virtualFile = LocalFileSystem::getInstance.findFileByPath find_view_file(paramContext, root, name)
            end
            find_psi_file paramContext, virtualFile
        end

        protected
        def find_view_file(paramContext, root, name)
            root.path + "/" + name
        end

        def build_view_name(paramContext, root, path)
            s = com.intellij.openapi.vfs.VfsUtil::getRelativePath(path, root, '/'[0])
            s[0,s.rindex('.')]
        end

        private
        def build_item(root, path, paramContext)
            s = build_view_name(paramContext, root, path)
            create_lookup_item paramContext, s
        end
    end

    class LayoutRefParam < ViewRefParam
        def view_root(paramContext)
            root = super(paramContext)
            root.find_child("layouts")
        end
    end
end