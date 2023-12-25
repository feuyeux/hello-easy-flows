export JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home
mvn compile exec:java -Dexec.mainClass="org.feuyeux.workflow.HelloEasyFlows" -Dexec.args="Hello EasyFlows"