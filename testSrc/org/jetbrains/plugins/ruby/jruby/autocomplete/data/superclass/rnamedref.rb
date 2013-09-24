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

    class A < ja#caret#va.util.CollectionImpl
    end
end
#result#
A
InnerClass
M