module C
    module N
        def self.foo
            puts "C::N::foo"
        end
    end
end

class A
    class N
        def self.foo
            puts "A::N::foo"
        end
    end
end

class B < A
#    include C
#
#    class N
#        def self.foo
#            puts "B::N::foo"
#        end
#    end
end

B::N::f#caret#oo