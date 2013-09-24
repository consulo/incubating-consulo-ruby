# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 13:31:25
# To change this template use File | Settings | File Templates.

#start#

begin
    a = boo()
    if a then
        break
    end
    a = boo2()
end while foo(a)

#stop#
#result#
0(1) element: null
1(2) element: While modifier statement
2(3) element: Function call
3(4,15) READ a
4(5) element: Block statement
5(6) element: Assignment expression
6(7) element: Function call
7(8) ASSIGN a
8(9) element: If statement
9(10,12) READ a
10(11) element: Compound statement
11(15) element: Break statement
12(13) element: Assignment expression
13(14) element: Function call
14(1) ASSIGN a
15() element: null