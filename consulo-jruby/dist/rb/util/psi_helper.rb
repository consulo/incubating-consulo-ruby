# Created by IntelliJ IDEA.
# User: oleg
# Date: Sep 19, 2007
# Time: 7:39:46 PM
# To change this template use File | Settings | File Templates.
include Java

require File.dirname(__FILE__) + '/generate_helper'

module PsiHelper
    include_class 'com.intellij.psi.util.PsiTreeUtil' unless defined? PsiTreeUtil
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement' unless defined? RPsiElement
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement' unless defined? RCompoundStatement
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpression' unless defined? RExpression
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil' unless defined? RubyPsiUtil
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.documentation.RubyHelpUtil' unless defined? RubyHelpUtil
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer' unless defined? RContainer

    class <<self
        # returns element of given class clazzz in current editor with given psi_file
        def get_element_at(editor, psi_file, clazzz)
            element = psi_file.find_element_at(editor.caretModel.offset)
            PsiTreeUtil.getParentOfType element, clazzz.java_class
        end

        # returns RPsiElement in current editor with given psi_file
        def get_rpsielement_at(editor, psi_file)
            get_element_at editor, psi_file, RPsiElement
        end

        def get_selected_element(project, editor, file, dataContext)
            selection_model = editor.selection_model
            if selection_model.has_selection
                element1 = file.find_element_at(selection_model.selection_start)
                element2 = file.find_element_at(selection_model.selection_end-1)
            else
                caret_model = editor.caret_model
                document = editor.document
                line_number = document.line_number caret_model.offset
                # if we cannot find correct line
                return nil unless 0 <= line_number && line_number < document.line_count
                element1 = file.find_element_at(document.line_start_offset(line_number))
                element2 = file.find_element_at(document.line_end_offset(line_number) - 1)
            end
            return nil unless element1 && element2
            parent = PsiTreeUtil.findCommonParent element1, element2
            if parent.kind_of? RPsiElement
                parent
            else
                PsiTreeUtil.getParentOfType(parent, RPsiElement.java_class)
            end
        end


        # inserts new statements in container after element
        # element - anchor element
        # new_elements - new generated elements list
        def insert_elements project, element, new_elements
            container = PsiTreeUtil.getParentOfType element, RContainer.java_class
            comp_statement = container.getBody.getBlock

            # looking for the following statement
            statements = comp_statement.getStatements

            @next_statement = nil
            statements.each do |statement|
                if RubyPsiUtil.isBefore(element, statement)
                    @next_statement = statement;
                    break
                end
            end

            if @next_statement
                elements_to_insert = []
                new_elements.each do |new_method|
                    elements_to_insert << new_method
                    elements_to_insert << GenerateHelper.generate_newline(project)
                end
                comments = RubyHelpUtil.getPsiComments(@next_statement)
                if comments.size > 0
                    RubyPsiUtil.addBeforeInParent(comments[0], elements_to_insert.to_java(:'com.intellij.psi.PsiElement'))
                else
                    RubyPsiUtil.addBeforeInParent(@next_statement, elements_to_insert.to_java(:'com.intellij.psi.PsiElement'))
                end
            else
                elements_to_insert = []
                elements_to_insert << GenerateHelper.generate_newline(project) if !statements.empty?
                new_elements.each do |new_method|
                    elements_to_insert << new_method
                    elements_to_insert << GenerateHelper.generate_newline(project)
                end
                RubyPsiUtil.addToEnd(comp_statement, elements_to_insert.to_java(:'com.intellij.psi.PsiElement'))
            end
            RubyPsiUtil.reformat container
        end
    end

end