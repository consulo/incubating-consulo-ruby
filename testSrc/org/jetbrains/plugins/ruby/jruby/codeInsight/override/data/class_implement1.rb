include Java

class A
    include java.TestInterface2
    def foo
#caret#
    end
end
#result#
1