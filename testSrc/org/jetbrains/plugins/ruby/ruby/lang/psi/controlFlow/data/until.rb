# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 13:31:25
# To change this template use File | Settings | File Templates.

#start#

until foo(a = 12) do
    boo(a)
end

#stop#
#result#
0(1) element: null
1(2) element: Until statement
2(3) element: Function call
3(4) element: Assignment expression
4(5,8) ASSIGN a
5(6) element: Compound statement
6(7) element: Function call
7(1) READ a
8() element: null