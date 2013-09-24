# mixin test
module A
    def foo
        @aaa
    end
end

class B
    include A
    def boo
        @a#caret#aa
    end
end