# Created by IntelliJ IDEA.
# User: oleg
# Date: Oct 2, 2007
# Time: 6:08:18 PM
# To change this template use File | Settings | File Templates.
require File.dirname(__FILE__) + '/reloadable_implement_handler'
require File.dirname(__FILE__) + '/../../rb/util/psi_helper'
require File.dirname(__FILE__) + '/../util/generate_helper'
require File.dirname(__FILE__) + '/../../rb/util/execute_helper'
require File.dirname(__FILE__) + '/../util/symbol_helper'
require 'set'

class RubyImplementHandler
    include_class 'rb.implement.ImplementHandler' unless defined? ImplementHandler
    include ImplementHandler

    include SymbolHelper
    include_class 'rb.override.RubyMemberChooser' unless defined? RubyMemberChooser
    include_class 'org.jetbrains.plugins.ruby.RBundle' unless defined? RBundle
    include_class 'org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type' unless defined? Type
    include_class 'rb.override.RClassMember' unless defined? RClassMember
    include_class "org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.RubyOverrideImplementUtil" unless defined? RubyOverrideImplementUtil
    include_class 'com.intellij.codeInsight.generation.PsiMethodMember' unless defined? PsiMethodMember
    import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types unless defined? Types


    def self.rnew
        ReloadableImplementHandler.new(:RubyImplementHandler, __FILE__)
    end

    #boolean isValidFor(Editor editor, PsiFile file);
    def isValidFor(editor, file)
        with_element_and_symbol(editor, file) do |element, symbol|
            return false unless element && symbol
            # Create ruby_class_members to override
            implement_members = create_implement_members symbol
            return implement_members.size > 0 ? true : false
        end
    end

    #void invoke(Project project, Editor editor, PsiFile file);
    def invoke(project, editor, file)
        with_element_and_symbol(editor, file) do |element, symbol|
            execute editor, project, element, symbol
        end
    end

    #void execute(@Nullable Editor editor, @NotNull Project project, @Nullable PsiElement element, @Nullable Symbol symbol);
    def execute(editor, project, element, symbol)
        return unless element && symbol

        # Create ruby_class_members to override
        implement_members = create_implement_members symbol

        # Creating override dialog
        ruby_member_chooser = RubyMemberChooser.new implement_members.to_java(:'com.intellij.codeInsight.generation.ClassMember'), project
        ruby_member_chooser.setTitle(RBundle.message("refactoring.implement.java.methods.title", [].to_java))
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
            if editor and !new_methods.empty?
                method = new_methods.first
                comment = method.getChildByFilter(org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes::TLINE_COMMENT, 0)
                editor.caret_model.moveToOffset comment.text_offset if comment
            end
        end
    end

    #boolean startInWriteAction();
    def startInWriteAction
        false
    end

    # Gathers info about methods to implement
    # if ruby class implements some java interface
    def create_implement_members class_symbol
        file_symbol = LastSymbolStorage.getInstance(class_symbol.project).getSymbol
        children = SymbolUtil.getAllChildrenWithSuperClassesAndIncludes file_symbol, org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context::INSTANCE, class_symbol, nil

        # we create new set to store seen names
        seen_names = Set.new

        # Here we gather info about methods to implement
        implement_members = []
        children.getSymbolsOfTypes(Types.METHODS).all.each do |method|
            name = method.name
            # Here we check weither method is java_method and is abstract
            if method.type == Type::JAVA_METHOD
                seen_names << name unless psi_method_abstract? method.psi_element
            else
                seen_names << name
            end
        end

        # Process only abstract java methods
        java_methods = children.getSymbolsOfTypes(Type::JAVA_METHOD.as_set).all.to_a.reverse
        java_methods.each do |method_symbol|
            psi_method = method_symbol.psi_element
            name = method_symbol.name
            if !seen_names.include? name and psi_method_abstract? psi_method
                seen_names << name
                implement_members << PsiMethodMember.new(psi_method)
            end
        end

        implement_members
    end

    def psi_method_abstract? psi_method
        psi_method and psi_method.valid? and RubyOverrideImplementUtil.isAbstract psi_method
    end

end
