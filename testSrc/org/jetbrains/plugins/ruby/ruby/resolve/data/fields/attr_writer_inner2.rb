# attr_reader inner2
class A
    attr_writer :foo
    def initialize
        @foo = "Hello, ruby"
    end

    def boo
        self.f#caret#oo = 12
    end
end
