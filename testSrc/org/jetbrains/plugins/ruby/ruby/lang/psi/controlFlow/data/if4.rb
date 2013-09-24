# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 13:31:25
# To change this template use File | Settings | File Templates.

#start#
a = "dddd"

if a
    a = a
    a = 12
end

a
#stop#
#result#
0(1) element: null
1(2) element: Assignment expression
2(3) ASSIGN a
3(4) element: If statement
4(5,11) READ a
5(6) element: Compound statement
6(7) element: Assignment expression
7(8) READ a
8(9) ASSIGN a
9(10) element: Assignment expression
10(11) ASSIGN a
11(12) READ a
12() element: null