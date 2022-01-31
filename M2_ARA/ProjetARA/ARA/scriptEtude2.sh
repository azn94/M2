#!/bin/bash

javac -Xlint:deprecation -d bin -classpath lib/djep-1.0.0.jar:lib/jep-2.3.0.jar:lib/peersim-1.0.5.jar:lib/peersim-doclet.jar src/projet/Etude2/*.java src/projet/util/Etude2/*.java 

FILE_PATH=configEtude2
OUTPUT_FILE=test.csv

# Etude 2
echo "time,nbMessages" > $OUTPUT_FILE

for i in {10..300..10};
      do
      for j in {0..50};
        do
          echo "network.size $i" > $FILE_PATH
          echo "simulation.endtime 5000000" >> $FILE_PATH
          echo "random.seed $j" >> $FILE_PATH
          echo "" >> $FILE_PATH
          echo "init.i Initialisation2" >> $FILE_PATH
          echo "" >> $FILE_PATH
          echo "protocol.multipaxos_protocol MultiPaxos_Protocol" >> $FILE_PATH
          echo "protocol.multipaxos_protocol.transport deadlytransport" >> $FILE_PATH
          echo "" >> $FILE_PATH
          echo "#protocol.applicative.MultiPaxos_Protocol" >> $FILE_PATH
          echo "" >> $FILE_PATH
          echo "" >> $FILE_PATH
          echo "protocol.transport UniformRandomTransport" >> $FILE_PATH
          echo "protocol.transport.mindelay 5" >> $FILE_PATH
          echo "protocol.transport.maxdelay 500" >> $FILE_PATH
          echo "" >> $FILE_PATH
          echo "" >> $FILE_PATH
          echo "protocol.deadlytransport DeadlyTransport" >> $FILE_PATH
          echo "protocol.deadlytransport.drop 0.1" >> $FILE_PATH
          echo "protocol.deadlytransport.faultynodes 1  # 1 pour activer les fautes / 0 sinon" >> $FILE_PATH
          echo "protocol.deadlytransport.transport transport" >> $FILE_PATH
          echo "" >> $FILE_PATH
          echo "" >> $FILE_PATH
          echo "control.endcontroler EndControler2" >> $FILE_PATH
          echo "control.endcontroler.multipaxos_protocol multipaxos_protocol" >> $FILE_PATH
          echo "control.endcontroler.at -1" >> $FILE_PATH
          echo "control.endcontroler.FINAL" >> $FILE_PATH
          java -cp bin/:lib/*: peersim.Simulator $FILE_PATH
        done
      done
rm $FILE_PATH
