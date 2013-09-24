# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 13:31:25
# To change this template use File | Settings | File Templates.

#start#

for i in foo()
    boo(i)
end

#stop#
#result#
0(1) element: null
1(2) element: For statement
2(3,6) element: Function call
3(4) element: Compound statement
4(5) element: Function call
5(3,6) READ i
6() element: null