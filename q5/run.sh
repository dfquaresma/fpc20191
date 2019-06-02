#!/bin/bash
date
set -x

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "EXPID_START: ${EXPID_START:=0}"
echo "EXPID_END: ${EXPID_END:=4}"
echo "STRUCTURE_OPTION: ${STRUCTURE_OPTION:= concurrentHash synchronizedMap copyOnWriteList synchronizedList}"
echo "NUMBER_OF_THREADS: ${NUMBER_OF_THREADS:= 1 128 512}"
echo "READ_RATE: ${READ_RATE:= 0 0.25 0.5 0.75 1}"

# Compiling the stuff
javac Main.java

mkdir -p results/
for expid in `seq ${EXPID_START} ${EXPID_END}`;
do
    for structure_option in ${STRUCTURE_OPTION};
    do
        for number_of_threads in ${NUMBER_OF_THREADS};
        do
            for read_rate in ${READ_RATE};
            do
                echo -e "${GREEN}EXPERIMENT RUNNING: ${structure_option}-t${number_of_threads}-r${read_rate}-e${expid}${NC}"
                java Main ${structure_option} ${number_of_threads} ${read_rate} > results/${structure_option}-t${number_of_threads}-r${read_rate}-e${expid}.csv
            done;
        done;
    done;
done

# Cleaning the stuff
rm -rf *.class Main.java