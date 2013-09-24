# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 13:31:25
# To change this template use File | Settings | File Templates.

#start#

while foo(a = 12) do
    boo(a + 3)
    if a then
        break
    end
    a = boo2()
end

#stop#
#result#
0(1) element: null
1(2) element: While statement
2(3) element: Function call
3(4) element: Assignment expression
4(5,15) ASSIGN a
5(6) element: Compound statement
6(7) element: Function call
7(8) READ a
8(9) element: If statement
9(10,12) READ a
10(11) element: Compound statement
11(15) element: Break statement
12(13) element: Assignment expression
13(14) element: Function call
14(1) ASSIGN a
15() element: null