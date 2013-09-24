include Java

class B < java.AbstractClass2
    def foo
    end
end

class A < B
    def foo
#caret#
    end
end
#result#
2