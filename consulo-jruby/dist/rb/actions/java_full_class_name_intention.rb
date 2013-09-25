# Created by IntelliJ IDEA.
# User: oleg
# Date: Sep 24, 2007
# Time: 1:53:47 PM
# To change this template use File | Settings | File Templates.
include Java

require File.dirname(__FILE__) + '/../../rb/idea/idea'
require File.dirname(__FILE__) + "/../util/reloadable_intention_action"
require File.dirname(__FILE__) + '/../util/psi_helper'
require File.dirname(__FILE__) + '/../util/execute_helper'


class JavaFullClassNameIntention
    include_class 'com.intellij.codeInsight.intention.IntentionAction' unless defined? IntentionAction
    include IntentionAction

    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.RFile' unless defined? RFile
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant' unless defined? RConstant
    include_class 'com.intellij.psi.search.GlobalSearchScope' unless defined? GlobalSearchScope
    include_class 'com.intellij.psi.search.GlobalSearchScope' unless defined? GlobalSearchScope
    include_class 'javax.swing.JList' unless defined? JList
    include_class 'com.intellij.openapi.ui.popup.PopupChooserBuilder' unless defined? PopupChooserBuilder

    include_class 'org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil' unless defined? ResolveUtil
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.impl.references.RReferenceNavigator' unless defined? RReferenceNavigator
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.impl.methodCall.RCallNavigator' unless defined? RCallNavigator

    include_class 'org.jetbrains.plugins.ruby.RBundle' unless defined? RBundle
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.TextUtil' unless defined? TextUtil

    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil' unless defined? RubyPsiUtil
    include_class 'org.jetbrains.plugins.ruby.ruby.actions.intention.RubyIntentionUtil' unless defined? RubyIntentionUtil

    NAME = "JavaFullClassNameIntention"
    TEXT = RBundle.message("ruby.intentions.java.full.class.name", [].to_java)

    def self.rnew
        ReloadableIntentionAction.new(:JavaFullClassNameIntention, __FILE__)
    end

    #String
    def getFamilyName
        NAME
    end

    #String
    def getText
        TEXT
    end

    #boolean
    def isAvailable(project, editor, psi_file)
        return false unless psi_file.kind_of? RFile and psi_file.isJRubyEnabled

        rpsi_element = PsiHelper.get_rpsielement_at editor, psi_file
        return false unless rpsi_element

        return false unless rpsi_element.kind_of? RConstant

        # we return true if this is not a command call
        return false if RReferenceNavigator.getReferenceByRightPart rpsi_element or
                RCallNavigator.getByCommand rpsi_element

        #try to fing java class with given name
        name = rpsi_element.text
        cache = psi_file.manager.getShortNamesCache
        classes = cache.getClassesByName name, GlobalSearchScope.allScope(project)
        classes.size > 0
    end

    #void
    def invoke(project, editor, psi_file)
        rpsi_element = PsiHelper.get_rpsielement_at editor, psi_file
        return unless rpsi_element

        #try to fing java class with given name
        name = rpsi_element.text
        cache = psi_file.manager.getShortNamesCache
        classes = cache.getClassesByName name, GlobalSearchScope.allScope(project)

        # if we have only 1 class we just import it
        if classes.size == 1
            insertImportClass project, rpsi_element, classes[0]
        elsif classes.size > 1
            list = JList.new classes;
            list.setCellRenderer com.intellij.ide.util.FQNameCellRenderer.new;

            proc = Proc.new do
                index = list.getSelectedIndex
                return if index == -1
                insertImportClass project, rpsi_element, classes[index]
            end
            runnable = ExecuteHelper::MyRunnable.new proc
            # PopupChooserBuilder BREAKS writeAction!!!!
            PopupChooserBuilder.new(list).
                    setTitle(RBundle.message("class.to.import.chooser.title", [].to_java)).
                    setItemChoosenCallback(runnable).createPopup().showInBestPositionFor(editor)
        end
    end

    def insertImportClass project, rpsi_element, clazzz
        qualifiedName = clazzz.getQualifiedName
        full_name = RubyPsiUtil.getTopLevelElements(project, "#{qualifiedName}")[0]
        # we need manually do it in write action!
        ExecuteHelper.run_as_command project, "import class" do
            ExecuteHelper.run_in_write_action do
                RubyPsiUtil.replaceInParent rpsi_element, [full_name].to_java(:'com.intellij.psi.PsiElement')
            end
        end
    end

    #boolean
    def startInWriteAction
        false
    end

end