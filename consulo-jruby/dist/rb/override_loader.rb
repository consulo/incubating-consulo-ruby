# Created by IntelliJ IDEA.
# User: oleg
# Date: Sep 28, 2007
# Time: 5:34:13 PM
# To change this template use File | Settings | File Templates.

include Java

require File.dirname(__FILE__) + '/override/ruby_override_handler'

module OverrideLoader
    org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage::RUBY.
            setRubyOverrideMethodsHandler RubyOverrideHandler.rnew
end