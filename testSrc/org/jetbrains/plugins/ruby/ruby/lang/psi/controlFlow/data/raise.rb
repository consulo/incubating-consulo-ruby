# Created by IntelliJ IDEA.
# User: oleg
# Date: 09.04.2008
# Time: 13:31:25
# To change this template use File | Settings | File Templates.

#start#

begin
    a = 12
    raise Exception.new
rescue
    foo(a)
ensure
    boo(a)
end
#stop#
#result#
0(1) element: null
1(2) element: Assignment expression
2(3) ASSIGN a
3(4) element: Command call
4(5) element: Function call
5(6) READ a
6(7) element: Function call
7(8) READ a
8() element: null