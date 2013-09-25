include Java
require File.dirname(__FILE__) + "/reloadable_introduce_variable"
require File.dirname(__FILE__) + '/../../util/psi_helper'
require File.dirname(__FILE__) + '/../../util/execute_helper'
require File.dirname(__FILE__) + '/validator'

class IntroduceVariableHandler
    LOG = com.intellij.openapi.diagnostic.Logger.getInstance self.class.name

    include_class 'rb.refactoring.introduceVariable.RubyIntroduceVariableHandler' unless
            defined? RubyIntroduceVariableHandler
    include RubyIntroduceVariableHandler

    import com.intellij.psi.util.PsiTreeUtil
    import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpression
    import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil
    import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeHolder
    import org.jetbrains.plugins.ruby.ruby.refactoring.RubyRefactoringUtil
    import org.jetbrains.plugins.ruby.ruby.refactoring.introduceVariable.RubyIntroduceVariableDialog
    import org.jetbrains.plugins.ruby.ruby.refactoring.introduceVariable.NamesSuggestor
    import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.PseudoScopeHolder

    def self.rnew
        ReloadableIntroduceVariable.new(:IntroduceVariableHandler, __FILE__)
    end


    # void invokeOutter(@NotNull Project project, @NotNull PsiElement[] elements, @Nullable DataContext dataContext);
    def invokeOutter(project, elements, dataContext)
        # do nothing
    end

    # void invoke(@NotNull Project project, Editor editor, PsiFile file, @Nullable DataContext dataContext);
    def invoke(project, editor, file, dataContext)
        introduceVariable(project, editor, file, dataContext, nil, false)
    end


    # introduces variable
    # if name is nil, we show dialog to enter new name
    def introduceVariable(project, editor, file, dataContext, name, replace_all)
        expression = PsiHelper.get_selected_element project, editor, file, dataContext
        # we exit if we have no RExpression to introduce variable for
        return unless expression

        # Creating ruby introduce variable dialog
        search_scope = PsiTreeUtil.getParentOfType expression, ScopeHolder.java_class

        occurences = RubyRefactoringUtil.getOccurences expression, search_scope

        if occurences.size == 0 then
            LOG.error("Some error! Cannot find occurences!")
            return
        end

        # If name is given we don`t show any dialogs
        unless name
            validator = RubyValidator.new expression, occurences

            variants = []
            suggested_names = NamesSuggestor.getSuggestedNames expression
            suggested_names.each do |suggested_name|
                variants << suggested_name unless validator.check_possible_name(suggested_name)
            end

            dialog = RubyIntroduceVariableDialog.new project, occurences.size, validator, variants.to_java(:'java.lang.String')
            dialog.show

            # return if don`t want to introduce variable
            return unless dialog.isOK

            name = dialog.getName
            replace_all = dialog.doReplaceAllOccurrences
        end
        name.strip!

        # get anchor statement
        anchor_statement = get_anchor_statement(search_scope, expression, occurences, replace_all)
        return unless anchor_statement

        # creating new psiElements to insert
        assignment_text = "#{name} = #{expression.text}"
        assignment_element = RubyPsiUtil.createDummyRubyFile(project, assignment_text).
                getCompoundStatement.get_first_child

        names_text = "#{name}\n" * (replace_all ? occurences.size : 1)
        names_nodes = RubyPsiUtil.createDummyRubyFile(project, names_text).
                getCompoundStatement.getNode.getChildren nil

        ExecuteHelper.run_as_command project, "RubyIntroduceVariable" do
            ExecuteHelper.run_in_write_action do
                # If we have only 1 occurence and it is a statement, we just replace it with local variable definition
                if occurences.size == 1 and expression == RubyPsiUtil.getStatement(expression)
                    RubyPsiUtil.replaceInParent expression, [assignment_element].to_java(:"com.intellij.psi.PsiElement")
                else
                    # inserting definition
                    RubyPsiUtil.addBeforeInParent anchor_statement, [assignment_element, GenerateHelper.generate_newline(project)].to_java(:"com.intellij.psi.PsiElement")

                    # replace occurences
                    if replace_all
                        1.upto occurences.size do |index|
                            RubyPsiUtil.replaceInParent occurences[index-1], [names_nodes[(index-1)*2].psi].to_java(:"com.intellij.psi.PsiElement")
                        end
                    else
                        RubyPsiUtil.replaceInParent expression, [names_nodes[0].psi].to_java(:"com.intellij.psi.PsiElement")
                    end
                end
            end
        end
    end

    private
    def get_anchor_statement search_scope, expression, occurences, replace_all
        # Searching for the place to insert variable definition
        anchor = expression
        if replace_all
            # Here we look for a real scope
            parent = expression
            occurences.each do |occurence|
                parent = PsiTreeUtil.find_common_parent parent, occurence unless PsiTreeUtil.isAncestor parent, occurence, false
            end
            scope = parent.instance_of?(PseudoScopeHolder) ?
                    parent : PsiTreeUtil.getParentOfType(parent, PseudoScopeHolder.java_class)
            # Here scope is real scope
            occurences.each do |occurence|
                current_anchor = occurence
                # Find subscope for each occurence
                while (holder = PsiTreeUtil.getParentOfType(current_anchor, PseudoScopeHolder.java_class); holder!=scope)
                    current_anchor = holder
                end
                if RubyPsiUtil.isBefore current_anchor, anchor
                    anchor = current_anchor
                end
            end
        end
        RubyPsiUtil.getStatement anchor
    end
end