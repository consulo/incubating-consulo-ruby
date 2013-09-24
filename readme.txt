1. Add idea.jar to IntelliJ IDEA SDK

2. Use this VM properties
for Plugin Run configuration:
-Didea.is.internal=true -Xmx256M -ea -XX:+HeapDumpOnOutOfMemoryError -XX:MaxPermSize=99m

for JUnit and RunParserOnRubyFiles:
-Didea.ruby.mock.sdk=~/work/ruby/dist/mockSdk -Didea.load.plugins=false -Xbootclasspath/p:$IDEA_HOME/lib/boot.jar -Xmx256M -ea

3. To build ruby.jar, set environmental variables in build.properties:
idea.home = IntelliJ IDEA home directory
JAVA_HOME = JDK Home directory
