#!/bin/sh
java -jar ./lib/java-cup-11a.jar -parser C89Parser -symbols CTokenType < $1
mv ./CTokenType.java ./src/com/smwatt/comp/CTokenType.java
mv ./C89Parser.java ./src/com/smwatt/comp/C89Parser.java
