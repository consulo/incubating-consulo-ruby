# Created by IntelliJ IDEA.
# User: oleg
# Date: Sep 21, 2007
# Time: 12:06:09 PM
# To change this template use File | Settings | File Templates.

include Java

require File.dirname(__FILE__) + '/refactoring/ruby_refactoring_provider'

module RefactoringLoader
    org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage::RUBY.
            setRubyRefactoringSupportProvider RubyRefactoringProvider.new
end
