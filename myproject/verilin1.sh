#!/bin/sh

for i in $(seq 1 100)
do
	echo 'execution NO.'$i
	sh ./clean.sh
	javac -encoding UTF-8 -cp . ticketingsystem/GenerateHistory.java
	java -cp . ticketingsystem/GenerateHistory 64 10000 1 0 0 > history
	java -jar VeriLin.jar 64 history 1 failedHistory
done