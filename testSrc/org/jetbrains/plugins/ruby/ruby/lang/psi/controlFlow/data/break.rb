# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 13:31:25
# To change this template use File | Settings | File Templates.

#start#
aa = 1
while foo() do
    boo(a)
    if aa then
        break
    elsif b = 555
        bla_bla_bla b
        break
    end
    boo2(b)
end

#stop#
#result#
0(1) element: null
1(2) element: Assignment expression
2(3) ASSIGN aa
3(4) element: While statement
4(5,21) element: Function call
5(6) element: Compound statement
6(7) element: Function call
7(8) element: If statement
8(9,11) READ aa
9(10) element: Compound statement
10(21) element: Break statement
11(12) element: Assignment expression
12(13,19) ASSIGN b
13(14) element: Compound statement
14(15) element: Assignment expression
15(16) ASSIGN b
16(17) element: Command call
17(18) READ b
18(21) element: Break statement
19(20) element: Function call
20(3) READ b
21() element: null