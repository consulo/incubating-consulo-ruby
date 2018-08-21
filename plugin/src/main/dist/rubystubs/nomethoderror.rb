=begin
 This is a machine generated stub using stdlib-doc for <b>class NoMethodError</b>
 Ruby sources used:  RUBY-1.8.6-p111
 Created on Fri Dec 07 13:57:12 +0300 2007 by IntelliJ IDEA Ruby Plugin.
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
class NoMethodError < NameError
    public
    #  
    # NoMethodError.new(msg, name [, args])  => no_method_error
    #   
    #    
    #    Construct a NoMethodError exception for a method of the given name
    #    called with the given arguments. The name may be accessed using
    #    the <code>#name</code> method on the resulting object, and the
    #    arguments using the <code>#args</code> method.
    #    
    # 
    def self.new(msg, name, *args)
        #This is a stub, used for indexing
    end
    public
    #  
    # no_method_error.args  => obj
    #   
    #    
    #    Return the arguments passed in as the third parameter to
    #    the constructor.
    #    
    # 
    def args()
        #This is a stub, used for indexing
    end
end
