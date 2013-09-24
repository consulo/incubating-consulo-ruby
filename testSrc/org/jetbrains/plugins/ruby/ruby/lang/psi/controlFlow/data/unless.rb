# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 13:31:25
# To change this template use File | Settings | File Templates.

#start#

unless foo(boo) then
    boo = 1
else
    boo = 3
end

#stop#
#result#
0(1) element: null
1(2) element: Unless statement
2(3) element: Function call
3(4,7) READ boo
4(5) element: Compound statement
5(6) element: Assignment expression
6(10) ASSIGN boo
7(8) element: Else block
8(9) element: Assignment expression
9(10) ASSIGN boo
10() element: null