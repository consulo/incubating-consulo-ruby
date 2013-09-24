# attr_reader test
class A
    attr_writer :foo
    def initialize
        @foo = "Hello, ruby"
    end
end

A.new.fo#caret#o = 12