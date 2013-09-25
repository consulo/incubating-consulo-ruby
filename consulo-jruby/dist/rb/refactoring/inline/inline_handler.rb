# Created by IntelliJ IDEA.
# User: oleg
# Date: Dec 17, 2007
# Time: 5:45:54 PM
# To change this template use File | Settings | File Templates.

require File.dirname(__FILE__) + '/reloadable_inline_handler'

class RubyInlineHandler
    include_class 'com.intellij.lang.refactoring.InlineHandler' unless defined? InlineHandler
    include InlineHandler

    def self.rnew
        ReloadableInlineHandler.new(:RubyInlineHandler, __FILE__)
    end

    # @Nullable Settings prepareInlineElement(PsiElement element, Editor editor, boolean invokedOnReference);
    def prepareInlineElement(element, editor, invokedOnReference)
        nil
    end

    # void removeDefinition(PsiElement element);
    def removeDefinition(element)

    end

    # @Nullable Inliner createInliner (PsiElement element);
    def createInliner(element)
        nil
    end
end