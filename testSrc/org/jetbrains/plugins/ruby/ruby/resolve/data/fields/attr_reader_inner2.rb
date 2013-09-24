# attr_reader inner2
class A
    attr_reader :foo
    def initialize
        @foo = "Hello, ruby"
    end

    def boo
        puts f#caret#oo
    end
end
