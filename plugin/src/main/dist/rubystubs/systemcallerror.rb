=begin
 This is a machine generated stub using stdlib-doc for <b>class SystemCallError</b>
 Ruby sources used:  RUBY-1.8.6-p111
 Created on Fri Dec 07 13:52:52 +0300 2007 by IntelliJ IDEA Ruby Plugin.
=end

#   
#     Descendents of class <code>Exception</code> are used to communicate
#     between <code>raise</code> methods and <code>rescue</code>
#     statements in <code>begin/end</code> blocks. <code>Exception</code>
#     objects carry information about the exception---its type (the
#     exception's class name), an optional descriptive string, and
#     optional traceback information. Programs may subclass 
#     <code>Exception</code> to add additional information.
#    
class SystemCallError < StandardError
    public
    #  
    # SystemCallError.new(msg, errno)  => system_call_error_subclass
    #   
    #    
    #    If _errno_ corresponds to a known system error code, constructs
    #    the appropriate <code>Errno</code> class for that error, otherwise
    #    constructs a generic <code>SystemCallError</code> object. The
    #    error number is subsequently available via the <code>errno</code>
    #    method.
    #    
    # 
    def self.new(msg, errno)
        #This is a stub, used for indexing
    end
    public
    #  
    # system_call_error.errno   => fixnum
    #   
    #    
    #    Return this SystemCallError's error number.
    #    
    # 
    def errno()
        #This is a stub, used for indexing
    end
    public
    #  
    # system_call_error === other  => true or false
    #   
    #    
    #    Return +true+ if the receiver is a generic +SystemCallError+, or
    #    if the error numbers _self_ and _other_ are the same.
    #    
    # 
    # 
    def self.=== other
        #This is a stub, used for indexing
    end
end
