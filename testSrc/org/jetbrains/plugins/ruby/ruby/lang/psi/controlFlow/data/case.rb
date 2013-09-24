# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 13:31:25
# To change this template use File | Settings | File Templates.

#start#
a = "ddd"
case foo()
when a
    boo1 = a
when b
    a; break
    boo1;
else
    boo1 = "sss"
end
#stop#
#result#
0(1) element: null
1(2) element: Assignment expression
2(3) ASSIGN a
3(4) element: Case statement
4(5) element: Function call
5(6) element: When case
6(7,10) READ a
7(8) element: Assignment expression
8(9) READ a
9(10) ASSIGN boo1
10(11,14) element: When case
11(12) READ a
12(17) element: Break statement
13(14) READ boo1
14(15) element: Else block
15(16) element: Assignment expression
16(17) ASSIGN boo1
17() element: null