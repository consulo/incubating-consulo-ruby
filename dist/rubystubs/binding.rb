=begin
 This is a machine generated stub using stdlib-doc for <b>class Binding</b>
 Ruby sources used:  RUBY-1.8.6-p111
 Created on Fri Dec 07 14:11:40 +0300 2007 by IntelliJ IDEA Ruby Plugin.
=end

#   
#     Objects of class <code>Binding</code> encapsulate the execution
#     context at some particular place in the code and retain this context 
#     for future use. The variables, methods, value of <code>self</code>,
#     and possibly an iterator block that can be accessed in this context
#     are all retained. Binding objects can be created using
#     <code>Kernel#binding</code>, and are made available to the callback
#     of <code>Kernel#set_trace_func</code>.
#        
#     These binding objects can be passed as the second argument of the
#     <code>Kernel#eval</code> method, establishing an environment for the
#     evaluation.
#        
#        class Demo
#          def initialize(n)
#            @secret = n
#          end
#          def getBinding
#            return binding()
#          end
#        end
#        
#        k1 = Demo.new(99)
#        b1 = k1.getBinding
#        k2 = Demo.new(-3)
#        b2 = k2.getBinding
#        
#        eval("@secret", b1)   #=> 99
#        eval("@secret", b2)   #=> -3
#        eval("@secret")       #=> nil
#        
#     Binding objects have no class-specific methods.
#        
#    
# 
class Binding < Object
    public
    #   
    #    MISSING: documentation
    #    
    # 
    def clone()
        #This is a stub, used for indexing
    end
    public
    #   
    #    MISSING: documentation
    #    
    # 
    def dup()
        #This is a stub, used for indexing
    end
end
