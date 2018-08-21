=begin
 This is a machine generated stub using stdlib-doc for <b>class Iconv</b>
 Ruby sources used:  RUBY-1.8.6-p111
 Created on Fri Dec 07 13:56:57 +0300 2007 by IntelliJ IDEA Ruby Plugin.
=end

#   
#    Document-class: Iconv::BrokenLibrary
#    
#    Detected a bug of underlying iconv(3) libray.
#    * returns an error without setting errno properly
#    
# 
class Iconv < Data
    public
    #  
    #  Iconv.open(to, from) { |iconv| ... }
    #    
    #    Equivalent to Iconv.new except that when it is called with a block, it
    #    yields with the new instance and closes it, and returns the result which
    #    returned from the block.
    #    
    def self.open(to, from)
        #This is a stub, used for indexing
    end
    public
    #  
    #  Iconv.iconv(to, from, *strs)
    #    
    #    Shorthand for
    #      Iconv.open(to, from) { |cd|
    #        (strs + [nil]).collect { |s| cd.iconv(s) }
    #      }
    #   
    #    === Parameters
    #   
    #    <tt>to, from</tt>:: see Iconv.new
    #    <tt>strs</tt>:: strings to be converted
    #   
    #    === Exceptions
    #   
    #    Exceptions thrown by Iconv.new, Iconv.open and Iconv#iconv.
    #    
    def self.iconv(to, from, *strs)
        #This is a stub, used for indexing
    end
    public
    #  
    #  Iconv.conv(to, from, str)
    #   
    #    Document-method: Iconv::conv
    #    
    #    Shorthand for
    #      Iconv.iconv(to, from, str).join
    #    See Iconv.iconv.
    #    
    def self.conv(to, from, str)
        #This is a stub, used for indexing
    end
    public
    #  
    #  Iconv.new(to, from)
    #    
    #    Creates new code converter from a coding-system designated with +from+
    #    to another one designated with +to+.
    #    
    #    === Parameters
    #   
    #    +to+::   encoding name for destination
    #    +from+:: encoding name for source
    #   
    #    === Exceptions
    #   
    #    TypeError::       if +to+ or +from+ aren't String
    #    InvalidEncoding:: if designated converter couldn't find out
    #    SystemCallError:: if <tt>iconv_open(3)</tt> fails
    #    
    def self.new(to, from)
        #This is a stub, used for indexing
    end
    public
    #   
    #    Finishes conversion.
    #   
    #    After calling this, calling Iconv#iconv will cause an exception, but
    #    multiple calls of #close are guaranteed to end successfully.
    #   
    #    Returns a string containing the byte sequence to change the output buffer to
    #    its initial shift state.
    #    
    def close()
        #This is a stub, used for indexing
    end
    public
    #  
    #  Iconv.iconv(to, from, *strs)
    #    
    #    Shorthand for
    #      Iconv.open(to, from) { |cd|
    #        (strs + [nil]).collect { |s| cd.iconv(s) }
    #      }
    #   
    #    === Parameters
    #   
    #    <tt>to, from</tt>:: see Iconv.new
    #    <tt>strs</tt>:: strings to be converted
    #   
    #    === Exceptions
    #   
    #    Exceptions thrown by Iconv.new, Iconv.open and Iconv#iconv.
    #    
    def iconv(to, from, *strs)
        #This is a stub, used for indexing
    end
    public
    #  
    #  Iconv.charset_map
    #    
    #    Returns the map from canonical name to system dependent name.
    #    
    def self.charset_map()
        #This is a stub, used for indexing
    end
    #   
    #    Base attributes for Iconv exceptions.
    #    
    module Failure
        public
        #  
        #  Iconv.new(to, from)
        #    
        #    Creates new code converter from a coding-system designated with +from+
        #    to another one designated with +to+.
        #    
        #    === Parameters
        #   
        #    +to+::   encoding name for destination
        #    +from+:: encoding name for source
        #   
        #    === Exceptions
        #   
        #    TypeError::       if +to+ or +from+ aren't String
        #    InvalidEncoding:: if designated converter couldn't find out
        #    SystemCallError:: if <tt>iconv_open(3)</tt> fails
        #    
        def self.new(to, from)
            #This is a stub, used for indexing
        end
        public
        #  
        #  success
        #    
        #    Returns string(s) translated successfully until the exception occurred.
        #    * In the case of failure occurred within Iconv.iconv, returned
        #      value is an array of strings translated successfully preceding
        #      failure and the last element is string on the way.
        #    
        def success()
            #This is a stub, used for indexing
        end
        public
        #  
        #  failed
        #    
        #    Returns substring of the original string passed to Iconv that starts at the
        #    character caused the exception. 
        #    
        def failed()
            #This is a stub, used for indexing
        end
        public
        #  
        #  inspect
        #    
        #    Returns inspected string like as: #<_class_: _success_, _failed_>
        #    
        def inspect()
            #This is a stub, used for indexing
        end
    end
    require 'argerror'
    #    
    #    Input conversion stopped due to an incomplete character or shift
    #    sequence at the end of the input buffer.
    #    
    class InvalidCharacter < ArgError
        include Iconv::Failure
    end
    #    
    #    Input conversion stopped due to an input byte that does not belong to
    #    the input codeset, or the output codeset does not contain the
    #    character.
    #    
    class IllegalSequence < ArgError
        include Iconv::Failure
    end
    #    
    #    Detected a bug of underlying iconv(3) libray.
    #    * returns an error without setting errno properly
    #    
    class BrokenLibrary < RuntimeError
        include Iconv::Failure
    end
    #    
    #    Iconv library internal error.  Must not occur.
    #    
    class OutOfRange < RuntimeError
        include Iconv::Failure
    end
    #    
    #    Requested coding-system is not available on this system.
    #    
    class InvalidEncoding < ArgError
        include Iconv::Failure
    end
end
