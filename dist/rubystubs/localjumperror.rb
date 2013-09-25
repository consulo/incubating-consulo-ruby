=begin
 This is a machine generated stub using stdlib-doc for <b>class LocalJumpError</b>
 Ruby sources used:  RUBY-1.8.6-p111
 Created on Fri Dec 07 14:02:55 +0300 2007 by IntelliJ IDEA Ruby Plugin.
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
class LocalJumpError < StandardError
    public
    #   
    #    call_seq:
    #      local_jump_error.exit_value  => obj
    #   
    #    Returns the exit value associated with this +LocalJumpError+.
    #    
    def exit_value()
        #This is a stub, used for indexing
    end
    public
    #  
    # local_jump_error.reason   => symbol
    #   
    #    
    #    The reason this block was terminated:
    #    :break, :redo, :retry, :next, :return, or :noreason.
    #    
    # 
    def reason()
        #This is a stub, used for indexing
    end
end
