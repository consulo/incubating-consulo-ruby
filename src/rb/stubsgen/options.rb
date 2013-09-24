#Copyright 2000-2007 JetBrains s.r.o.
#
#Licensed under the Apache License, Version 2.0 (the "License");
#you may not use this file except in compliance with the License.
#You may obtain a copy of the License at
#
#http://www.apache.org/licenses/LICENSE-2.0
#
#Unless required by applicable law or agreed to in writing, software
#distributed under the License is distributed on an "AS IS" BASIS,
#WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#See the License for the specific language governing permissions and
#limitations under the License.

# Created by IntelliJ IDEA.
# User: oleg
# Date: Jun 11, 2007

require 'settings'
require 'rdoc/options'

class Options

  alias old_parse parse

  def parse(argv, generators)
    old_parse argv, generators
      @op_dir = $DIRECTORY
      @generator_name = 'rb'
  end

end
