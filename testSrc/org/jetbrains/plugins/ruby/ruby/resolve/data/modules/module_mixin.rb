# module mixin
module A
    BB = "ttt"
    module A
        AA = "sss"
    end
end

class B
    include A
    puts #caret#A.constants
end