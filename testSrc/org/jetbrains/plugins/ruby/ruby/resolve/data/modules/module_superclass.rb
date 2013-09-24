# module in superclass
class A
    TT = "bbb"
    module A
        SS = "aaa"
    end
end

class B < A
    puts #caret#A.constants
end