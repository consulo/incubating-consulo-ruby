# Created by IntelliJ IDEA.
# User: oleg
# Date: Oct 2, 2007
# Time: 7:30:39 PM
# To change this template use File | Settings | File Templates.

module SymbolHelper
    include_class 'com.intellij.psi.util.PsiTreeUtil' unless defined? PsiTreeUtil
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer' unless defined? RContainer
    include_class 'org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil' unless defined? SymbolUtil
    include_class 'org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type' unless defined? Type
    include_class 'org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.LastSymbolStorage' unless defined? LastSymbolStorage

    def with_element_and_symbol editor, file
        # here we find symbol for given location
        element = file.find_element_at editor.caret_model.offset
        unless element
            yield nil, nil
            return
        end

        container = PsiTreeUtil.getParentOfType element, RContainer.java_class
        unless container
            yield nil, nil
            return
        end


        file_symbol = LastSymbolStorage.getInstance(element.project).symbol
        symbol = SymbolUtil.getSymbolByContainer file_symbol, container
        unless symbol
            yield nil, nil
            return
        end

        # check for symbol type. We should perform only if we`re in module or class
        type = symbol.getType
        unless type == Type::CLASS or type == Type::MODULE
            yield nil, nil
            return
        end
        # if everything is ok)
        yield element, symbol
    end
end