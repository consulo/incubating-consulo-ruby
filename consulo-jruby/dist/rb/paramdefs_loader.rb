# Created by IntelliJ IDEA.
# User: yole
# Date: 22.12.2007
# Time: 23:17:02
# To change this template use File | Settings | File Templates.

include Java
require File.dirname(__FILE__) + '/paramdefs/view_ref_param'
require File.dirname(__FILE__) + '/paramdefs/partial_ref_param'
require File.dirname(__FILE__) + '/paramdefs/controller_ref_param'
require File.dirname(__FILE__) + '/paramdefs/method_ref_param'
require File.dirname(__FILE__) + '/paramdefs/controller_method_ref_param'
require File.dirname(__FILE__) + '/paramdefs/model_ref_param'
require File.dirname(__FILE__) + '/paramdefs/stylesheet_ref_param'
require File.dirname(__FILE__) + '/paramdefs/image_ref_param'
require File.dirname(__FILE__) + '/paramdefs/script_ref_param'
require File.dirname(__FILE__) + '/paramdefs/helper_ref_param'

module ParamDefs
    class <<self
        import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier
        include_class 'org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.ResolvingParamDependency'

        def method_ref
            MethodRefParam.new AccessModifier::PUBLIC, LookupItemType::Symbol
        end

        def action_ref(options = {})
            p = ControllerMethodRefParam.new AccessModifier::PUBLIC
            if options.has_key? :class
                p.class_dependency = ResolvingParamDependency.new(':' + options[:class].to_s)
            end
            p
        end

        def model_ref
            ModelRefParam.new
        end

        def controller_ref
            ControllerRefParam.new
        end

        def view_ref
            ViewRefParam.new
        end

        def layout_ref
            LayoutRefParam.new
        end

        def partial_ref
            PartialRefParam.new
        end

        def stylesheet_ref
            StylesheetRefParam.new
        end

        def script_ref
            ScriptRefParam.new
        end

        def image_ref
            ImageRefParam.new
        end

        def helper_ref
            HelperRefParam.new
        end

        def one_of(*params)
            org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.EnumParam.new params
        end

        def convert_param(param)
            if param.kind_of? Hash
                h = org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.HashParamDef.new
                param.each { |key, value| h.add(key.to_s, convert_param(value)) }
                h
            elsif param.kind_of? Array
                inner = convert_param param[0]
                org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.ListParamDef::new(inner)
            else
                param
            end
        end

        def define_params(name, *params)
            manager = org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.ParamDefManager.getInstance
            paramDefs = params.collect { |p| convert_param(p) }
            manager.registerParamDef name, paramDefs.to_java(:'org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.ParamDef')
        end

        def define_params_copy(name, copy_from_name)
            manager = org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs.ParamDefManager.getInstance
            paramDefs = manager.getParamDefs copy_from_name
            manager.registerParamDef name, paramDefs
        end
    end

    define_params 'ActionController::Base::render',
            { :action => action_ref,
              :text => nil,
              :template => view_ref,
              :partial => partial_ref }

    define_params_copy 'ActionController::Base::render_to_string', 'ActionController::Base::render'

    define_params 'ActionController::Base::redirect_to',
            { :controller => controller_ref,
              :action => action_ref(:class => :controller)
            }
    define_params 'ActionController::Verification::ClassMethods::verify',
            { :method => one_of(:get, :head, :post, :put, :delete),
              :only => action_ref,
              :redirect_to => nil}
    define_params 'ActionController::Pagination::ClassMethods::paginate', model_ref, {:per_page => nil}
    define_params 'ActionController::Filters::ClassMethods::append_before_filter', method_ref,
            { :except => action_ref,
              :only => action_ref }

    define_params 'ActionController::Base::url_for',
            { :controller => controller_ref,
              :action => action_ref(:class => :controller) }

    define_params 'ActionController::Layout::ClassMethods::layout',
            layout_ref

    define_params 'ActionController::Helpers::ClassMethods::helper',
            [helper_ref]

    define_params 'ActionView::Helpers::FormTagHelper::form_tag',
            { :controller => controller_ref,
              :action => action_ref(:class => :controller) }
    define_params 'ActionView::Helpers::UrlHelper::link_to',
            nil,
            { :controller => controller_ref,
              :action => action_ref(:class => :controller) }
    define_params "ActionView::Helpers::AssetTagHelper::stylesheet_link_tag",
            [stylesheet_ref]
    define_params "ActionView::Helpers::AssetTagHelper::image_tag",
            image_ref
    define_params "ActionView::Helpers::AssetTagHelper::javascript_include_tag",
            script_ref
    define_params "ActionView::Helpers::PrototypeHelper::JavaScriptGenerator::GeneratorMethods::replace_html",
            nil,
            { :partial => partial_ref }
end