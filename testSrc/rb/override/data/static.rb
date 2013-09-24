class A
    def self.foo
    end
end

class B < A
end
#name#
B
#result#
superclass
allocate
inherited (subclass)
class_variable_set (symbol, obj)
class_variable_get (symbol)
remove_class_variable (sym)
class_variables
const_missing (sym)
remove_const (sym)
const_defined? (sym)
const_set (sym, obj)
const_get (sym)
constants
class_variable_defined? (symbol)
private_instance_methods (include_super=...)
protected_instance_methods (include_super=...)
public_instance_methods (include_super=...)
instance_methods (include_super=...)
attr_accessor (symbol, *smth)
attr_writer (symbol, *smth)
attr_reader (symbol, *smth)
attr (symbol, writable=...)
ancestors
name
include? (module1)
included_modules
to_s
>= other
> other
<= other
< other
<=> other_mod
== other
=== obj
freeze
method_undefined (p1)
method_removed (p1)
method_added (p1)
extended (p1)
included (othermod)
instance_method (symbol)
autoload? (name)
autoload (name, filename)
define_method (*several_variants)
alias_method (new_name, old_name)
undef_method (symbol)
remove_method (symbol)
class_eval (string, *filename_lineno)
module_eval
private_class_method (symbol, *smth)
public_class_method (symbol, *smth)
protected_method_defined? (symbol)
private_method_defined? (symbol)
public_method_defined? (symbol)
method_defined? (symbol)
module_function (symbol, *smth)
protected (*several_variants)
extend_object (obj)
append_features (mod)
enum_for (method=..., *args)
to_enum (method=..., *args)
singleton_method_undefined (symbol)
singleton_method_removed (symbol)
singleton_method_added (symbol)
is_a? (class1)
kind_of? (class1)
instance_of? (class1)
remove_instance_variable (symbol)
instance_variable_defined? (symbol)
instance_variable_set (symbol, obj)
instance_variable_get (symbol)
instance_variables
public_methods (all=...)
private_methods (all=...)
protected_methods (all=...)
singleton_methods (all=...)
methods
inspect
to_a
frozen?
untaint
tainted?
taint
dup
clone
class
type
id
eql? (other)
=~ other
equal? (other)
nil?
display (port=...)
object_id
__id__
hash
method (sym)
extend (module1, *args)
instance_eval (*several_variants)
__send__ (symbol, *args)
send (symbol, *args)
respond_to? (symbol, include_private=...)
public (*several_variants)
private (*several_variants)
include (module1, *smth)
scan (*several_variants)
split *pattern_limit
chomp! (*several_variants)
chomp (*several_variants)
chop!
chop
gsub! (*several_variants)
sub! (*several_variants)
gsub (*several_variants)
sub (*several_variants)
trap (*several_variants)
rand (max=...)
srand (number=...)
sleep (*duration)
system (cmd, *arg)
exit! (fixnum=...)
fork
exec (command, *arg)
Array (arg)
String (arg)
Float (arg)
Integer (arg)
format (format_string, *args)
sprintf (format_string, *args)
p (obj, *smth)
` cmd
readlines (separator=...)
select (read_array, *write_error_arrays_timeout)
getc
readline (separator=...)
gets (separator=...)
puts (obj, *smth)
putc (int)
print (obj, *smth)
printf (*several_variants)
open (*several_variants)
syscall (fixnum, *args)
test (int_cmd, file1, *file2)
callcc
binding
lambda
proc
require (string)
load (filename, wrap=...)
set_trace_func (*several_variants)
untrace_var (symbol, *cmd)
trace_var (*several_variants)
local_variables
global_variables
throw (symbol, *obj)
catch (symbol)
at_exit
abort
exit (integer=...)
caller (start=...)
fail (*several_variants)
raise (*several_variants)
loop
method_missing (symbol, *args)
block_given?
iterator?
eval (string, *binding_filename_lineno)
warn (msg)