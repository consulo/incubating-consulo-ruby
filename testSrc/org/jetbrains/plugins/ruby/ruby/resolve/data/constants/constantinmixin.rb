# constant in mixin
module A
    C = "Hello"
end

class B
    include A
    #caret#C
end