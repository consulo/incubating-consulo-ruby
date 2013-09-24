=begin
 This is a machine generated stub using stdlib-doc for <b>module GC</b>
 Ruby sources used:  RUBY-1.8.6-p111
 Created on Fri Dec 07 14:15:10 +0300 2007 by IntelliJ IDEA Ruby Plugin.
=end

#   
#     The <code>GC</code> module provides an interface to Ruby's mark and
#     sweep garbage collection mechanism. Some of the underlying methods
#     are also available via the <code>ObjectSpace</code> module.
#    
# 
module GC
    public
    #  
    # GC.start                     => nil
    # gc.garbage_collect           => nil
    # ObjectSpace.garbage_collect  => nil
    #   
    #     
    #     Initiates garbage collection, unless manually disabled.
    #   
    #    
    # 
    def self.start()
        #This is a stub, used for indexing
    end
    public
    #  
    # GC.enable    => true or false
    #   
    #     
    #     Enables garbage collection, returning <code>true</code> if garbage
    #     collection was previously disabled.
    #   
    #        GC.disable   #=> false
    #        GC.enable    #=> true
    #        GC.enable    #=> false
    #   
    #    
    # 
    def self.enable()
        #This is a stub, used for indexing
    end
    public
    #  
    # GC.disable    => true or false
    #   
    #     
    #     Disables garbage collection, returning <code>true</code> if garbage
    #     collection was already disabled.
    #   
    #        GC.disable   #=> false
    #        GC.disable   #=> true
    #   
    #    
    # 
    def self.disable()
        #This is a stub, used for indexing
    end
    public
    #  
    # GC.start                     => nil
    # gc.garbage_collect           => nil
    # ObjectSpace.garbage_collect  => nil
    #   
    #     
    #     Initiates garbage collection, unless manually disabled.
    #   
    #    
    # 
    def garbage_collect(*several_variants)
        #This is a stub, used for indexing
    end
end
