# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 13:31:25
# To change this template use File | Settings | File Templates.

#start#

if true
    fooo(a = 1)
    return a + 1
else
    return a + 2
    boo(a)
end
bla_bla_bla(a)
#stop#
#result#
0(1) element: null
1(2,8) element: If statement
2(3) element: Compound statement
3(4) element: Function call
4(5) element: Assignment expression
5(6) ASSIGN a
6(7) element: Return statement
7(15) READ a
8(9) element: Else block
9(10) element: Return statement
10(15) READ a
11(12) element: Function call
12(13) READ a
13(14) element: Function call
14(15) READ a
15() element: null