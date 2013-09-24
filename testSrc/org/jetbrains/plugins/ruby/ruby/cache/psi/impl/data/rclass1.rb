# Created by IntelliJ IDEA.
# User: Roman.Chernyatchik
# Date: 11.08.2007
# Time: 15:09:44
# To change this template use File | Settings | File Templates.
require "rclass2"
class C0
  def self.static1
  end

  def method1
  end
end

class C1 < C0
  def method2
  end

  private
  def method2
  end
end

class C2 < C1
  def method4
  end
end
