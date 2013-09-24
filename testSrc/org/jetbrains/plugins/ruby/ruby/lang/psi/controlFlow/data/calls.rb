# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 14:24:19
# To change this template use File | Settings | File Templates.

#start#

a = b = "foo"
foo call1(a,b,c), call2(a,b,c)
call3 a,b,c

#stop#
#result#
0(1) element: null
1(2) element: Assignment expression
2(3) element: Assignment expression
3(4) ASSIGN b
4(5) ASSIGN a
5(6) element: Command call
6(7) element: Function call
7(8) READ a
8(9) READ b
9(10) element: Function call
10(11) READ a
11(12) READ b
12(13) element: Command call
13(14) READ a
14(15) READ b
15() element: null