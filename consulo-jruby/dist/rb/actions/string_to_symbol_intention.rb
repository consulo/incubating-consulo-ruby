include Java

require File.dirname(__FILE__) + '/../idea/idea'
require File.dirname(__FILE__) + "/../util/reloadable_intention_action"
require File.dirname(__FILE__) + '/../util/psi_helper'

class StringToSymbolIntention
    include_class 'com.intellij.codeInsight.intention.IntentionAction' unless defined? IntentionAction
    include IntentionAction

    include_class 'org.jetbrains.plugins.ruby.RBundle' unless defined? RBundle
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.TextUtil' unless defined? TextUtil

    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil' unless defined? RubyPsiUtil
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RBaseString' unless defined? RBaseString
    include_class 'org.jetbrains.plugins.ruby.ruby.actions.intention.RubyIntentionUtil' unless defined? RubyIntentionUtil

    NAME = "StringToSymbol"
    TEXT = RBundle.message("ruby.intentions.string.to.symbol", [].to_java)

    def self.rnew
        ReloadableIntentionAction.new(:StringToSymbolIntention, __FILE__)
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
        canIntent?(base_string_at(psi_file, editor))
    end

    #void
    def invoke(project, editor, psi_file)
        string = base_string_at(psi_file, editor)
        symbol = RubyPsiUtil.getTopLevelElements(project, ":#{string.content}")[0]
        RubyPsiUtil.replaceInParent(string, [symbol].to_java(:'com.intellij.psi.PsiElement'))
    end

    #boolean
    def startInWriteAction
        true
    end

    private

    #RBaseString
    def base_string_at psi_file, editor
        PsiHelper.get_element_at editor, psi_file, RBaseString
    end

    def canIntent?(string)
        # We can replace only if string content is Constant or identifier
        return false unless string
        !string.hasExpressionSubstitutions and TextUtil.isCID string.content
    end

end