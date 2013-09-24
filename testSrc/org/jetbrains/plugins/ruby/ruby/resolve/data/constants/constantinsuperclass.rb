# constant in superclass
class A
    C = "Hello"
end

class B < A
    #caret#C
end