# Created by IntelliJ IDEA.
# User: oleg
# Date: Sep 19, 2007
# Time: 7:07:54 PM
# To change this template use File | Settings | File Templates.
include Java

require File.dirname(__FILE__) + '/../../rb/idea/idea'
require File.dirname(__FILE__) + "/../util/reloadable_intention_action"
require File.dirname(__FILE__) + '/../util/psi_helper'
require File.dirname(__FILE__) + '/../../rb/util/generate_helper'

class QualifiedNameToIncludeClass
    include_class 'com.intellij.codeInsight.intention.IntentionAction' unless defined? IntentionAction
    include IntentionAction

    include_class 'org.jetbrains.plugins.ruby.RBundle' unless defined? RBundle
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.TextUtil' unless defined? TextUtil
    include_class 'org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil' unless defined? ResolveUtil

    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil' unless defined? RubyPsiUtil
    include_class 'org.jetbrains.plugins.ruby.ruby.actions.intention.RubyIntentionUtil' unless defined? RubyIntentionUtil

    NAME = "QualifiedNameToIncludeClass"
    TEXT = RBundle.message("ruby.intentions.qualified.name.to.include_class", [].to_java)

    def self.rnew
        ReloadableIntentionAction.new(:QualifiedNameToIncludeClass, __FILE__)
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
        rpsi_element = PsiHelper.get_rpsielement_at editor, psi_file
        return false unless rpsi_element

        # Look for element with reference
        unless !rpsi_element or ref = rpsi_element.reference
            rpsi_element = rpsi_element.parent
        end

        symbols = ResolveUtil.resolveToSymbols rpsi_element
        return false unless symbols.size == 1
        symbol = symbols[0]
        symbol.type == org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type::JAVA_CLASS
    end

    #void
    def invoke(project, editor, psi_file)
        rpsi_element = PsiHelper.get_rpsielement_at editor, psi_file
        return unless rpsi_element

        # Look for element with reference
        unless !rpsi_element or ref = rpsi_element.reference
            rpsi_element = rpsi_element.parent
        end

        symbols = ResolveUtil.resolveToSymbols rpsi_element
        return false unless symbols.size == 1
        symbol = symbols[0]

        # replacing qualified_name with short name
        constant = RubyPsiUtil.getTopLevelElements(project, "#{symbol.name}")[0]
        RubyPsiUtil.replaceInParent(rpsi_element, [constant].to_java(:'com.intellij.psi.PsiElement'))

        #adding include_class
        to_add = RubyPsiUtil.getTopLevelElements(project,
                "include_class '#{symbol.getPsiElement.getQualifiedName}'")[0]
        statement = RubyPsiUtil.getStatement constant
        RubyPsiUtil.addBeforeInParent statement, [to_add, GenerateHelper.generate_newline(project)].to_java(:'com.intellij.psi.PsiElement')
    end

    #boolean
    def startInWriteAction
        true
    end

end