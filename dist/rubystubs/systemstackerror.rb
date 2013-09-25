=begin
 This is a machine generated stub using stdlib-doc for <b>class SystemStackError</b>
 Ruby sources used:  RUBY-1.8.6-p111
 Created on Fri Dec 07 13:53:05 +0300 2007 by IntelliJ IDEA Ruby Plugin.
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
class SystemStackError < StandardError
end
