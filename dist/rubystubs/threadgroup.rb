=begin
 This is a machine generated stub using stdlib-doc for <b>class ThreadGroup</b>
 Ruby sources used:  RUBY-1.8.6-p111
 Created on Fri Dec 07 14:09:25 +0300 2007 by IntelliJ IDEA Ruby Plugin.
=end

#   
#     <code>ThreadGroup</code> provides a means of keeping track of a number of
#     threads as a group. A <code>Thread</code> can belong to only one
#     <code>ThreadGroup</code> at a time; adding a thread to a new group will
#     remove it from any previous group.
#        
#     Newly created threads belong to the same group as the thread from which they
#     were created.
#    
class ThreadGroup < Object
    Default = nil #value is unknown, used for indexing.
    public
    #  
    # thgrp.list   => array
    #   
    #     
    #     Returns an array of all existing <code>Thread</code> objects that belong to
    #     this group.
    #        
    #        ThreadGroup::Default.list   #=> [#<Thread:0x401bdf4c run>]
    #    
    # 
    def list()
        #This is a stub, used for indexing
    end
    public
    #  
    # thgrp.enclose   => thgrp
    #   
    #     
    #     Prevents threads from being added to or removed from the receiving
    #     <code>ThreadGroup</code>. New threads can still be started in an enclosed
    #     <code>ThreadGroup</code>.
    #        
    #        ThreadGroup::Default.enclose        #=> #<ThreadGroup:0x4029d914>
    #        thr = Thread::new { Thread.stop }   #=> #<Thread:0x402a7210 sleep>
    #        tg = ThreadGroup::new               #=> #<ThreadGroup:0x402752d4>
    #        tg.add thr
    #   
    #     <em>produces:</em>
    #   
    #        ThreadError: can't move from the enclosed thread group
    #    
    # 
    def enclose()
        #This is a stub, used for indexing
    end
    public
    #  
    # thgrp.enclosed?   => true or false
    #   
    #     
    #     Returns <code>true</code> if <em>thgrp</em> is enclosed. See also
    #     ThreadGroup#enclose.
    #    
    # 
    def enclosed?()
        #This is a stub, used for indexing
    end
    public
    #  
    # thgrp.add(thread)   => thgrp
    #   
    #     
    #     Adds the given <em>thread</em> to this group, removing it from any other
    #     group to which it may have previously belonged.
    #        
    #        puts "Initial group is #{ThreadGroup::Default.list}"
    #        tg = ThreadGroup.new
    #        t1 = Thread.new { sleep }
    #        t2 = Thread.new { sleep }
    #        puts "t1 is #{t1}"
    #        puts "t2 is #{t2}"
    #        tg.add(t1)
    #        puts "Initial group now #{ThreadGroup::Default.list}"
    #        puts "tg group now #{tg.list}"
    #        
    #     <em>produces:</em>
    #        
    #        Initial group is #<Thread:0x401bdf4c>
    #        t1 is #<Thread:0x401b3c90>
    #        t2 is #<Thread:0x401b3c18>
    #        Initial group now #<Thread:0x401b3c18>#<Thread:0x401bdf4c>
    #        tg group now #<Thread:0x401b3c90>
    #    
    # 
    def add(thread)
        #This is a stub, used for indexing
    end
end
