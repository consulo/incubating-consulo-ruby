# attr_reader inner
class A
    attr_accessor :fo#caret#o
    def initialize
        @foo = "Hello, ruby"
    end

    def foo
        "I`m a slacker"
    end
end

A.new.foo