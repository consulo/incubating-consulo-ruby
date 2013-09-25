# Created by IntelliJ IDEA.
# User: oleg
# Date: Sep 26, 2007
# Time: 5:42:03 PM
# To change this template use File | Settings | File Templates.

#Extract BNF from ruby's parse.y
$INPUT = "/home/oleg/work/ruby-1.8.6/parse.y"
$OUTPUT = "grammar.txt"

### Reading text from input ############################################################################################
text=""
IO.foreach($INPUT) { |line| text+=line }


### Processing text ####################################################################################################

#Save anything between the first and second %%
text =~ /%%.*%%/m
text = $&
text = text.gsub(/%%/, '')	#Discard %%

#Replace '{' with LCURLY, and '}' with RCURLY
text = text.gsub(/'\{'/, 'LCURLY')
text = text.gsub(/'\}'/, 'RCURLY')

#Remove semantic actions
#Note: be careful with strings like "{{}}" (And that's why we need loop herre)
#Use ? to avoid greedy match.
while text =~ /\{/
	text = text.gsub(/\{[^\{]*?\}/m, '')
end

#Indent the result
text = text.gsub(/\s+$/, ' ')	#Remove spaces before line break and empty lines
text = text.gsub(/^\s+;/, "\t\t;\n");	#Add linebreak between rules


#Discard %prec
text = text.gsub(/%prec.*$/, '');

### Writing result to output ###########################################################################################
file_ruby_bnf = File.new($OUTPUT, "w")
file_ruby_bnf.puts text
file_ruby_bnf.close
