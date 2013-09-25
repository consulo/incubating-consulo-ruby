=begin
 This is a machine generated stub using stdlib-doc for <b>class Method</b>
 Ruby sources used:  RUBY-1.8.6-p111
 Created on Fri Dec 07 13:52:55 +0300 2007 by IntelliJ IDEA Ruby Plugin.
=end

#   
#     <code>Proc</code> objects are blocks of code that have been bound to
#     a set of local variables. Once bound, the code may be called in
#     different contexts and still access those variables.
#        
#        def gen_times(factor)
#          return Proc.new {|n| n*factor }
#        end
#        
#        times3 = gen_times(3)
#        times5 = gen_times(5)
#        
#        times3.call(12)               #=> 36
#        times5.call(5)                #=> 25
#        times3.call(times5.call(4))   #=> 60
#        
#    
class Method < Object
    public
    #  
    # meth == other_meth  => true or false
    #   
    #    
    #    Two method objects are equal if that are bound to the same
    #    object and contain the same body.
    #    
    # 
    # 
    def == other_meth
        #This is a stub, used for indexing
    end
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
    # meth.call(args, ...)    => obj
    # meth[args, ...]         => obj
    #   
    #     
    #     Invokes the <i>meth</i> with the specified arguments, returning the
    #     method's return value.
    #        
    #        m = 12.method("+")
    #        m.call(3)    #=> 15
    #        m.call(20)   #=> 32
    #    
    # 
    def call(args, *smth)
        #This is a stub, used for indexing
    end
    public
    #  
    # meth.call(args, ...)    => obj
    # meth[args, ...]         => obj
    #   
    #     
    #     Invokes the <i>meth</i> with the specified arguments, returning the
    #     method's return value.
    #        
    #        m = 12.method("+")
    #        m.call(3)    #=> 15
    #        m.call(20)   #=> 32
    #    
    # 
    def [](args, *smth)
        #This is a stub, used for indexing
    end
    public
    #  
    # meth.arity    => fixnum
    #   
    #     
    #     Returns an indication of the number of arguments accepted by a
    #     method. Returns a nonnegative integer for methods that take a fixed
    #     number of arguments. For Ruby methods that take a variable number of
    #     arguments, returns -n-1, where n is the number of required
    #     arguments. For methods written in C, returns -1 if the call takes a
    #     variable number of arguments.
    #        
    #        class C
    #          def one;    end
    #          def two(a); end
    #          def three(*a);  end
    #          def four(a, b); end
    #          def five(a, b, *c);    end
    #          def six(a, b, *c, &d); end
    #        end
    #        c = C.new
    #        c.method(:one).arity     #=> 0
    #        c.method(:two).arity     #=> 1
    #        c.method(:three).arity   #=> -1
    #        c.method(:four).arity    #=> 2
    #        c.method(:five).arity    #=> -3
    #        c.method(:six).arity     #=> -3
    #        
    #        "cat".method(:size).arity      #=> 0
    #        "cat".method(:replace).arity   #=> 1
    #        "cat".method(:squeeze).arity   #=> -1
    #        "cat".method(:count).arity     #=> -1
    #    
    # 
    def arity()
        #This is a stub, used for indexing
    end
    public
    #  
    # meth.to_s      =>  string
    # meth.inspect   =>  string
    #   
    #     
    #     Show the name of the underlying method.
    #   
    #       "cat".method(:count).inspect   #=> "#<Method: String#count>"
    #    
    # 
    def inspect()
        #This is a stub, used for indexing
    end
    public
    #  
    # meth.to_s      =>  string
    # meth.inspect   =>  string
    #   
    #     
    #     Show the name of the underlying method.
    #   
    #       "cat".method(:count).inspect   #=> "#<Method: String#count>"
    #    
    # 
    def to_s()
        #This is a stub, used for indexing
    end
    public
    #  
    # meth.to_proc    => prc
    #   
    #     
    #     Returns a <code>Proc</code> object corresponding to this method.
    #    
    # 
    def to_proc()
        #This is a stub, used for indexing
    end
    public
    #  
    # meth.unbind    => unbound_method
    #   
    #     
    #     Dissociates <i>meth</i> from it's current receiver. The resulting
    #     <code>UnboundMethod</code> can subsequently be bound to a new object
    #     of the same class (see <code>UnboundMethod</code>).
    #    
    # 
    def unbind()
        #This is a stub, used for indexing
    end
end
