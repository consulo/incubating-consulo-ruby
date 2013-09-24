# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 13:31:25
# To change this template use File | Settings | File Templates.

#start#
if true
    a = 2
else
    a  = "dddd"
end

a.Foo
#stop#
#result#
0(1) element: null
1(2,5) element: If statement
2(3) element: Compound statement
3(4) element: Assignment expression
4(8) ASSIGN a
5(6) element: Else block
6(7) element: Assignment expression
7(8) ASSIGN a
8(9) element: Dot reference
9(10) CONSTANT a
10() element: null