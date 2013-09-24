# Created by IntelliJ IDEA.
# User: romeo
# Date: Nov 7, 2007
# Time: 10:08:04 PM
# To change this template use File | Settings | File Templates.

def colour(text, colour_code)
  return "#{colour_code}#{text}\e[0m"
end

def green(text); colour(text, "\e[32m"); end
def red(text); colour(text, "\e[31m"); end
def magenta(text); colour(text, "\e[35m"); end
def yellow(text); colour(text, "\e[33m"); end
def blue(text); colour(text, "\e[34m"); end

puts green("Colors: \ngreen")
puts red("red")
puts blue("blue")
puts magenta("magenta")
puts yellow("yellow")