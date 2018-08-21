=begin
 This is a machine generated stub using stdlib-doc for <b>class NameError</b>
 Ruby sources used:  RUBY-1.8.6-p111
 Created on Fri Dec 07 14:08:08 +0300 2007 by IntelliJ IDEA Ruby Plugin.
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
class NameError < StandardError
    public
    #  
    # NameError.new(msg [, name])  => name_error
    #   
    #    
    #    Construct a new NameError exception. If given the <i>name</i>
    #    parameter may subsequently be examined using the <code>NameError.name</code>
    #    method.
    #    
    # 
    def self.new(msg, *name)
        #This is a stub, used for indexing
    end
    public
    #  
    # name_error.name    =>  string or nil
    #   
    #     
    #     Return the name associated with this NameError exception.
    #    
    # 
    def name()
        #This is a stub, used for indexing
    end
    public
    #  
    # name_error.to_s   => string
    #   
    #    
    #    Produce a nicely-formated string representing the +NameError+.
    #    
    # 
    def to_s()
        #This is a stub, used for indexing
    end
    #   
    #     Descendents of class <code>Exception</code> are used to communicate
    #     between <code>raise</code> methods and <code>rescue</code>
    #     statements in <code>begin/end</code> blocks. <code>Exception</code>
    #     objects carry information about the exception---its type (the
    #     exception's class name), an optional descriptive string, and
    #     optional traceback information. Programs may subclass 
    #     <code>Exception</code> to add additional information.
    #    
    class message < Data
    end
end
