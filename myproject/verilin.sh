#!/bin/sh

#javac -encoding UTF-8 -cp . ticketingsystem/GenerateHistory.java
#java -cp . ticketingsystem/GenerateHistory 64 100000 0 0 0 > history
#java -Xss1024m -Xmx400g -jar VeriLin.jar 64 history 1 failedHistory
javac -encoding UTF-8 -cp . ticketingsystem/GenerateHistory.java
java -cp . ticketingsystem/GenerateHistory 4 1000 0 0 0 > history
java -Xss1024m -Xmx400g -jar VeriLin.jar 4 history 1 failedHistory
