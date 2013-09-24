# simple fields test
class A
    def initialize
        @aaa = 12
    end

    def foo
        #caret#@aaa
    end
end