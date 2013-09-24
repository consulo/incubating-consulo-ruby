# Created by IntelliJ IDEA.
# User: oleg, Roman Chernyatchik
# Date: Dec 10, 2007

=begin
 This is a stub file, used for indexing
=end

module ActionMailer
    class Base
        include TMail::Mail

        # E-mail subject string
        @subject    = '<%= class_name %>#<%= action %>'

        # A hash with values that are passed to e-mail template.
        @body       = {}

        # One recipient e-mail adress(string) or more(array of strings).
        @recipients = ''

        # From: header. One e-mail adress(string) or more(array of strings).
        @from       = ''
        
        # E-mail Date: header. Current time by default.
        @sent_on    = Time.now

        # Hash of header name => value pairs
        @headers    = {}
    end
end