# superclass test
class A
    def initialize
        @aaa = 12
    end
end

class B < A
    def foo
        puts #caret#@aaa
    end
end