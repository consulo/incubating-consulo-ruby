# Created by IntelliJ IDEA.
# User: oleg
# Date: Oct 2, 2007
# Time: 6:06:41 PM
# To change this template use File | Settings | File Templates.
include Java

require File.dirname(__FILE__) + '/implement/ruby_implement_handler'

module ImplementLoader
    org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage::RUBY.
            setRubyImplementMethodsHandler RubyImplementHandler.rnew
end