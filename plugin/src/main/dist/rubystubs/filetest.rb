=begin
 This is a machine generated stub using stdlib-doc for <b>module FileTest</b>
 Ruby sources used:  RUBY-1.8.6-p111
 Created on Fri Dec 07 14:11:59 +0300 2007 by IntelliJ IDEA Ruby Plugin.
=end

#   
#     <code>FileTest</code> implements file test operations similar to
#     those used in <code>File::Stat</code>. It exists as a standalone
#     module, and its methods are also insinuated into the <code>File</code>
#     class. (Note that this is not done by inclusion: the interpreter cheats).
#        
#    
module FileTest
    public
    #  
    # File.directory?(file_name)   =>  true or false
    #   
    #    
    #    Returns <code>true</code> if the named file is a directory,
    #    <code>false</code> otherwise.
    #   
    #       File.directory?(".")
    #    
    # 
    def directory?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.exist?(file_name)    =>  true or false
    # File.exists?(file_name)   =>  true or false    (obsolete)
    #   
    #    
    #    Return <code>true</code> if the named file exists.
    #    
    # 
    def exist?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.exist?(file_name)    =>  true or false
    # File.exists?(file_name)   =>  true or false    (obsolete)
    #   
    #    
    #    Return <code>true</code> if the named file exists.
    #    
    # 
    def exists?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.readable?(file_name)   => true or false
    #   
    #    
    #    Returns <code>true</code> if the named file is readable by the effective
    #    user id of this process.
    #    
    # 
    def readable?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.readable_real?(file_name)   => true or false
    #   
    #    
    #    Returns <code>true</code> if the named file is readable by the real
    #    user id of this process.
    #    
    # 
    def readable_real?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.writable?(file_name)   => true or false
    #   
    #    
    #    Returns <code>true</code> if the named file is writable by the effective
    #    user id of this process.
    #    
    # 
    def writable?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.writable_real?(file_name)   => true or false
    #   
    #    
    #    Returns <code>true</code> if the named file is writable by the real
    #    user id of this process.
    #    
    # 
    def writable_real?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.executable?(file_name)   => true or false
    #   
    #    
    #    Returns <code>true</code> if the named file is executable by the effective
    #    user id of this process.
    #    
    # 
    def executable?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.executable_real?(file_name)   => true or false
    #   
    #    
    #    Returns <code>true</code> if the named file is executable by the real
    #    user id of this process.
    #    
    # 
    def executable_real?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.file?(file_name)   => true or false
    #   
    #    
    #    Returns <code>true</code> if the named file exists and is a
    #    regular file.
    #    
    # 
    def file?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.zero?(file_name)   => true or false
    #   
    #    
    #    Returns <code>true</code> if the named file exists and has
    #    a zero size.
    #    
    # 
    def zero?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.size?(file_name)   => Integer or nil
    #   
    #    
    #    Returns +nil+ if +file_name+ doesn't exist or has zero size, the size of the
    #    file otherwise.
    #    
    # 
    def size?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.size(file_name)   => integer
    #   
    #    
    #    Returns the size of <code>file_name</code>.
    #    
    # 
    def size(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.owned?(file_name)   => true or false
    #   
    #    
    #    Returns <code>true</code> if the named file exists and the
    #    effective used id of the calling process is the owner of
    #    the file.
    #    
    # 
    def owned?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.grpowned?(file_name)   => true or false
    #   
    #    
    #    Returns <code>true</code> if the named file exists and the
    #    effective group id of the calling process is the owner of
    #    the file. Returns <code>false</code> on Windows.
    #    
    # 
    def grpowned?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.pipe?(file_name)   =>  true or false
    #   
    #    
    #    Returns <code>true</code> if the named file is a pipe.
    #    
    # 
    def pipe?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.symlink?(file_name)   =>  true or false
    #   
    #    
    #    Returns <code>true</code> if the named file is a symbolic link.
    #    
    # 
    def symlink?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.socket?(file_name)   =>  true or false
    #   
    #    
    #    Returns <code>true</code> if the named file is a socket.
    #    
    # 
    def socket?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.blockdev?(file_name)   =>  true or false
    #   
    #    
    #    Returns <code>true</code> if the named file is a block device.
    #    
    # 
    def blockdev?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.chardev?(file_name)   =>  true or false
    #   
    #    
    #    Returns <code>true</code> if the named file is a character device.
    #    
    def chardev?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.setuid?(file_name)   =>  true or false
    #   
    #    
    #    Returns <code>true</code> if the named file has the setuid bit set.
    #    
    # 
    def setuid?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.setgid?(file_name)   =>  true or false
    #   
    #    
    #    Returns <code>true</code> if the named file has the setgid bit set.
    #    
    # 
    def setgid?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.sticky?(file_name)   =>  true or false
    #   
    #    
    #    Returns <code>true</code> if the named file has the sticky bit set.
    #    
    # 
    def sticky?(file_name)
        #This is a stub, used for indexing
    end
    public
    #  
    # File.identical?(file_1, file_2)   =>  true or false
    #   
    #    
    #    Returns <code>true</code> if the named files are identical.
    #   
    #        open("a", "w") {}
    #        p File.identical?("a", "a")      #=> true
    #        p File.identical?("a", "./a")    #=> true
    #        File.link("a", "b")
    #        p File.identical?("a", "b")      #=> true
    #        File.symlink("a", "c")
    #        p File.identical?("a", "c")      #=> true
    #        open("d", "w") {}
    #        p File.identical?("a", "d")      #=> false
    #    
    # 
    def identical?(file_1, file_2)
        #This is a stub, used for indexing
    end
end
