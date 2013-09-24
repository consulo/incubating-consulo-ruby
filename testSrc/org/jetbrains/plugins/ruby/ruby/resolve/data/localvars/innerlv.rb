# Test inner local variable with same name
aaa = "Test"
1..10.each do |aaa| 
    puts aa#caret#a
end