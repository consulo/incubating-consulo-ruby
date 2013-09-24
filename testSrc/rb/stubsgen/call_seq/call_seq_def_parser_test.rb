#Copyright 2000-2007 JetBrains s.r.o.
#
#Licensed under the Apache License, Version 2.0 (the "License");
#you may not use this file except in compliance with the License.
#You may obtain a copy of the License at
#
#http://www.apache.org/licenses/LICENSE-2.0
#
#Unless required by applicable law or agreed to in writing, software
#distributed under the License is distributed on an "AS IS" BASIS,
#WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#See the License for the specific language governing permissions and
#limitations under the License.

# Created by IntelliJ IDEA.
# User: Roman Chernyatchik
# Date: Nov 24, 2007

require File.dirname(__FILE__) + '/../../../../src/rb/stubsgen/call_seq/call_seq_def_parser'
require "test/unit"

class ParamsOfCallSeqTest < Test::Unit::TestCase
  def test_no_call_seq
    assert_equal(UNKNOWN_PARAMS_SET, _params_from_call_seq("smile", nil))
  end

  def test_wrong_operator_name
    check_call_seq([nil, nil, nil], "@-", "fix @- count     => integer")
    check_call_seq([nil, nil, nil], "&&", "fix && count     => integer")

    check_call_seq([nil, nil, nil], "!", "fix ! count     => integer")
    check_call_seq([nil, nil, nil], "~", "fix ~ count     => integer")

    check_call_seq([nil, nil, nil], "-@", "fix -@ count     => integer")
    check_call_seq([nil, nil, nil], "-@", "fix - count     => integer")

    check_call_seq([nil, nil, nil], "+@", "fix +@ count     => integer")
    check_call_seq([nil, nil, nil], "+@", "fix + count     => integer")

    check_call_seq([nil, nil, nil], "...", "fix ... count     => integer")

  end

  def test_wrong_method_name
    check_call_seq([nil, nil, nil], "next", "str.succ(bbb)   => new_str\nstr.succ(aaa)   => new_str\n")
  end


  def test_bad_format
    check_call_seq([nil, nil, nil], "times", "int.times qq qq {|i| block }     => int\n")
  end

  def test_multiple_defs
    check_call_seq(["str.succ", "", "=> new_str"], "succ", "str.succ   => new_str\nstr.next   => new_str\n")
    check_call_seq(["str.next", "", "=> new_str"], "next", "str.succ   => new_str\nstr.next   => new_str\n")
    check_call_seq(["str.next", "", "=> new_str"], "next", "str.succ   => new_str\nstr.succ   => new_str\nstr.next   => new_str\n")
    check_call_seq(["str.next", "", "=> new_str"], "next", "str.succ(aaa)   => new_str\nstr.next   => new_str\n")
    check_call_seq(["str.next", "", "=> new_str"], "next", "str.succ(aaa)   => new_str\nstr.next   => new_str\n")

    #*several_variants
    assert_equal(["str.succ", "(bbb)", "=> new_str"], _split_call_seq("succ", "str.succ(bbb)   => new_str\nstr.succ(aaa)   => new_str\n"))
    assert_equal(SEVERAL_VARIANTS, _params_from_call_seq("succ", "str.succ(bbb)   => new_str\nstr.succ(aaa)   => new_str\n"))

    check_call_seq(["File.exists?", "(file_name)", "=>  true or false    (obsolete)"], "exists?", "File.exist?(file_name)    =>  true or false\nFile.exists?(file_name)   =>  true or false    (obsolete)")
  end

  def test_no_params_no_block_one_def
    assert_equal(["WIN32OLE.codepage =", "CP", ""], _split_call_seq("codepage=", "WIN32OLE.codepage = CP\n"))
    assert_equal("(code_page)", _params_from_call_seq("codepage=", "WIN32OLE.codepage = CP\n"))

    check_call_seq(["int.integer?", "", "=> true"], "integer?", " int.integer? => true\n")
    check_call_seq(["int.integer!", "", "=> true"], "integer!", " int.integer! => true\n")
    check_call_seq(["int.integer=", "", "=> true"], "integer=", " int.integer= => true\n")
    check_call_seq(["int.inte_ger", "", "=> true"], "inte_ger", " int.inte_ger => true\n")
    check_call_seq(["int.chr", "", "=> string"], "chr", "int.chr    => string\n")
    check_call_seq(["int.chr", "", "=> string"], "chr", "int.chr    => string")
    check_call_seq(["int.chr", "", "=> string, int"], "chr", "int.chr    => string, int")

    check_call_seq(["Integer.induced_from", "", "=>  fixnum, bignum"], "induced_from", "Integer.induced_from    =>  fixnum, bignum\n")


    check_call_seq(["int.integer?", "", "=> true"], "integer?", "int.integer? => true\n")
    check_call_seq(["int.integer!", "", "=> true"], "integer!", "int.integer! => true\n")
    check_call_seq(["int.integer=", "", "=> true"], "integer=", "int.integer= => true\n")
    check_call_seq(["int.inte_ger", "", "=> true"], "inte_ger", "int.inte_ger => true\n")

    check_call_seq(["int.chr", "", "=> string"], "chr", "int.chr    => string\n")
    check_call_seq(["int.chr", "", "=>string"], "chr", "int.chr=>string\n")
    check_call_seq(["int.chr", "", "=> numeric_result"], "chr", "int.chr    => numeric_result\n")

    check_call_seq(["int.chr", "()", "=> string"], "chr", "int.chr()    => string\n")
    check_call_seq(["int.chr", "()", "=>string"], "chr", "int.chr()=>string\n")

    check_call_seq(["int.chr", "", "=> string, int"], "chr", "int.chr    => string, int\n")
    check_call_seq(["int.chr", "", "=> string,int"], "chr", "int.chr    => string,int\n")
  end

  def test_params_no_block_one_def
    check_call_seq(["Integer.induced_from", "(obj)", "=>  fixnum, bignum"], "induced_from", "Integer.induced_from(obj)    =>  fixnum, bignum\n")
    check_call_seq(["Integer.induced_from", "(obj, qq = {:ss => 'dd'})", "=>  fixnum, bignum"], "induced_from", "Integer.induced_from(obj, qq = {:ss => 'dd'})    =>  fixnum, bignum\n")

    assert_equal(["File.expand_path", "(file_name [, dir_string] )", "-> abs_file_name"], _split_call_seq("expand_path", "File.expand_path(file_name [, dir_string] ) -> abs_file_name\n"))
    assert_equal("(file_name, *dir_string)", _params_from_call_seq("expand_path", "File.expand_path(file_name [, dir_string] ) -> abs_file_name\n"))

    assert_equal(["File.basename", "(file_name [, suffix] )", "-> base_name"], _split_call_seq("basename", "File.basename(file_name [, suffix] ) -> base_name\n"))
    assert_equal("(file_name, *suffix)", _params_from_call_seq("basename", "File.basename(file_name [, suffix] ) -> base_name\n"))
  end

  def test_params_no_block_defs
    check_call_seq(["struct[", "symbol", "]    => anObject"], "[]", "struct[symbol]    => anObject")
    check_call_seq(["struct[", "symbol", "]=    => anObject"], "[]=", "struct[symbol]=    => anObject")
    check_call_seq(["struct[", "symbol", "] =    => anObject"], "[]=", "struct[symbol] =    => anObject")

    check_call_seq(["File::Stat.new","(file_name)", "=> stat"], "new", "File::Stat.new(file_name)  => stat")
    check_call_seq(["File.exists?","(file_name)", "=>  true or false    (obsolete)"], "exists?", "File.exists?(file_name)   =>  true or false    (obsolete)")
    check_call_seq(["params","(level, strategy)", ""], "params", "params(level, strategy)")

    check_call_seq(["params", "(level, strategy)", ""], "params", "params(level, strategy)")
    check_call_seq(["set_dictionary", "(string)", ""], "set_dictionary", "set_dictionary(string)")
    check_call_seq(["state.size", "", "=> integer for \"size?\""], "size", "state.size    => integer for \"size?\"")

    check_call_seq(["WIN32OLE_TYPE.new", "(typelib, ole_class)", "-> WIN32OLE_TYPE object"], "new", "WIN32OLE_TYPE.new(typelib, ole_class) -> WIN32OLE_TYPE object")
    check_call_seq(["WIN32OLE_TYPE#name", "", "#=> OLE type name"], "name", "WIN32OLE_TYPE#name #=> OLE type name")

    check_call_seq(["hsh.default =", "obj", "=> hsh"], "default=", "hsh.default = obj     => hsh")

    check_call_seq(["hsh.default =", "obj", "=> hsh"], "default=", "hsh.default = obj     => hsh")
    check_call_seq(["WIN32OLE.connect", "( ole )", "--> aWIN32OLE"], "connect", "WIN32OLE.connect( ole ) --> aWIN32OLE")


    check_call_seq(["mod <=>", "other_mod", "=> -1, 0, +1, or nil"], "<=>", "mod <=> other_mod   => -1, 0, +1, or nil")
    check_call_seq(["mod <", "other", "=>  true, false, or nil"], "<", "mod < other   =>  true, false, or nil")

    check_call_seq(["strio.string =", "string", "-> string"], "string=", "strio.string = string  -> string")
    check_call_seq(["nil.to_f", "", "=> 0.0"], "to_f", "nil.to_f    => 0.0")
    check_call_seq(["nil.to_s", "", "=> \"\""], "to_s", "nil.to_s    => \"\"")
    check_call_seq(["nil.to_a", "", "=> []"], "to_a", "nil.to_a    => []")
    check_call_seq(["nil.inspect", "", "=> \"nil\""], "inspect", "nil.inspect  => \"nil\"")
    check_call_seq(["socket.accept", "", "=> [ socket, string ]"], "accept", "socket.accept => [ socket, string ]")

    check_call_seq(["Process.wait2", "(pid=-1, flags=0)", "=> [pid, status]"], "wait2", " Process.wait2(pid=-1, flags=0)      => [pid, status]")
    check_call_seq(["sub", "(pattern, replacement)", "=> $_"], "sub", "sub(pattern, replacement)   => $_")

    check_call_seq(["Integer.induced_from","(obj)", "=>  fixnum, bignum"], "induced_from", "str.succ   => new_str\nInteger.induced_from(obj)    =>  fixnum, bignum\n")
    check_call_seq(["Integer.induced_from","(obj, qq = {:ss => 'dd'})", "=>  fixnum, bignum"], "induced_from", "str.succ   => new_str\nInteger.induced_from(obj, qq = {:ss => 'dd'})    =>  fixnum, bignum\n")

  end

  def test_manually_enterd
    assert_equal("(symbol)", _params_from_call_seq("catch", "catch(symbol) {| | block }  > obj"))
    assert_equal("(server, *host)", _params_from_call_seq("new", "WIN32OLE.new(server, [host]) -> WIN32OLE object"))
  end

  def test_no_params_block
    check_call_seq(["int.times", "", "{|i| block }     => int"], "times", "int.times {|i| block }     => int\n")
    check_call_seq(["int.times", "()", "{|i| block }     => int"], "times", "int.times() {|i| block }     => int\n")
  end

  def test_params_block
    check_call_seq(["int.times", "qq", "{|i| block }     => int"], "times", "int.times qq {|i| block }     => int\n")
    check_call_seq(["Thread.abort_on_exception=", "boolean", "=> true or false"], "abort_on_exception=", "Thread.abort_on_exception= boolean   => true or false")

    check_call_seq(["int.downto", "(limit)", "{|i| block }     => int"], "downto", "int.downto(limit) {|i| block }     => int\n")
    check_call_seq(["int.times", "(qq)", "{|i| block }     => int"], "times", "int.times(qq) {|i| block }     => int\n")
    check_call_seq(["int.times", "(qq)", "{|i| block }=>int"], "times", "int.times(qq){|i| block }=>int\n")
    check_call_seq(["int.times", "(qq, vv)", "{|i| block }     => int"], "times", "int.times(qq, vv) {|i| block }     => int\n")
    check_call_seq(["int.times", "(qq, vv)", "{|i, j| block }     => int"], "times", "int.times(qq, vv) {|i, j| block }     => int\n")
    check_call_seq(["int.times", "(qq, vv = 5)", "{|i, j| block }     => int"], "times", "int.times(qq, vv = 5) {|i, j| block }     => int\n")
    check_call_seq(["int.times", "(qq, {aa => dd, bbb => 'ddd', ddd => 4}, vv)", "{|i, j| block }     => int"], "times", "int.times(qq, {aa => dd, bbb => 'ddd', ddd => 4}, vv) {|i, j| block }     => int\n")

    check_call_seq(["flt <=>", "numeric", "=> -1, 0, +1"], "<=>", "flt <=> numeric   => -1, 0, +1")
  end


  #check_call_seq

  def test_params_no_block_bad_type_declaration
    check_call_seq(["int.integer", "", "-> true"], "integer", "int.integer -> true\n")
    check_call_seq(["int.integer?", "", "-> true"], "integer?", "int.integer? -> true\n")
    check_call_seq(["fix.id2name", "", "-> string or nil"], "id2name", "fix.id2name -> string or nil\n")
    check_call_seq(["pos=", "(n)", ""], "pos=", "pos=(n)")
  end

  def test_exceptional_cases
    check_call_seq(["WIN32OLE_PARAM#ole_type_detail", "", ""], "ole_type_detail", "WIN32OLE_PARAM#ole_type_detail\n")

    check_call_seq(["int.times", "(qq, {:w => s, :w => 'bb'})", "{|i| () block () } { } =>  int"], "times", "int.times(qq, {:w => s, :w => 'bb'}) {|i| () block () } { } =>  int\n")

    check_call_seq(["thr.raise", "(exception)", ""], "raise", "thr.raise(exception)")

    assert_equal(["hsh.values_at", "(key, ...)", "=> array"], _split_call_seq("values_at", "hsh.values_at(key, ...)   => array"))
    assert_equal("(key, *smth)", _params_from_call_seq("values_at", "hsh.values_at(key, ...)   => array"))

    assert_equal(["File.utime", "(atime, mtime, file_name,... )", "=>  integer"], _split_call_seq("utime", "File.utime(atime, mtime, file_name,... )   =>  integer"))
    assert_equal("(atime, mtime, file_name,*smth )", _params_from_call_seq("utime", "File.utime(atime, mtime, file_name,... )   =>  integer"))

    assert_equal(["File.chown", "(owner_int, group_int, file_name,... )", "-> integer"], _split_call_seq("chown", "File.chown(owner_int, group_int, file_name,... ) -> integer"))
    assert_equal("(owner_int, group_int, file_name,*smth )", _params_from_call_seq("chown", "File.chown(owner_int, group_int, file_name,... ) -> integer"))

    assert_equal(["include", "(module, ...)", "=> self"], _split_call_seq("include", "include(module, ...)    => self"))
    assert_equal("(module1, *smth)", _params_from_call_seq("include", "include(module, ...)    => self"))

    assert_equal("(key, value)", _params_from_call_seq("[]=", "hsh[key] = value        => value"))
    assert_equal(["hsh[","(key, value)", "] = value        => value"], _split_call_seq("[]=", "hsh[key] = value        => value"))

    assert_equal("(module1, filename)", _params_from_call_seq("autoload", "autoload(module, filename)   => nil"))
    assert_equal(["autoload", "(module, filename)", "=> nil"], _split_call_seq("autoload", "autoload(module, filename)   => nil"))
  end


  def test_operators
    assert_equal(["cont[", "args, ...", "]"], _split_call_seq("[]", "cont[args, ...]"))
    assert_equal("(args, *smth)", _params_from_call_seq("[]", "cont[args, ...]"))

    check_operator("[]");
    check_operator("[]=");
    check_operator("**");
    check_operator("+");
    check_operator("-");
    check_operator("*");
    check_operator("/");
    check_operator("%");
    check_operator(">>");
    check_operator("<<");
    check_operator("&");
    check_operator("^");
    check_operator("|");
    check_operator("<=");
    check_operator("<");
    check_operator(">");
    check_operator(">=");
    check_operator("<=>");
    check_operator("==");
    check_operator("===");
    check_operator("!=");
    check_operator("=~");
    check_operator("!~");

    check_call_seq(["~", "big", "=>  integer"], "~", " ~big  =>  integer")
    check_call_seq(["!", "big", "=>  integer"], "!", " !big  =>  integer")
    check_call_seq(["+", "big", "=>  integer"], "+@", " +big  =>  integer")
    check_call_seq(["-", "big", "=>  integer"], "-@", " -big  =>  integer")
    check_call_seq([nil, nil, nil], "&", " & big  =>  integer")
    check_call_seq([nil, nil, nil], "!", "fix ! count     => integer")

    check_call_seq(["fix +", "numeric", "=>  numeric_result"], "+", "fix + numeric   =>  numeric_result")
    check_call_seq(["`", "cmd", "`    => string"], "`", "`cmd`    => string ")
    check_call_seq(["array[", "index", "]                -> obj      or nil"], "[]", "array[index]                -> obj      or nil")
  end

  def test_method_params_syntax_is_ok
    assert _method_params_syntax_is_ok(" p")

    assert _method_params_syntax_is_ok("(p1, p2)")
    assert !_method_params_syntax_is_ok("(p1, p2, ...)")
    assert !_method_params_syntax_is_ok("(p1, p2, [p3]+)")
  end

  private
  def check_operator(operator_name)
    check_call_seq(["fix #{operator_name}", "count", "=> integer"], operator_name, "fix #{operator_name} count     => integer")
  end

  def check_call_seq(m_data, method_name, call_seq)
    m_def = m_data[0]
    m_params = m_data[1]
    m_tail = m_data[2]

    real_params =
            if !m_params
              UNKNOWN_PARAMS_SET
            elsif m_params.empty?
              "()"
            else
              m_params[0, 1] == "(" ? m_params : " #{m_params}"
            end
    assert_equal([m_def, m_params, m_tail], _split_call_seq(method_name, call_seq.strip))
    assert_equal(real_params, _params_from_call_seq(method_name, call_seq))
  end
end