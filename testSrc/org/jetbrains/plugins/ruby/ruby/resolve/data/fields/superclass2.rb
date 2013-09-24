# superclass test2
class A
    def initialize
        @aaa = 12
    end
end

class B < A
    def intialize
        @aaa
    end
    def foo
        puts #caret#@aaa
    end
end