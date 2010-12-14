#!/bin/sh

mkdir -p bin
javac -d ./bin -sourcepath src -classpath lib/java-cup-11a-runtime.jar ./src/com/pag/*.java
