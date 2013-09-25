# Created by IntelliJ IDEA.
# User: oleg
# Date: Oct 1, 2007
# Time: 4:21:49 PM
# To change this template use File | Settings | File Templates.
module GenerateHelper

    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod' unless
            defined? RMethod
    include_class 'com.intellij.psi.PsiMethod' unless defined? PsiMethod
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RSingletonMethod' unless
            defined? RSingletonMethod
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo' unless
            defined? ArgumentInfo
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil' unless defined? RubyPsiUtil
    include_class 'org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF' unless defined? BNF

    COMMENT_TEXT = "#code here" unless defined? COMMENT_TEXT

    # Generates new method with the same signature
    def self.generate_new_method element
        # Ruby method
        if element.kind_of? RMethod
            text = "def "
            text << "self." if element.kind_of? RSingletonMethod
            text << "#{element.name}("
            first = true
            # arguments generating
            element.getArgumentInfos.each do |arg_info|
                text << ',' unless first
                first = false

                type = arg_info.type

                if type == ArgumentInfo::Type::SIMPLE
                    text << "#{arg_info.name}"
                elsif type == ArgumentInfo::Type::PREDEFINED
                    text << "#{arg_info.name}=nil"
                elsif type == ArgumentInfo::Type::ARRAY
                    text << "*#{arg_info.name}"
                elsif type == ArgumentInfo::Type::BLOCK
                    text << "&#{arg_info.name}"
                end
            end
            text << ")\n#{COMMENT_TEXT}\nend"

            # text is ok now. Creating psiElement by text
            RubyPsiUtil.getTopLevelElements(element.project, text)[0]

        # PsiMethod
        elsif element.kind_of? PsiMethod
            modifiers = element.getModifierList()

            text = "def "

            text << "self." if modifiers.hasModifierProperty(com.intellij.psi.PsiModifier::STATIC)
            text << "#{element.name}("
            first = true
            # arguments generating
            element.getParameterList().getParameters().each do |arg|
                text << ',' unless first
                first = false
                text << "#{fix_name arg.name}"
            end
            text << ")\n#{COMMENT_TEXT}\nend"

            # text is ok now. Creating psiElement by text
            RubyPsiUtil.getTopLevelElements(element.project, text)[0]
        else
            nil
        end
    end

    # Generates newline
    def self.generate_newline project
        dummy_file = RubyPsiUtil.createDummyRubyFile(project, "roman_is_a_butthead\nIDEA_Rulezzz")
        dummy_file.getCompoundStatement.getNode.getChildren(nil)[1].getPsi
    end

    def self.fix_name name
        BNF::KALL_RESWORDS.getTypes.each do |ielement_type|
            if name == ielement_type.to_string
                return "#{name}1"
            end
        end
        name
    end
end