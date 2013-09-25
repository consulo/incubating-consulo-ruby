# Created by IntelliJ IDEA.
# @author: Roman Chernyatchik
# @date: Apr 11, 2008
require "java"

require File.dirname(__FILE__) + '/../../resources/facet_rails_settings'
require File.dirname(__FILE__) + '/../../util/reloadable_base'

import org.jetbrains.plugins.ruby.rails.ExternalRailsSettings
include_class 'org.jetbrains.plugins.ruby.rails.ExternalRailsSettingsDelegate'

class ReloadableFacetRailsSettingsDelegate
    include ExternalRailsSettingsDelegate
    include Reloadable

    def getRailsScriptDataBaseTypes()
        reload
        @instance.getRailsScriptDataBaseTypes
    end

    def getRailsServersTypes()
        reload
        @instance.getRailsServersTypes
    end

    def getRailsServersTitlesByType(serverType)
        reload
        @instance.getRailsServersTitlesByType serverType
    end
end

class FacetRailsSettingsDelegate
    include ExternalRailsSettingsDelegate

    def self.rnew
        ReloadableFacetRailsSettingsDelegate.new(:FacetRailsSettingsDelegate, __FILE__)
    end

    def getRailsScriptDataBaseTypes()
        RAILS_SCRIPT_DATABASE_TYPES.to_java(:'java.lang.String')
    end

    def getRailsServersTypes()
        RAILS_SERVER_TYPES.keys.to_java(:'java.lang.String')
    end
    def getRailsServersTitlesByType(serverType)
        RAILS_SERVER_TYPES[serverType.to_sym];
    end



end

ExternalRailsSettings.get_instance.setExternalRailsSettingsDelegate FacetRailsSettingsDelegate.rnew