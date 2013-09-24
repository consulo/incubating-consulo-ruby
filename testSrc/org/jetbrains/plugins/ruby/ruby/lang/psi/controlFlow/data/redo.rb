# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 13:31:25
# To change this template use File | Settings | File Templates.

#start#

while foo(a = 12) do
    boo(a)
    a = a + 12
    redo
end

#stop#
#result#
0(1) element: null
1(2) element: While statement
2(3) element: Function call
3(4) element: Assignment expression
4(5,12) ASSIGN a
5(6) element: Compound statement
6(7) element: Function call
7(8) READ a
8(9) element: Assignment expression
9(10) READ a
10(11) ASSIGN a
11(5) element: Redo statement
12() element: null