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

#TODO move to some NameSpace / utility module

def params_from_call_seq(method)
  _params_from_call_seq(method.name, method.call_seq)
end

def _params_from_call_seq(method_name, call_seq)
  if (call_seq)
    fitred_call_seq = call_seq.gsub(/\s*\#.*/, '').strip

    defs_array = fitred_call_seq.split("\n") #"hack" to be sure that we match regexp till end

    # for each definition from possible multiline def
    defs_array.each do |item|
      m1, margs, mtails = _split_call_seq(method_name, item)
      if (margs)
        args = margs.strip
        if (args.empty?)
          return "()"
        else
          return args[0..0] == '(' ? args : " #{args}"
        end
      end
    end
  end
  "(...)"
end

REGEXP_METHOD_ARGS_OPTIONAL = /(\s*\(.*\))?/
REGEXP_METHOD_BLOCK_OPTIONAL = /(\{.*\})?/
REGEXP_METHOD_RETURN_VALUES_MANDATORY = /\s*[=-]>\s*\w+(\s*(\,|or)\s*\w+)*\n/

def _split_call_seq(method_name, metod_def_seq)
  # Debug output
  # puts "def_seq: [#{metod_def_seq}]\n\n"
  # puts "method_name: [#{method_name}]\n\n"

  operator_name_regexp = _convert_operator_method_to_regexp(method_name)
#if method is binary operator
  if operator_name_regexp
    # Debug output
    #puts "Bin Operator name: #{method_name}"
    #puts "Operator regexp: #{operator_name_regexp}"

    operator_regexp = /(\w+\s*#{operator_name_regexp}\s*)(\w+)(\s*)(#{REGEXP_METHOD_RETURN_VALUES_MANDATORY})/
    string_seq = "#{metod_def_seq}\n"
    if (!(string_seq =~ operator_regexp) || $2.empty?)
      return nil, nil, nil
    end
    # Debug output
    #p [$1, $2, $3, $4]

    m_def = _strip_if_not_null($1) # definition piece
    m_args = $2
    m_tail = _strip_if_not_null("#{$3}#{$4}") # tail piece
    return m_def, m_args, m_tail
  end

#if unary operator
  operator_name_regexp = _convert_unary_operator_method_to_regexp(method_name)
  if operator_name_regexp
    # Debug output
    #puts "Un Operator name: #{method_name}"

    operator_regexp_un = /(\s*#{operator_name_regexp}\s*)(\w+)(\s*)(#{REGEXP_METHOD_RETURN_VALUES_MANDATORY})/
    string_seq_un = "#{metod_def_seq}\n"

    if (!(string_seq_un =~ operator_regexp_un) || $2.empty?)
      return nil, nil, nil
    end
    # Debug output
    #p [$1, $2, $3, $4]

    m_def = _strip_if_not_null($1) # definition piece
    m_args = $2
    m_tail = _strip_if_not_null("#{$3}#{$4}") # tail piece
    return m_def, m_args, m_tail
  end
#if method is method


  # Patch method name for valid REGEXP form
  last = method_name.length - 1
  if method_name[last, last] == "?"
    method_name = "#{method_name[0..last-1]}\\?"
  end

  # puts "patched mname: [#{method_name}]\n\n"

  string1 = "#{metod_def_seq}\n"
  regexp1 = /(\w+\.#{method_name})#{REGEXP_METHOD_ARGS_OPTIONAL}(\s*)#{REGEXP_METHOD_BLOCK_OPTIONAL}(\s*)(#{REGEXP_METHOD_RETURN_VALUES_MANDATORY})/
  if (!(string1 =~ regexp1))
    return nil, nil, nil
  end

  # Debug output
  # p [$1, $2, $3, $4, $5, $6]

  m_def = _strip_if_not_null($1) # definition piece
  m_args = $2 ? $2.strip : ""
  m_tail = _strip_if_not_null("#{$3}#{$4}#{$5}#{$6}") # tail piece

  return m_def, m_args, m_tail
end

def  _convert_unary_operator_method_to_regexp(method_name)
  case method_name
  when "!", "~"
    method_name
  when "+@"
    "\\+"
  when "-@"
    "-"
  else
    nil
  end
end

def  _convert_operator_method_to_regexp(method_name)
  case method_name
  when  "[]"
    "\\[\\]"
  when "[]="
    "\\[\\]="
  when "**"
    "\\*\\*"
  when "+"
    "\\+"
  when "-"
    method_name
  when "*"
    "\\*"
  when "/", "%", ">>", "<<", "&"
    method_name
  when  "^"
    "\\^"
  when  "|"
    "\\|"
  when  "<=", "<", ">", ">=", "<=>", "==", "==="
    method_name
  when  "!="
    "\\!="
  when  "=~"
    method_name
  when  "!~"
    "\\!~"
  else
    nil
  end
end