include Java

import org.jetbrains.plugins.ruby.rails.RailsUtil unless defined? RailsUtil
import org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.ParamDefImplUtil unless defined? ParamDefImplUtil
import org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.LookupItemType unless defined? LookupItemType
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyRecursiveElementVisitor
import com.intellij.psi.PsiManager unless defined? PsiManager
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil unless defined? RailsFacetUtil

module ParamDefs
    class MethodCollector < RubyRecursiveElementVisitor
        def initialize(callback)
            super()
            @callback = callback
            @result = []
        end

        attr_reader :result

        def visitRMethod(method)
            item = @callback.call(method)
            @result << item unless item.nil?
        end
    end

    class ParamDefBase < org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.ParamDef
    protected
        def view_root(paramContext)
            m = paramContext.module
            return nil if m.nil?
            RailsUtil::getViewsRoot m
        end

        def controller_root(paramContext)
            m = paramContext.module
            return nil if m.nil?
            RailsUtil::getControllersRoot m
        end

        def model_root(paramContext)
            m = paramContext.module
            return nil if m.nil?
            RailsUtil::getModelRoot m
        end

        def module_settings(context)
            m = context.module

            m ? (settings = RailsFacetUtil::get_rails_app_paths(m)) : nil
        end

        def element_text(paramContext)
            ParamDefImplUtil::getElementText paramContext.value_element
        end

        def collect_files(project, root)
            return nil unless root

            fileIndex = com.intellij.openapi.roots.ProjectRootManager::getInstance(project).getFileIndex
            result = []

            iterator = com.intellij.openapi.roots.ContentIterator.impl do |name, f|
                unless f.directory?
                    item = yield f
                    result << item unless item.nil?
                end
                true
            end
            fileIndex.iterateContentUnderDirectory root, iterator

            lookUpItems_to_java_list(result)
        end

        def collect_lookup_items(context, root, item_type=LookupItemType::String)
            return nil unless root

            fileIndex = com.intellij.openapi.roots.ProjectRootManager::getInstance(context.project).getFileIndex
            result = []

            iterator = com.intellij.openapi.roots.ContentIterator.impl do |name, f|
                unless f.directory?
                    item_name = yield f
                    unless item_name.nil?
                        item = relative_path(root, f) + item_name
                        result << create_lookup_item(context, item, item_type)
                    end
                end
                true
            end
            fileIndex.iterateContentUnderDirectory root, iterator

            lookUpItems_to_java_list(result)
        end

        def relative_path(root, f)
            s = ""
            while f.parent != root
                s = f.parent.name + "/" + s
                f = f.parent
            end
            s
        end

        def collect_methods(psiElement, &callback)
            visitor = MethodCollector.new(callback)
            psiElement.accept visitor unless psiElement.nil?

            lookUpItems_to_java_list(visitor.result)
        end

        def create_lookup_item(paramContext, s, item_type=LookupItemType::String)
            return nil unless s

            ParamDefImplUtil::createSimpleLookupItem s, item_type, paramContext.value_element
        end

        def find_psi_file(paramContext, virtual_file)
            return nil unless virtual_file

            PsiManager::getInstance(paramContext.project).findFile virtual_file
        end

        def find_psi_file_under(paramContext, root, name)
            return nil unless root

            f = root
            components = name.split("/")
            for c in components
                f = f.find_child(c)
                return nil if f.nil?
            end
            find_psi_file paramContext, f
        end

        def rails_std_paths_file(paramContext, method_name)
            m = paramContext.module
            return nil unless m

            pathsClass = RailsFacetUtil.getRailsAppPaths(m)
            url = pathsClass.send method_name
            
            VirtualFileManager::get_instance.find_file_by_url url
        end

        def lookUpItems_to_java_list(results)
            java.util.Arrays.as_list(results.to_java(:"org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubyLookupItem"))
        end
    end
end
