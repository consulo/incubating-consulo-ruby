# Created by IntelliJ IDEA.
# User: oleg
# Date: Sep 21, 2007
# Time: 11:15:12 AM
# To change this template use File | Settings | File Templates.

require File.dirname(__FILE__) + "/actions/string_to_symbol_intention.rb"
require File.dirname(__FILE__) + "/actions/qualified_name_to_include_class.rb"
require File.dirname(__FILE__) + "/actions/qualified_name_to_import.rb"
require File.dirname(__FILE__) + '/actions/java_full_class_name_intention'

module IntetionLoader
    RUBY_INTENTIONS = org.jetbrains.plugins.ruby.RBundle.message "ruby.intentions", [].to_java

    include_class 'org.jetbrains.plugins.ruby.ruby.actions.intention.RubyIntentionActionClassLoaderHack' unless defined? RubyIntentionActionClassLoaderHack

# registering StringToSymbol intention
    $intentionManager.registerIntentionAndMetaData(
            RubyIntentionActionClassLoaderHack.new(StringToSymbolIntention.rnew), [RUBY_INTENTIONS].to_java(:'java.lang.String'))
# registering QualifiedNameToIncludeClass intention
    $intentionManager.registerIntentionAndMetaData(
            RubyIntentionActionClassLoaderHack.new(QualifiedNameToIncludeClass.rnew), [RUBY_INTENTIONS].to_java(:'java.lang.String'))
# registering QualifiedNameToImportClass intention
    $intentionManager.registerIntentionAndMetaData(
            RubyIntentionActionClassLoaderHack.new(QualifiedNameToImportClass.rnew), [RUBY_INTENTIONS].to_java(:'java.lang.String'))
# registering JavaClassImportIntention
    $intentionManager.registerIntentionAndMetaData(
            RubyIntentionActionClassLoaderHack.new(JavaFullClassNameIntention.rnew), [RUBY_INTENTIONS].to_java(:'java.lang.String'))
end
