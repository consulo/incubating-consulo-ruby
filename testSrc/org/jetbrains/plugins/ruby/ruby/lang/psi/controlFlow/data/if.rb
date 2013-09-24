# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 13:31:25
# To change this template use File | Settings | File Templates.

#start#

if foo() then
    boo = 1
elsif foo1()
    boo = 2
else
    boo = 3
end

#stop#
#result#
0(1) element: null
1(2) element: If statement
2(3,6) element: Function call
3(4) element: Compound statement
4(5) element: Assignment expression
5(14) ASSIGN boo
6(7,11) element: Function call
7(8) element: Compound statement
8(9) element: Function call
9(10) element: Assignment expression
10(14) ASSIGN boo
11(12) element: Else block
12(13) element: Assignment expression
13(14) ASSIGN boo
14() element: null