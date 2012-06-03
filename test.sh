#!/bin/sh

CLASSPATH=target/classes:target/alternateLocation/commons-cli-1.1.jar
export CLASSPATH

java ru.mipt.archtoys.star.compiler.App -i program.calc -o program.star

java ru.mipt.archtoys.yapm.bt.Translator -i program.star -o program.yapm

java ru.mipt.archtoys.yapm.interpretator.App program.yapm