include Java

module M

    class InnerClass
    end

    def foo

    end

    aaa = 12

    AAA = 3444

    $AAA = "foo"

    @aa = @bbbb = @@cccc = "variables"

    class A < ::Outter#caret#Class
    end
end

class OutterClass
end

#result#
M
OutterClass