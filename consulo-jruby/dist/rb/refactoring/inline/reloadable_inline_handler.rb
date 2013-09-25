# Created by IntelliJ IDEA.
# User: oleg
# Date: Dec 17, 2007
# Time: 6:00:52 PM
# To change this template use File | Settings | File Templates.
require File.dirname(__FILE__) + '/../../util/reloadable_base'

class ReloadableInlineHandler
    include_class 'com.intellij.lang.refactoring.InlineHandler'
    include InlineHandler
    include Reloadable

    # @Nullable Settings prepareInlineElement(PsiElement element, Editor editor, boolean invokedOnReference);
    def prepareInlineElement(element, editor, invokedOnReference)
        @instance.prepareInlineElement element, editor, invokedOnReference
    end

    # void removeDefinition(PsiElement element);
    def removeDefinition(element)
        @instance.removeDefinition element
    end

    # @Nullable Inliner createInliner (PsiElement element);
    def createInliner(element)
        @instance.createInliner element
    end

end