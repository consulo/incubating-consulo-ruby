# Created by IntelliJ IDEA.
# User: oleg
# Date: Sep 28, 2007
# Time: 5:36:20 PM
# To change this template use File | Settings | File Templates.

require File.dirname(__FILE__) + '/reloadable_override_handler'
require File.dirname(__FILE__) + '/../../rb/util/psi_helper'
require File.dirname(__FILE__) + '/../util/generate_helper'
require File.dirname(__FILE__) + '/../../rb/util/execute_helper'
require File.dirname(__FILE__) + '/../util/symbol_helper'

class RubyOverrideHandler
    include_class 'rb.override.OverrideHandler' unless defined? OverrideHandler
    include OverrideHandler
    include SymbolHelper

    include_class 'rb.override.RubyMemberChooser' unless defined? RubyMemberChooser
    include_class 'com.intellij.codeInsight.generation.PsiMethodMember' unless defined? PsiMethodMember
    include_class 'org.jetbrains.plugins.ruby.RBundle' unless defined? RBundle
    include_class 'org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil' unless defined? SymbolUtil
    include_class 'org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types' unless defined? Types
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil' unless defined? RVirtualPsiUtil
    include_class 'rb.override.RClassMember' unless defined? RClassMember
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod' unless defined? RMethod
    include_class 'org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.LastSymbolStorage' unless defined? LastSymbolStorage
    import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.RubyOverrideImplementUtil unless defined? RubyOverrideImplementUtil
    import com.intellij.psi.util.PsiUtil unless defined? PsiUtil


    def self.rnew
        ReloadableOverrideHandler.new(:RubyOverrideHandler, __FILE__)
    end

    #boolean isValidFor(Editor editor, PsiFile file);
    def isValidFor(editor, file)
        with_element_and_symbol(editor, file) do |element, symbol|
            return element && symbol ? true : false
        end
    end

    #void invoke(Project project, Editor editor, PsiFile file);
    def invoke(project, editor, file)
        with_element_and_symbol(editor, file) do |element, symbol|
            return unless element && symbol

            # Create ruby_class_members to override
            override_members = create_override_members project, symbol

            # Creating override dialog
            ruby_member_chooser = RubyMemberChooser.new override_members.to_java(:'com.intellij.codeInsight.generation.ClassMember'), project
            ruby_member_chooser.setTitle(RBundle.message("refactoring.override.methods.title", [].to_java))
            ruby_member_chooser.setCopyJavadocVisible false
            ruby_member_chooser.show

            return if ruby_member_chooser.getExitCode == RubyMemberChooser::CANCEL_EXIT_CODE

            #Starting code generation!
            new_methods = []
            ruby_member_chooser.getSelectedElements.each do |class_member|
                new_method = GenerateHelper.generate_new_method class_member.getPsiElement
                new_methods << new_method if new_method
            end

            #Inserting new elements
            ExecuteHelper.run_in_write_action do
                PsiHelper.insert_elements project, element, new_methods
                # setting caret
                unless new_methods.empty?
                    method = new_methods.first
                    comment = method.getChildByFilter(org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes::TLINE_COMMENT, 0)
                    editor.caret_model.moveToOffset comment.text_offset if comment
                end
            end
        end
    end

    #boolean startInWriteAction();
    def startInWriteAction
        false
    end

    # Gather all the info about context, that we can override
    # Note: it`s a bit different from java behaviour!!!
    def create_override_members project, class_symbol
        file_symbol = LastSymbolStorage.getInstance(project).getSymbol
        children = SymbolUtil.getAllChildrenWithSuperClassesAndIncludes file_symbol, org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context::INSTANCE, class_symbol, nil

        overriden_members = []
        methods = children.getSymbolsOfTypes(Types.METHODS).all.to_a.reverse

        # we create new set to store seen names
        seen_names = Set.new

        # we shouldn`t show already overriden methods
        class_symbol.getChildren(file_symbol).getSymbolsOfTypes(Types.METHODS).all.each do |m|
            seen_names << m.name
        end

        methods.each do |method|
            name = method.name
            unless seen_names.include? name or
                    method.getParentSymbol == class_symbol or
                    name == RMethod::NEW or name == RMethod::INITIALIZE

                psiElement = nil
                # Here we process pure Java method
                if method.type == Type::JAVA_METHOD
                    psiElement = method.psi_element
                    # We should override only methods, that can be overriden in Java
                    if psiElement and psiElement.valid? and
                            PsiUtil.can_be_overriden psiElement and !RubyOverrideImplementUtil.isAbstract psiElement
                        seen_names.add name
                        overriden_members << PsiMethodMember.new(psiElement)
                    end
                else
                # Here we process Ruby methods
                    prototype = method.getLastVirtualPrototype file_symbol
                    if prototype
                        psiElement = RVirtualPsiUtil.findPsiByVirtualElement prototype, project
                        if psiElement
                            seen_names.add name
                            overriden_members << RClassMember.new(psiElement)
                        end
                    end
                end

            end
        end

        overriden_members
    end

end
