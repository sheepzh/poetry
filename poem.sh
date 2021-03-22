jarOptions="-Dfile.encoding=UTF-8 -DpoemDir=./data"
jarPath="./tool/target/tool.jar"

java $jarOptions -jar $jarPath $*
