# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 13:31:25
# To change this template use File | Settings | File Templates.

#start#

while foo() do
    a = boo()
    next
    b = boo2()
end

#stop#
#result#
0(1) element: null
1(2) element: While statement
2(3,11) element: Function call
3(4) element: Compound statement
4(5) element: Assignment expression
5(6) element: Function call
6(7) ASSIGN a
7(1) element: Next statement
8(9) element: Assignment expression
9(10) element: Function call
10(1) ASSIGN b
11() element: null