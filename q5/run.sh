#!/bin/bash
date
set -x

GREEN='\033[0;32m'
NC='\033[0m'

echo "STRUCTURE_OPTION: ${STRUCTURE_OPTION:= concurrentHash synchronizedMap copyOnWriteList synchronizedList}"
echo "NUMBER_OF_THREADS: ${NUMBER_OF_THREADS:=1 1024 16384}"
echo "READ_RATE: ${READ_RATE:=0.1 0.5 0.9}"

# Compiling the stuff
javac Main.java

mkdir -p results/
for structure_option in ${STRUCTURE_OPTION};
do
    for number_of_threads in ${NUMBER_OF_THREADS};
    do
        for read_rate in ${READ_RATE};
        do
            echo -e "${GREEN}EXPERIMENT RUNNING: ${structure_option}-t${number_of_threads}-r${read_rate}${NC}"
            java Main ${structure_option} ${number_of_threads} ${read_rate} > results/${structure_option}-t${number_of_threads}-r${read_rate}.csv
        done;
    done;
done

# Cleaning the stuff
rm -rf *.class Main.java