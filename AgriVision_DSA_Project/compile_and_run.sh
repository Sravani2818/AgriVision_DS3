#!/bin/sh
mkdir -p out
javac -encoding UTF-8 -d out src/agrivision/*.java || exit 1
java -cp out agrivision.Main
