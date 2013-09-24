# load intention actions
require File.dirname(__FILE__) + '/intentions_loader'

# load refactoring support provider
require File.dirname(__FILE__) + '/refactoring_loader'

# load ruby override methods handler
require File.dirname(__FILE__) + '/override_loader'

# load ruby implement methods handler
require File.dirname(__FILE__) + '/implement_loader'

# load Rails parameter definitions
require File.dirname(__FILE__) + '/paramdefs_loader'

# load Rails settings
require File.dirname(__FILE__) + '/addins/rails/rails_settings_loader'