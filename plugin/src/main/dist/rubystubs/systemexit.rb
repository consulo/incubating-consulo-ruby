=begin
 This is a machine generated stub using stdlib-doc for <b>class SystemExit</b>
 Ruby sources used:  RUBY-1.8.6-p111
 Created on Fri Dec 07 14:11:41 +0300 2007 by IntelliJ IDEA Ruby Plugin.
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
class SystemExit < Exception
    public
    #  
    # SystemExit.new(status=0)   => system_exit
    #   
    #    
    #    Create a new +SystemExit+ exception with the given status.
    #    
    # 
    def self.new(status=0)
        #This is a stub, used for indexing
    end
    public
    #  
    # system_exit.status   => fixnum
    #   
    #    
    #    Return the status value associated with this system exit.
    #    
    # 
    def status()
        #This is a stub, used for indexing
    end
    public
    #  
    # system_exit.success?  => true or false
    #   
    #    
    #    Returns +true+ if exiting successful, +false+ if not.
    #    
    # 
    def success?()
        #This is a stub, used for indexing
    end
end
