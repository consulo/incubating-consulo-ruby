def foo
end

class <<self
    def foo
#caret#
    end
end
#result#
0