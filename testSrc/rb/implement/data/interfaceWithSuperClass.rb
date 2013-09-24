include Java

class B
    def foo
        #code here
    end
    def boo_super()
        #code here
    end
end

class A < B
    include java.Interface
end
#name#
A
#result#
boo():void
bar():void
foo_super():void
bar_super():void
