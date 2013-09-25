=begin
 This is a machine generated stub using stdlib-doc for <b>class Class</b>
 Ruby sources used:  RUBY-1.8.6-p111
 Created on Fri Dec 07 13:56:30 +0300 2007 by IntelliJ IDEA Ruby Plugin.
=end

#   
#     Classes in Ruby are first-class objects---each is an instance of
#     class <code>Class</code>.
#        
#     When a new class is created (typically using <code>class Name ...
#     end</code>), an object of type <code>Class</code> is created and
#     assigned to a global constant (<code>Name</code> in this case). When
#     <code>Name.new</code> is called to create a new object, the
#     <code>new</code> method in <code>Class</code> is run by default.
#     This can be demonstrated by overriding <code>new</code> in
#     <code>Class</code>:
#        
#        class Class
#           alias oldNew  new
#           def new(*args)
#             print "Creating a new ", self.name, "\n"
#             oldNew(*args)
#           end
#         end
#        
#        
#         class Name
#         end
#        
#        
#         n = Name.new
#        
#     <em>produces:</em>
#        
#        Creating a new Name
#        
#     Classes, modules, and objects are interrelated. In the diagram
#     that follows, the vertical arrows represent inheritance, and the
#     parentheses meta-classes. All metaclasses are instances 
#     of the class `Class'.
#   
#                               +------------------+
#                               |                  |
#                 Object---->(Object)              |
#                  ^  ^        ^  ^                |
#                  |  |        |  |                |
#                  |  |  +-----+  +---------+      |
#                  |  |  |                  |      |
#                  |  +-----------+         |      |
#                  |     |        |         |      |
#           +------+     |     Module--->(Module)  |
#           |            |        ^         ^      |
#      OtherClass-->(OtherClass)  |         |      |
#                                 |         |      |
#                               Class---->(Class)  |
#                                 ^                |
#                                 |                |
#                                 +----------------+
#    
class Class < Module
    public
    #  
    # inherited(subclass)
    #   
    #    
    #    Callback invoked whenever a subclass of the current class is created.
    #   
    #    Example:
    #   
    #       class Foo
    #          def self.inherited(subclass)
    #             puts "New subclass: #{subclass}"
    #          end
    #       end
    #   
    #       class Bar < Foo
    #       end
    #   
    #       class Baz < Bar
    #       end
    #   
    #    produces:
    #   
    #       New subclass: Bar
    #       New subclass: Baz
    #    
    def inherited(subclass)
        #This is a stub, used for indexing
    end
    public
    #  
    # class.allocate()   =>   obj
    #   
    #     
    #     Allocates space for a new object of <i>class</i>'s class. The
    #     returned object must be an instance of <i>class</i>.
    #        
    #    
    # 
    def allocate()
        #This is a stub, used for indexing
    end
    public
    #  
    # class.new(args, ...)    =>  obj
    #   
    #     
    #     Calls <code>allocate</code> to create a new object of
    #     <i>class</i>'s class, then invokes that object's
    #     <code>initialize</code> method, passing it <i>args</i>.
    #     This is the method that ends up getting called whenever
    #     an object is constructed using .new.
    #        
    #    
    # 
    def new(args, *smth)
        #This is a stub, used for indexing
    end
    public
    #  
    # Class.new(super_class=Object)   =>    a_class
    #   
    #     
    #     Creates a new anonymous (unnamed) class with the given superclass
    #     (or <code>Object</code> if no parameter is given). You can give a
    #     class a name by assigning the class object to a constant.
    #        
    #    
    # 
    def self.new(super_class=Object)
        #This is a stub, used for indexing
    end
    public
    #  
    # class.superclass -> a_super_class or nil
    #   
    #     
    #     Returns the superclass of <i>class</i>, or <code>nil</code>.
    #        
    #        File.superclass     #=> IO
    #        IO.superclass       #=> Object
    #        Object.superclass   #=> nil
    #        
    #    
    # 
    def superclass()
        #This is a stub, used for indexing
    end
end
