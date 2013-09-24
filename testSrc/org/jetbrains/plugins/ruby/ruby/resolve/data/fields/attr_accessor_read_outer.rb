# attr_reader test
class A
    attr_accessor :foo
    def initialize
        @foo = "Hello, ruby"
    end
end

A.new.fo#caret#o